package org.doogle;

import static io.quarkus.test.junit.QuarkusMock.installMockForType;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.doogle.reservation.billing.Invoice;
import org.doogle.reservation.entity.Reservation;
import org.doogle.reservation.rental.Rental;
import org.doogle.reservation.rental.RentalClient;
import org.doogle.reservation.rest.ReservationResource;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
@ApplicationScoped
public class ReservationInvoiceProducerTest {

  private final Map<Integer, Invoice> receivedInvoices = new HashMap<>();
  private final AtomicInteger ids = new AtomicInteger(0);

  @Incoming("invoices-in")
  public void processInvoice(JsonObject json) {
    Invoice invoice = json.mapTo(Invoice.class);
    System.out.println("Received invoice " + invoice);
    receivedInvoices.put(ids.incrementAndGet(), invoice);
  }

  @Test
  public void testInvoiceProduced() throws Throwable {
    // Make a reservation request that sends the invoice to RabbitMQ
    Reservation reservation = new Reservation();
    reservation.carId = 1L;
    reservation.startDay = LocalDate.now();
    reservation.endDay = reservation.startDay;
    RentalClient mock = mock(RentalClient.class);
    when(mock.start("anonymous", 1L)).thenReturn(
        Uni.createFrom().item(new Rental("1", "anonymous", 1L, reservation.startDay)));
    installMockForType(mock, RentalClient.class, RestClient.LITERAL);
    given().body(reservation).contentType(MediaType.APPLICATION_JSON).when().post("/reservation")
        .then().statusCode(200);
    await().atMost(60, TimeUnit.SECONDS).until(() -> receivedInvoices.size() == 1);
    // Assert that the invoice message was received in this consumer
    assertEquals(1, receivedInvoices.size());
    assertEquals(ReservationResource.STANDARD_RATE_PER_DAY, receivedInvoices.get(1).price);
  }
}