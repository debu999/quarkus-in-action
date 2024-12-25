package org.doogle.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.doogle.inventory.model.Car;
import org.doogle.inventory.model.CarResponse;
import org.doogle.inventory.model.InsertCarRequest;
import org.doogle.inventory.model.InventoryService;
import org.doogle.inventory.model.RemoveCarRequest;

@GrpcService
public class GrpcInventoryService implements InventoryService {
//
//  @Inject
//  CarInventory inventory;

  @Override
  public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {
    return requests.map(request -> {
      Car c = new Car();
      c.licensePlateNumber = request.getLicensePlateNumber();
      c.manufacturer = request.getManufacturer();
      c.model = request.getModel();
      return c;
    }).call(car -> {
      Log.info("Persisting " + car);
      return Panache.withTransaction(car::<Car>persist).log();
    }).map(car -> CarResponse.newBuilder().setLicensePlateNumber(car.licensePlateNumber)
        .setManufacturer(car.manufacturer).setModel(car.model).setId(car.id).build());
  }

  @Override
  @WithTransaction
  public Uni<CarResponse> remove(RemoveCarRequest request) {
    var carUni = Car.findByLicensePlateNumberOptional(request.getLicensePlateNumber()).log("CAR");
    return carUni.flatMap(car -> {
      if (car != null) {
        return car.delete().replaceWith(
            CarResponse.newBuilder().setLicensePlateNumber(car.licensePlateNumber)
                .setManufacturer(car.manufacturer).setModel(car.model).setId(car.id).build());
      }
      return Uni.createFrom().nullItem();
    });
  }
}