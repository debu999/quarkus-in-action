package org.doogle.inventory.model;
//
//import org.eclipse.microprofile.graphql.Description;
//
//public record Car(@Description("The id of the car") Long id, String licensePlateNumber,
//                  String manufacturer, String model) {
//  public Car withId(Long newId) {
//    return new Car(newId, licensePlateNumber, manufacturer, model);
//  }
//}

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Car extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String licensePlateNumber;
  public String manufacturer;
  public String model;

  public static Uni<Car> findByLicensePlateNumberOptional(String licensePlateNumber) {
    return find("licensePlateNumber", licensePlateNumber).firstResult();
  }
}