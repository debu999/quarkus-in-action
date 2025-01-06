package org.doogle.rental.invoice.data;

import io.quarkus.arc.All;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class InvoiceConfirmation {

  public Invoice invoice;
  public boolean paid;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class Invoice {
    public boolean paid;
    public InvoiceReservation reservation;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static final class InvoiceReservation {
    public Long id;
    public String userId;
    public LocalDate startDay;
  }
}
