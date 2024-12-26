package org.doogle.billing.data;

import lombok.Data;
import org.doogle.billing.model.Invoice;

@Data
public class InvoiceConfirmation {

  public Invoice invoice;
  public boolean paid;
// all-arg constructor, toString
}