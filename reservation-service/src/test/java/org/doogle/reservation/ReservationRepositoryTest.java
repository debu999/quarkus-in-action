package org.doogle.reservation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReservationRepositoryTest {

  @Inject
  ReservationsRepository reservatinsRepository;

  @Test
  public void testCreateReservation() {
    Reservation reservation = new Reservation(null, null, 384L, LocalDate.now().plusDays(5),
        LocalDate.now().plusDays(12));
    reservation = reservatinsRepository.save(reservation);
    Assertions.assertNotNull(reservation.id());
    Assertions.assertTrue(reservatinsRepository.findAll().contains(reservation));
  }
}