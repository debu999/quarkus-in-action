package org.doogle.users.model;

import java.time.LocalDate;

public record Reservation(Long id, String userId, Long carId, LocalDate startDay,
                          LocalDate endDay) {
  public static Reservation fromCarIdStartDtEndDt(Long carId, LocalDate startDay, LocalDate endDay) {
    return new Reservation(null, null, carId, startDay, endDay);
  }
}