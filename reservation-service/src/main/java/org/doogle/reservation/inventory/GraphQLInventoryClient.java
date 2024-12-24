package org.doogle.reservation.inventory;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.eclipse.microprofile.graphql.Query;

@GraphQLClientApi(configKey = "inventory")
public interface GraphQLInventoryClient extends InventoryClient {

  @Query("cars")
  Uni<List<Car>> allCars();
}