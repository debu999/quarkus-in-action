package org.doogle.billing;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.doogle.billing.data.ReservationInvoice;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class InvoiceProcessor {

  @Incoming("invoices")
  @Outgoing("invoices-requests")
  public Message<Invoice> processInvoice(Message<JsonObject> message) {
    ReservationInvoice invoiceMessage = message.getPayload().mapTo(ReservationInvoice.class);
    Invoice.Reservation reservation = invoiceMessage.reservation;
    Invoice invoice = new Invoice(invoiceMessage.price, false, reservation);
    invoice.persist();
    Log.info("Processing invoice: " + invoice);
    return Message.of(invoice, () -> message.ack());
  }
}