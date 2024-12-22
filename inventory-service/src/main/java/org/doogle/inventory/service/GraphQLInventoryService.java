package org.doogle.inventory.service;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.doogle.inventory.db.CarInventory;
import org.doogle.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class GraphQLInventoryService {

  @Inject
  CarInventory inventory;

  @Query
  public List<Car> cars() {
    return inventory.getCars();
  }

  @Mutation
  public Car register(Car car) {
    var carId = CarInventory.ids.incrementAndGet();
    inventory.getCars().add(car.withId(carId));
    return car;
  }

  @Mutation
  public boolean remove(String licensePlateNumber) {
    List<Car> cars = inventory.getCars();
    Optional<Car> toBeRemoved = cars.stream()
        .filter(car -> car.licensePlateNumber().equals(licensePlateNumber)).findAny();
    return toBeRemoved.map(cars::remove).orElse(false);
  }
}