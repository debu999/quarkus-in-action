package org.doogle.rental.reservation;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;

@RegisterRestClient(configKey = "reservation")
@Path("/admin/reservation")
public interface ReservationClient {

  @GET
  @Path("/{id}")
  Uni<Reservation> getById(@RestPath Long id);
}