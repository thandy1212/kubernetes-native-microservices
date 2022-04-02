package quarkus.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/bank")
public class BankResource {

  @Inject
  BankSupportConfigMapping configMapping;
  @ConfigProperty(name = "bank.name", defaultValue = "Bank of Default")
  String name;

  @ConfigProperty(name = "app.mobileBanking")
  Optional<Boolean> mobileBanking;

  @ConfigProperties(prefix = "bank-support")
  BankSupportConfig supportConfig;

  @ConfigProperty(name = "username")
  String username;

  @ConfigProperty(name = "password")
  String password;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/secrets")
  public Map<String, String> getSecrets() {
    Map<String, String> map = new HashMap<>();

    map.put("username", username);
    map.put("password", password);

    return map;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/supportmapping")
  public Map<String, String> getSupportMapping() {
    Map<String,String> map = getSupport();

    map.put("business.email", configMapping.business().email());
    map.put("business.phone", configMapping.business().phone());

    return map;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/support")
  public Map<String, String> getSupport() {
    Map<String, String> map = new HashMap<>();
    map.put("email", supportConfig.email);
    map.put("phone", supportConfig.getPhone());
    return map;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/mobilebanking")
  public Boolean getMobileBanking() {
    return mobileBanking.orElse(Boolean.FALSE);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.TEXT_PLAIN)
  public String getName() {
    return name;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello RESTEasy";
  }
}