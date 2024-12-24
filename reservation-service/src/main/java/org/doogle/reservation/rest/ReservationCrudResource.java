package org.doogle.reservation.rest;

import io.quarkus.hibernate.reactive.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;
import org.doogle.reservation.entity.Reservation;

@ResourceProperties(path = "/admin/reservation")
public interface ReservationCrudResource extends PanacheEntityResource<Reservation, Long> {

}