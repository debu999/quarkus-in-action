package org.doogle;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("")
public class GreetingResource {

    @ConfigProperty(name = "greeting")
    String greeting;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus In Action";
    }

    @GET
    @Path("/greeting")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> greeting() {
        return Map.of("greeting", greeting);
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> whoAmI(@Context SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal != null) {
            Log.infov("User: {0}", userPrincipal.getName());
            return Map.of("user", userPrincipal.getName());
        } else {
            Map<String, String> anonymousUser = Map.of("user", "anonymous");
            Log.error(anonymousUser);
            return anonymousUser;
        }
    }
}
