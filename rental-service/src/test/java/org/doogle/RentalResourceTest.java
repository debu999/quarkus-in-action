package org.doogle;

import static io.quarkus.test.junit.QuarkusMock.installMockForType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.time.Duration;
import java.time.LocalDate;
import org.doogle.rental.RentalResource;
import org.doogle.rental.reservation.Reservation;
import org.doogle.rental.reservation.ReservationClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class RentalResourceTest {

  @InjectKafkaCompanion
  KafkaCompanion kafkaCompanion;

  @Test
  public void testRentalProlongedInvoiceSend() {
    // stub the ReservationClient call
    Reservation reservation = new Reservation(LocalDate.now().minusDays(1));
    ReservationClient mock = mock(ReservationClient.class);
    when(mock.getById(1L)).thenReturn(Uni.createFrom().item(reservation));
    installMockForType(mock, ReservationClient.class, RestClient.LITERAL);
    // start new Rental for reservation with id 1
    given().contentType(ContentType.JSON) // Set the correct Content-Type
        .when().post("/rental/start/user123/1").then().statusCode(200);
    // end the with one prolonged day
    given().when().put("/rental/end/user123/1").then().statusCode(200)
        .body("active", is(false), "endDate", is(LocalDate.now().toString()));
    // verify that message is sent to the invoices-adjust Kafka topic
    ConsumerTask<String, String> invoiceAdjust = kafkaCompanion.consumeStrings()
        .fromTopics("invoices-adjust", 1).awaitNextRecord(Duration.ofSeconds(10));
    assertEquals(1, invoiceAdjust.count());
    assertTrue(invoiceAdjust.getFirstRecord().value()
        .contains("\"price\":" + RentalResource.STANDARD_PRICE_FOR_PROLONGED_DAY));
  }
}
