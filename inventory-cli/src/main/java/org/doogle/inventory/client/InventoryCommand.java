package org.doogle.inventory.client;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import java.util.stream.IntStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.doogle.inventory.model.CarResponse;
import org.doogle.inventory.model.InsertCarRequest;
import org.doogle.inventory.model.InventoryService;
import org.doogle.inventory.model.RemoveCarRequest;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

  private static final String USAGE =
      "Usage: inventory <add>|<remove> " + "<license plate number> <manufacturer> <model>";

  @GrpcClient("inventory")
  InventoryService inventory;

  @Override
  public int run(String... args) {
    String action = args.length > 0 ? args[0] : null;

    if ("add".equals(action) && args.length >= 4 && (args.length - 1) % 3 == 0) {
      Log.infov("args: {0}", "add", args);
      var carRequests = IntStream.range(0, (args.length - 1) / 3).boxed().map(
          idx -> InsertCarRequest.newBuilder().setLicensePlateNumber(args[1 + idx * 3])
              .setManufacturer(args[2 + idx * 3]).setModel(args[1 + idx * 3]).build()).toList();
      Multi<InsertCarRequest> multiRequests = Multi.createFrom().iterable(carRequests);
      add(multiRequests);
      return 0;
    } else if ("remove".equals(action) && args.length >= 2) {
      IntStream.range(0, args.length - 1).forEach(idx -> remove(args[1 + idx]));
      return 0;
    }
    System.err.println(USAGE);
    return 1;
  }

  public void add(Multi<InsertCarRequest> carRequestMulti) {
    Log.info("in add method adding details...");

    Multi<CarResponse> response = inventory.add(carRequestMulti);
    var carResponseList = response.onFailure().invoke(ExceptionUtils::getStackTrace).collect()
        .asList().await().indefinitely();
    Log.infov("Added Cars... {0}", carResponseList);
  }

  public void remove(String licensePlateNumber) {
    var removedCar = inventory.remove(
            RemoveCarRequest.newBuilder().setLicensePlateNumber(licensePlateNumber).build())
        .invoke(carResponse -> System.out.println("Removed car " + carResponse)).await()
        .indefinitely();
    Log.infov("Removed Car... {0}", removedCar);
  }
}