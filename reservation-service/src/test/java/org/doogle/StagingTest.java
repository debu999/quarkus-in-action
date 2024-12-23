package org.doogle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.doogle.reservation.inventory.Car;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestProfile(RunWithStaging.class)
@TestMethodOrder(OrderAnnotation.class)
public class StagingTest {


  @Test
  @Order(1)
  void testCarCreation() {
    Car myCar = new Car(1L, "ABC123", "Toyota", "Corolla");
    assertEquals(1L, myCar.id());
    assertEquals("ABC123", myCar.licensePlateNumber());
    assertEquals("Toyota", myCar.manufacturer());
    assertEquals("Corolla", myCar.model());
  }

  @Test
  @Order(2)
  void testCarUpdate() {
    Car myCar = new Car(1L, "ABC123", "Toyota", "Corolla");
    Car updatedCar = myCar.withId(2L);
    assertEquals(2L, updatedCar.id());
    assertEquals("ABC123", updatedCar.licensePlateNumber());
    assertEquals("Toyota", updatedCar.manufacturer());
    assertEquals("Corolla", updatedCar.model());
  }
}