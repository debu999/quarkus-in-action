package org.doogle.reservation.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.doogle.reservation.entity.Reservation;

@AllArgsConstructor
@Data
public class Invoice {

  public Reservation reservation;
  public double price;
  // all-arg constructor, toString
}