package org.doogle.reservation;

import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import org.doogle.reservation.entity.Reservation;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReservationPersistenceTest {

  @Test
  @RunOnVertxContext
  public void testCreateReservation(TransactionalUniAsserter asserter) {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(12);
    reservation.carId = 384L;
    asserter.execute(() -> Reservation.save(reservation));
    asserter.assertNotNull(() -> Uni.createFrom().item(reservation.id));
    asserter.assertEquals(Reservation::countAll, 1L);
    asserter.assertEquals(
        () -> Reservation.findByIdValue(reservation.id).map(r -> r).map(r1 -> r1.id),
        1L);
  }
}