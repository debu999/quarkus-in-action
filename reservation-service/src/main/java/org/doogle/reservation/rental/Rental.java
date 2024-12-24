package org.doogle.reservation.rental;

import java.time.LocalDate;

public record Rental(String id, String userId, Long reservationId, LocalDate startDate) {
}
