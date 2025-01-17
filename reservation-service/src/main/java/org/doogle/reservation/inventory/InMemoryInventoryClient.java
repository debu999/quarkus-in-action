package org.doogle.reservation.inventory;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class InMemoryInventoryClient implements InventoryClient {

  private static final List<Car> ALL_CARS = List.of(new Car(1L, "ABC-123", "Toyota", "Corolla"),
      new Car(2L, "ABC-987", "Honda", "Jazz"), new Car(3L, "XYZ-123", "Renault", "Clio"),
      new Car(4L, "XYZ-987", "Ford", "Focus"));

  @Override
  public Uni<List<Car>> allCars() {
    return Uni.createFrom().item(ALL_CARS);
  }
}