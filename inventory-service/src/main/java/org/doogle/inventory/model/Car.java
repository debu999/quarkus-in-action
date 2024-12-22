package org.doogle.inventory.model;

import org.eclipse.microprofile.graphql.Description;

public record Car(@Description("The id of the car") Long id, String licensePlateNumber,
                  String manufacturer, String model) {
  public Car withId(Long newId) {
    return new Car(newId, licensePlateNumber, manufacturer, model);
  }
}
