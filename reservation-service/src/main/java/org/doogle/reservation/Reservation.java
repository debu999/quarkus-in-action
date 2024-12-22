package org.doogle.reservation;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.LocalDate;

@RecordBuilder
public record Reservation(Long id, String userId, Long carId, LocalDate startDay,
                          LocalDate endDay) implements ReservationBuilder.With {

  /**
   * Check if the given duration overlaps with this reservation
   *
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return (!(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay)));
  }
}