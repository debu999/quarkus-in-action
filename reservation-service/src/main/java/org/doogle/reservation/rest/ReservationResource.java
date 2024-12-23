package org.doogle.reservation.rest;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.doogle.reservation.entity.Reservation;
import org.doogle.reservation.inventory.Car;
import org.doogle.reservation.inventory.GraphQLInventoryClient;
import org.doogle.reservation.rental.RentalClient;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  //  private final ReservationsRepository reservationsRepository;
  private final RentalClient rentalClient;
  private final GraphQLInventoryClient inventoryClient;

  @Inject
  SecurityContext context;

  public ReservationResource(
//      ReservationsRepository reservationsRepository,
      @RestClient RentalClient rentalClient,
      @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient) {
//    this.reservationsRepository = reservationsRepository;
    this.rentalClient = rentalClient;
    this.inventoryClient = inventoryClient;
  }

  @GET
  @Path("availability")
  public Uni<Collection<Car>> availability(
      @RestQuery @Parameter(name = "startDate", example = "2024-12-21") LocalDate startDate,
      @RestQuery @Parameter(name = "endDate", example = "2024-12-22") LocalDate endDate) {
    // obtain all cars from inventory
    var availableCars = inventoryClient.allCars();
    // create a map from id to car
    var carMap = availableCars.map(c -> c.stream().collect(Collectors.toMap(Car::id, c1 -> c1)));
    // get all current reservations
    Uni<List<Reservation>> reservations = Reservation.listAll();
    // for each reservation, remove the car from the map
    return Uni.combine().all().unis(carMap, reservations).asTuple().map(tuple -> {
      var cmap = tuple.getItem1();
      var res = tuple.getItem2();
      res.stream().forEach(reservation -> {
        if (reservation.isReserved(startDate, endDate)) {
          cmap.remove(reservation.carId);
        }
      });
      return cmap.values();
    });
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Uni<Reservation> make(Reservation reservation) {
//    return reservationsRepository.save(reservation);
    var userId =
        context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonymous";
    //    reservation = reservation.withUserId(userId);
    reservation.userId = userId;
    //    Reservation result = reservationsRepository.save(reservation);
    return Panache.withTransaction(reservation::<Reservation>persist).log()
        .invoke(r1 -> Log.info("Successfully reserved reservation " + r1)).call(r2 -> {
          // this is just a dummy value for the time being
          //    String userId = "x";
          if (r2.startDay.equals(LocalDate.now())) {
            return rentalClient.start(userId, r2.id)
                .invoke(rental -> Log.info("Successfully started rental " + rental))
                .replaceWith(r2);
          }
          return Uni.createFrom().item(r2);

        });
  }

  @GET
  @Path("all")
  public Uni<Collection<Reservation>> allReservations() {
    String userId =
        context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : null;
    return Reservation.<Reservation>listAll().map(
        reservations -> reservations.stream().filter(r -> userId == null || userId.equals(r.userId))
            .toList());
  }

}