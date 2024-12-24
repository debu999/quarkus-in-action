package org.doogle.rental;

import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.common.reactive.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.doogle.rental.entity.Rental;

@Path("/rental")
public class RentalResource {

  private final AtomicLong id = new AtomicLong(0);

  @Path("/start/{userId}/{reservationId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Rental> start(String userId, Long reservationId) {
    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    Rental rental = new Rental();
    rental.userId = userId;
    rental.reservationId = reservationId;
    rental.startDate = LocalDate.now();
    rental.active = true;
    return Panache.withTransaction(rental::save).log("RENTAL");
  }

  @PUT
  @Path("/end/{userId}/{reservationId}")
  public Uni<Rental> end(String userId, Long reservationId) {
    Log.infof("Ending rental for %s with reservation %s", userId, reservationId);
    return Rental.findByUserAndReservationIdsOptional(userId, reservationId)
        .flatMap(Unchecked.function(optionalRental -> {
          if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.endDate = LocalDate.now();
            rental.active = false;
            return Panache.withTransaction(rental::save).log();
          } else {
            throw new NotFoundException("Rental not found");
          }
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
}