package org.doogle.reservation.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Reservation extends PanacheEntity {

  public String userId;
  public Long carId;
  public LocalDate startDay;
  public LocalDate endDay;

  public static Uni<List<Reservation>> findByCar(Long carId) {
    return list("carId", carId);
  }

  public static Uni<Reservation> findByIdValue(Long id) {
    return findById(id);
  }

  public static Uni<Reservation> save(Reservation reservation) {
    return persist(reservation).replaceWith(reservation);
  }

  public static Uni<Long> countAll() {
    return count();
  }

  /**
   * Check if the given duration overlaps with this reservation
   *
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return (!(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay)));
  }

  @Override
  public String toString() {
    return "Reservation{" + "userId='" + userId + '\'' + ", carId=" + carId + ", startDay="
        + startDay + ", endDay=" + endDay + ", id=" + id + '}';
  }
}