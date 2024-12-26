package org.doogle.reservation.billing;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BillingService {

  @Incoming("invoices-in")
  public void processInvoice(JsonObject jsonObject) {
    Invoice invoice = jsonObject.mapTo(Invoice.class);
    Log.infov("Processing received invoice: {0}", invoice);
  }
}