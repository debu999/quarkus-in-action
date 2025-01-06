package org.doogle.inventory.health;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.health.api.Wellness;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import org.doogle.inventory.model.Car;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Wellness
public class CarCountCheck implements AsyncHealthCheck {

  @Override
  public Uni<HealthCheckResponse> call() {
    var carsCount = Panache.withSession(() -> Car.findAll().count());
    return carsCount
        .map(
            cars -> {
              boolean wellnessStatus = cars > 0;
              return HealthCheckResponse.builder()
                  .name("car-count-check")
                  .status(wellnessStatus)
                  .withData("cars-count", cars)
                  .build();
            })
        .onItem()
        .delayIt()
        .by(Duration.ofMillis(10));
  }
}
