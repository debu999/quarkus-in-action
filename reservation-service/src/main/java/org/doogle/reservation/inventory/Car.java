package org.doogle.reservation.inventory;

public record Car(

    Long id, String licensePlateNumber, String manufacturer, String model) {

  public Car withId(Long id) {
    return new Car(id, licensePlateNumber, manufacturer, model);
  }

}
