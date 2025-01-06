package org.doogle.rental;

import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.common.reactive.Panache;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.doogle.rental.billing.InvoiceAdjust;
import org.doogle.rental.entity.Rental;
import org.doogle.rental.reservation.ReservationClient;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Path("/rental")
public class RentalResource {

  public static final double STANDARD_REFUND_RATE_PER_DAY = -10.99;
  public static final double STANDARD_PRICE_FOR_PROLONGED_DAY = 25.99;
  private final AtomicLong id = new AtomicLong(0);

  @Inject @RestClient ReservationClient reservationClient;

  @Inject
  @Channel("invoices-adjust")
  MutinyEmitter<InvoiceAdjust> adjustmentEmitter;

  //  @Outgoing("invoices-adjust")
  //  public Multi<InvoiceAdjust> sendInvoiceAdjustment() {
  //    return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
  //        .map(x -> new InvoiceAdjust("A", "A", LocalDate.now(), 1.11));
  //  }

  //  @Outgoing("invoices-adjust-out")
  //  public Uni<InvoiceAdjust> sendInvoiceAdjustment(InvoiceAdjust invoiceAdjust) {
  //    return Uni.createFrom().item(invoiceAdjust);
  //  }

  @Incoming("invoices-adjust-in")
  public void processInvoice(ConsumerRecord<String, InvoiceAdjust> record) {
    System.out.println("Processing received invoice: " + record);
  }

  @Path("/start/{userId}/{reservationId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Rental> start(String userId, Long reservationId) {
    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    var rentalUni =
        Rental.findByUserAndReservationIdsOptional(userId, reservationId)
            .flatMap(
                Unchecked.function(
                    optionalRental -> {
                      if (optionalRental.isPresent()) {
                        Rental rental = optionalRental.get();
                        // mark the already started rental as paid
                        rental.active = true;
                        return Panache.withTransaction(rental::<Rental>update).log();
                      } else {
                        // create new rental starting in the future
                        Rental rental = new Rental();
                        rental.userId = userId;
                        rental.reservationId = reservationId;
                        rental.startDate = LocalDate.now();
                        rental.active = true;
                        return Panache.withTransaction(rental::<Rental>persist).log("RENTAL");
                      }
                    }));
    return rentalUni.log("RENTAL1");
  }

  private double computePrice(LocalDate endDate, LocalDate today) {
    return endDate.isBefore(today)
        ? ChronoUnit.DAYS.between(endDate, today) * STANDARD_PRICE_FOR_PROLONGED_DAY
        : ChronoUnit.DAYS.between(today, endDate) * STANDARD_REFUND_RATE_PER_DAY;
  }

  @PUT
  @Path("/end/{userId}/{reservationId}")
  public Uni<Rental> end(String userId, Long reservationId) {
    Log.infof("Ending rental for %s with reservation %s", userId, reservationId);
    var rentalUni =
        Rental.findByUserAndReservationIdsOptional(userId, reservationId)
            .flatMap(
                Unchecked.function(
                    optionalRental -> {
                      if (optionalRental.isPresent()) {
                        Rental rental = optionalRental.get();
                        rental.endDate = LocalDate.now();
                        rental.active = false;
                        return Panache.withTransaction(rental::save).log();
                      } else {
                        throw new NotFoundException("Rental not found");
                      }
                    }))
            .invoke(
                r -> {
                  if (!r.paid) {
                    Log.warn("Rental is not paid: " + r);
                  }
                });
    var reservation = reservationClient.getById(reservationId);
    return rentalUni.call(
        r ->
            reservation.call(
                res -> {
                  LocalDate today = LocalDate.now();
                  if (!res.endDay().isEqual(today)) {
                    return adjustmentEmitter.send(
                        new InvoiceAdjust(
                            r.id.toString(), userId, today, computePrice(res.endDay(), today)));
                  }
                  return Uni.createFrom().nullItem();
                }));
  }

  @GET
  public Uni<List<Rental>> list() {
    return Rental.listAll();
  }

  @GET
  @Path("/active")
  public Uni<List<Rental>> listActive() {
    return Rental.listActive();
  }

  @GET
  @Path("/virtualThread")
  @RunOnVirtualThread
  public String virtualThread() {
    var result = "Running on " + Thread.currentThread().getName();
    Log.info(result);
    return result;
  }
}
