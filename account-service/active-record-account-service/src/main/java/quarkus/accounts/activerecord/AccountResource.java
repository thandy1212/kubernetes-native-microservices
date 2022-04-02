package quarkus.accounts.activerecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Path("/accounts")
public class AccountResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Account> allAccounts() {
    return Account.listAll();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response createAccount(Account anAccount) {
    if (anAccount.accountNumber == null) {
      throw new WebApplicationException("No account number provided", 400);
    }
    anAccount.persist();
    return Response.status(201).entity(anAccount).build();
  }

  @PUT
  @Path("/{accountNumber}/withdrawl")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Account withdraw(@PathParam("accountNumber") Long accountNumber, String amount) {
    Account theAccount = getAccount(accountNumber);
    theAccount.withdrawFunds(new BigDecimal(amount));
    return theAccount;
  }

  @PUT
  @Path("/{accountNumber}/deposit")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Account deposit(@PathParam("accountNumber") Long accountNumber, String amount) {
    Account theAccount = getAccount(accountNumber);
    theAccount.addFunds(new BigDecimal(amount));
    return theAccount;
  }

  @DELETE
  @Path("/{accountNumber}")
  @Transactional
  public Response deleteAccount(@PathParam("accountNumber") Long accountNumber) {
    Account theAccount = getAccount(accountNumber);
    theAccount.delete();
    return Response.noContent().build();
  }

  @GET
  @Path("/{accountNumber}")
  @Produces(MediaType.APPLICATION_JSON)
  public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
    return Account.findByAccountNumber(accountNumber);
  }

  @GET
  @Path("/{acctNumber}/balance")
  public BigDecimal getBalance(@PathParam("acctNumber") Long accountNumber) {
    Account account = Account.findByAccountNumber(accountNumber);

    if (account == null) {
      throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
    }

    return account.balance;
  }

  @POST
  @Path("/{acctNumber}/transaction")
  @Transactional
  public Map<String, List<String>> transact(
      @Context HttpHeaders headers,
      @PathParam("acctNumber") Long accountNumber,
      BigDecimal amount) {
    Account entity = Account.findByAccountNumber(accountNumber);

    if (entity == null) {
      throw new WebApplicationException("Account with " + accountNumber + " does not exist.", 404);
    }

    if (entity.accountStatus.equals(AccountStatus.OVERDRAWN)) {
      throw new WebApplicationException("Account is overdrawn, no new withdrawals permitted", 409);
    }

    entity.balance = entity.balance.add(amount);
    return headers.getRequestHeaders();
  }

  // Provider indicates the class is an autodiscovered JAX-RS Provider
  @Provider
  public static class ErrorMapper
      implements ExceptionMapper<Exception> { // implemented for all exception types

    // overrides the toResponse method for converting the exception to a Response
    @Override
    public Response toResponse(Exception exception) {

      // checks for WebApplicationException, and extracts the HTTP
      // status code; otherwise defaults to 500
      int code = 500;
      if (exception instanceof WebApplicationException) {
        code = ((WebApplicationException) exception).getResponse().getStatus();
      }

      JsonObjectBuilder entityBuilder =
          Json.createObjectBuilder()
              .add("exceptionType", exception.getClass().getName())
              .add("code", code);

      // if there is a message, add it to the JSON object
      if (exception.getMessage() != null) {
        entityBuilder.add("error", exception.getMessage());
      }

      // Returns a Response with HTTP status code and JSON object
      return Response.status(code).entity(entityBuilder.build()).build();
    }
  }
}
