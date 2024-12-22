package org.doogle.reservation.rest;

import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.doogle.reservation.Reservation;
import org.doogle.reservation.ReservationsRepository;
import org.doogle.reservation.inventory.Car;
import org.doogle.reservation.inventory.InventoryClient;
import org.doogle.reservation.rental.Rental;
import org.doogle.reservation.rental.RentalClient;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  private final ReservationsRepository reservationsRepository;
  private final InventoryClient inventoryClient;
  private final RentalClient rentalClient;

  public ReservationResource(ReservationsRepository reservations, InventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.reservationsRepository = reservations;
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  @GET
  @Path("availability")
  public Collection<Car> availability(
      @RestQuery @Parameter(name = "startDate", example = "2024-12-21") LocalDate startDate,
      @RestQuery @Parameter(name = "endDate", example = "2024-12-22") LocalDate endDate) {
// obtain all cars from inventory
    List<Car> availableCars = inventoryClient.allCars();
    // create a map from id to car
    Map<Long, Car> carsById = new HashMap<>();
    for (Car car : availableCars) {
      carsById.put(car.id(), car);
    }
// get all current reservations
    List<Reservation> reservations = reservationsRepository.findAll();
// for each reservation, remove the car from the map
    for (Reservation reservation : reservations) {
      if (reservation.isReserved(startDate, endDate)) {
        carsById.remove(reservation.carId());
      }
    }
    return carsById.values();
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Reservation make(Reservation reservation) {
//    return reservationsRepository.save(reservation);
    Reservation result = reservationsRepository.save(reservation);
    // this is just a dummy value for the time being
    String userId = "x";
    if (reservation.startDay().equals(LocalDate.now())) {
      Rental rental = rentalClient.start(userId, result.id());
      Log.info("Successfully started rental " + rental);
    }
    return result;
  }

}