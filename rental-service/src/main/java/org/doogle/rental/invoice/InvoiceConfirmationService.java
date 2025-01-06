package org.doogle.rental.invoice;

import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.common.reactive.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.doogle.rental.entity.Rental;
import org.doogle.rental.invoice.data.InvoiceConfirmation;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class InvoiceConfirmationService {
  @Incoming("invoices-confirmations")
  @Blocking
  public void invoicePaid(InvoiceConfirmation invoiceConfirmation) {
    Log.info("Received invoice confirmation " + invoiceConfirmation);
    if (!invoiceConfirmation.paid) {
      Log.warn("Received unpaid invoice confirmation - " + invoiceConfirmation);
      // retry handling omitted
    }
    InvoiceConfirmation.InvoiceReservation reservation = invoiceConfirmation.invoice.reservation;
    var rentalUni =
        Rental.findByUserAndReservationIdsOptional(reservation.userId, reservation.id)
            .flatMap(
                Unchecked.function(
                    optionalRental -> {
                      if (optionalRental.isPresent()) {
                        Rental rental = optionalRental.get();
                        // mark the already started rental as paid
                        rental.paid = true;
                        return Panache.withTransaction(rental::<Rental>update).log();
                      } else {
                        // create new rental starting in the future
                        Rental rental = new Rental();
                        rental.userId = reservation.userId;
                        rental.reservationId = reservation.id;
                        rental.startDate = reservation.startDay;
                        rental.active = false;
                        rental.paid = true;
                        return Panache.withTransaction(rental::<Rental>persist).log();
                      }
                    }));
    var rentalEntity = rentalUni.await().indefinitely();
    Log.info("Rental updated " + rentalEntity);
  }
}
