package org.doogle.billing.data;

import lombok.Data;
import org.doogle.billing.model.Invoice;

@Data
public class ReservationInvoice {

  public Invoice.Reservation reservation;
  public double price;
}