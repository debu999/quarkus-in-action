package org.doogle.reservation.billing;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BillingService {

  @Incoming("invoices")
  public void processInvoice(Invoice invoice) {
    System.out.println("Processing received invoice: " + invoice);
  }
}