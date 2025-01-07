package org.doogle.inventory.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.doogle.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@WithTransaction
@ApplicationScoped
public class GraphQLInventoryService {
  //
  //  @Inject
  //  CarInventory inventory;

  @Inject
  MeterRegistry registry;

  @Query
  public Uni<List<Car>> cars() {
    return Car.listAll();
  }

  @Mutation
  @Counted(
      description = "How many cars are registered",
//      value = "register",
      extraTags = {"register", "counter", "extra", "annotated"})
  public Uni<Car> register(Car car) {
    return car.<Car>persist().log();
  }

  @Mutation
  public Uni<Boolean> remove(String licensePlateNumber) {
    var carUni = Car.findByLicensePlateNumberOptional(licensePlateNumber).log("CAR");

    return carUni.flatMap(
        car -> {
          if (car != null) {
            return car.delete().replaceWith(true);
          }
          return Uni.createFrom().item(false);
        });
  }
}
