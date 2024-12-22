package org.doogle.reservation;

import java.util.List;

public interface ReservationsRepository {

  List<Reservation> findAll();

  Reservation save(Reservation reservation);
}