package org.doogle.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import java.util.Optional;
import org.doogle.inventory.db.CarInventory;
import org.doogle.inventory.model.Car;
import org.doogle.inventory.model.CarResponse;
import org.doogle.inventory.model.InsertCarRequest;
import org.doogle.inventory.model.InventoryService;
import org.doogle.inventory.model.RemoveCarRequest;

@GrpcService
public class GrpcInventoryService implements InventoryService {

  @Inject
  CarInventory inventory;

  @Override
  public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {
    return requests.map(
        request -> new Car(CarInventory.ids.incrementAndGet(), request.getLicensePlateNumber(),
            request.getManufacturer(), request.getModel())).invoke(car -> {
      Log.info("Persisting " + car);
      inventory.getCars().add(car);
    }).map(car -> CarResponse.newBuilder().setLicensePlateNumber(car.licensePlateNumber())
        .setManufacturer(car.manufacturer()).setModel(car.model()).setId(car.id()).build());
  }

  @Override
  public Uni<CarResponse> remove(RemoveCarRequest request) {
    Optional<Car> optionalCar = inventory.getCars().stream()
        .filter(car -> request.getLicensePlateNumber().equals(car.licensePlateNumber()))
        .findFirst();
    if (optionalCar.isPresent()) {
      Car removedCar = optionalCar.get();
      inventory.getCars().remove(removedCar);
      return Uni.createFrom().item(
          CarResponse.newBuilder().setLicensePlateNumber(removedCar.licensePlateNumber())
              .setManufacturer(removedCar.manufacturer()).setModel(removedCar.model())
              .setId(removedCar.id()).build());
    }
    return Uni.createFrom().nullItem();
  }
}