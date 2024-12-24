package org.doogle.rental.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoEntity(database = "rental")
public class Rental extends ReactivePanacheMongoEntity {

  public String userId;
  public Long reservationId;
  public LocalDate startDate;
  public LocalDate endDate;
  @BsonProperty("active_indicator")
  public boolean active;

  public static Uni<Optional<Rental>> findByUserAndReservationIdsOptional(String userId,
      Long reservationId) {
    return find("userId = ?1 and reservationId = ?2", userId, reservationId).firstResultOptional();
  }

  public static Uni<List<Rental>> listActive() {
    return list("active", true);
  }

  public Uni<Rental> save() {
    return persistOrUpdate();
  }

}