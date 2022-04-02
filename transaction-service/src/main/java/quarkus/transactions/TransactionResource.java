package quarkus.transactions;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/transactions")
public class TransactionResource {
  @Inject @RestClient AccountService accountService;
  @ConfigProperty(name = "acount.service", defaultValue = "http://localhost:8080")
  String accountServiceUrl;

  @POST
  @Path("/{accountNumber}")
  public Response newTransaction(
      @PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
    accountService.transact(accountNumber, amount);
    return Response.ok().build();
  }

  @POST
  @Path("/api/{accountNumber}")
  public Response newTransactionWithApi(
      @PathParam("accountNumber") Long accountNumber, BigDecimal amount)
      throws MalformedURLException {
    AccountServiceProgrammatic acctService =
        RestClientBuilder.newBuilder()
            .baseUrl(new URL(accountServiceUrl))
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(1200, TimeUnit.MILLISECONDS)
            .build(AccountServiceProgrammatic.class);

    acctService.transact(accountNumber, amount);
    return Response.ok().build();
  }

  @POST
  @Path("/async/{accountNumber}")
  public CompletionStage<Void> newTransactionAsync(
      @PathParam("accountNumber") Long accountNumber, BigDecimal amount) {
    return accountService.transactAsync(accountNumber, amount);

  }

  @POST
  @Path("/api/async/{accountNumber}")
  public CompletionStage<Void> newTransactionWithApiAsync(
      @PathParam("accountNumber") Long accountNumber, BigDecimal amount)
      throws MalformedURLException {
    AccountServiceProgrammatic acctService =
        RestClientBuilder.newBuilder()
            .baseUrl(new URL(accountServiceUrl))
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(1200, TimeUnit.MILLISECONDS)
            .build(AccountServiceProgrammatic.class);

    return acctService.transactAsync(accountNumber, amount);

  }
}
