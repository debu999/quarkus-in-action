package org.doogle.billing;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;
import org.doogle.billing.data.InvoiceConfirmation;
import org.doogle.billing.model.Invoice;
import org.doogle.billing.model.InvoiceAdjust;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class PaymentRequester {

  private final Random random = new Random();

  @Incoming("invoices-requests")
  @Outgoing("invoices-confirmations")
  @Broadcast
  @Blocking
  public InvoiceConfirmation requestPayment(Invoice invoice) {
    payment(invoice.reservation.userId, invoice.totalPrice, invoice);
    invoice.paid = true;
    invoice.update();
    Log.infof("Invoice %s is paid.", invoice);
    return new InvoiceConfirmation(invoice, true);
  }

  private void payment(String user, double price, Object data) {
    Log.infof("Request for payment user: %s, price: %f, data: %s", user, price, data);
    try {
      Thread.sleep(random.nextInt(1000, 5000));
    } catch (InterruptedException e) {
      Log.error("Sleep interrupted.", e);
    }
  }

  // uncomment in order to consume confirmation here
  @Incoming("invoices-confirmations")
  public void consume(InvoiceConfirmation invoiceConfirmation) {
    Log.infov("invoice Confirmation Received {0}", invoiceConfirmation);
  }

  @Incoming("invoices-adjust")
  @Blocking
  @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
  public void requestAdjustment(InvoiceAdjust invoiceAdjust) {
    Log.info("Received invoice adjustment: " + invoiceAdjust);
    payment(invoiceAdjust.userId, invoiceAdjust.price, invoiceAdjust);
    invoiceAdjust.paid = true;
    invoiceAdjust.persist();
    Log.infof("Invoice adjustment %s is paid.", invoiceAdjust);
  }
}
