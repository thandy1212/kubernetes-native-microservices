package quarkus.transactions;

import java.math.BigDecimal;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/accounts")
@RegisterRestClient(configKey = "account-service")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountService {

  @GET
  @Path("/{acctNumber}/balance")
  BigDecimal getBalance(@PathParam("acctNumber") Long accountNumber);

  @POST
  @Path("/{acctNumber}/transaction")
  void transact(@PathParam("acctNumber") Long accountNumber, BigDecimal amount);

  @POST
  @Path("/{accountNumber}/transaction")
  CompletionStage<Void> transactAsync(@PathParam("accountNumber") Long accountNumber, BigDecimal amount);
}
