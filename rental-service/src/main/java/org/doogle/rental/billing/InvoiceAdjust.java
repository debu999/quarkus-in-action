package org.doogle.rental.billing;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvoiceAdjust {

  public String rentalId;
  public String userId;
  public LocalDate actualEndDate;
  public double price;

}