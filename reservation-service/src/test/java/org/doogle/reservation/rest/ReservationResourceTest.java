package org.doogle.reservation.rest;

import static io.quarkus.test.junit.QuarkusMock.installMockForType;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.logging.Log;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import org.doogle.reservation.Reservation;
import org.doogle.reservation.inventory.Car;
import org.doogle.reservation.inventory.GraphQLInventoryClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReservationResourceTest {

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource
  URL reservationResource;

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource("availability")
  URL availability;

  @Test
  public void testReservationIds() {
    Reservation reservation = new Reservation(null, null, 12345L, LocalDate.parse("2025-03-20"),
        LocalDate.parse("2025-03-29"));
    RestAssured.given().contentType(ContentType.JSON).body(reservation).when()
        .post(reservationResource).then().statusCode(200).body("id", notNullValue());
  }

  // uses mocks
  @DisabledOnIntegrationTest(forArtifactTypes = DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
  @Test
  public void testMakingAReservationAndCheckAvailability() {
    GraphQLInventoryClient inventoryClientMock = mock(GraphQLInventoryClient.class);
    Car peugeot = new Car(1L, "ABC 123", "Peugeot", "406");
    when(inventoryClientMock.allCars()).thenReturn(
        Uni.createFrom().item(Collections.singletonList(peugeot)));
    installMockForType(inventoryClientMock, GraphQLInventoryClient.class);
    String startDate = "2022-01-01";
    String endDate = "2022-01-10";
    // Get the list of available cars for our requested timeslot
    Car[] cars = RestAssured.given().queryParam("startDate", startDate)
        .queryParam("endDate", endDate).when().get(availability).then().statusCode(200).extract()
        .as(Car[].class);
    Log.info(cars);
  }
}