package org.doogle.billing.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Invoice extends PanacheMongoEntity {

  public double totalPrice;
  public boolean paid;
  public Reservation reservation;

  @Data
  public static final class Reservation {
    public Long id;
    public String userId;
    public Long carId;
    public LocalDate startDay;
    public LocalDate endDay;
  }
}
