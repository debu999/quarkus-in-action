package org.doogle.inventory.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.doogle.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@WithTransaction
public class GraphQLInventoryService {
//
//  @Inject
//  CarInventory inventory;

  @Query
  public Uni<List<Car>> cars() {
    return Car.listAll();
  }

  @Mutation
  public Uni<Car> register(Car car) {
    return car.persist().map(c -> (Car) c).log();
  }

  @Mutation
  public Uni<Boolean> remove(String licensePlateNumber) {
    var carUni = Car.findByLicensePlateNumberOptional(licensePlateNumber).log("CAR");

    return carUni.flatMap(car -> {
      if (car != null) {
        return car.delete().replaceWith(true);
      }
      return Uni.createFrom().item(false);
    });
  }
}