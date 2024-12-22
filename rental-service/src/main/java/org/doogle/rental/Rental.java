package org.doogle.rental;

import java.time.LocalDate;

public record Rental(Long id, String userId, Long reservationId, LocalDate startDate) {
}
