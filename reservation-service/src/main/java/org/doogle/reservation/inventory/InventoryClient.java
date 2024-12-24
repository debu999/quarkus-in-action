package org.doogle.reservation.inventory;

import io.smallrye.mutiny.Uni;
import java.util.List;

public interface InventoryClient {

  Uni<List<Car>> allCars();
}