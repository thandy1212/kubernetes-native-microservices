package quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import quarkus.accounts.repository.Account;
import quarkus.accounts.repository.AccountStatus;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(OrderAnnotation.class)
public class AccountResourceTest {

  @Test
  @Order(1)
  void testRetrieveAll() {
    Response result =
        given()
            .when()
            .get("/accounts")
            .then()
            .statusCode(200)
            .body(
                containsString("Debbie Hall"),
                containsString("David Tennant"),
                containsString("Alex Kingston"))
            .extract()
            .response();

    // extracts the json array and converts it to a list of accounts
    List<Account> accounts = result.jsonPath().getList("$");
    assertThat(accounts, not(empty()));
    assertThat(accounts, hasSize(8));
  }

  @Test
  @Order(2)
  void testGetAccount() {
    Account account =
        given()
            .when()
            .get("/accounts/{accountNumber}", 123456789)
            .then()
            .statusCode(200)
            .extract()
            .as(Account.class);

    assertThat(account.getAccountNumber(), equalTo(123456789L));
    assertThat(account.getCustomerName(), equalTo("Debbie Hall"));
    assertThat(account.getBalance(), equalTo(new BigDecimal("550.78")));
    assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
  }

  @Test
  @Order(3)
  void testCreateAccount() {
    Account newAccount = new Account();
    newAccount.setAccountNumber((324324L));
    newAccount.setCustomerNumber((112244L));
    newAccount.setCustomerName(("Sandy Holmes"));
    newAccount.setBalance((new BigDecimal("154.55")));

    Account returnedAccount =
        given()
            .contentType(ContentType.JSON)
            .body(newAccount)
            .when()
            .post("/accounts")
            .then()
            .statusCode(201)
            .extract()
            .as(Account.class);

    assertThat(returnedAccount, notNullValue());
    assertThat(returnedAccount, equalTo(newAccount));

    Response result =
        given()
            .when()
            .get("/accounts")
            .then()
            .statusCode(200)
            .body(
                containsString("Debbie Hall"),
                containsString("David Tennant"),
                containsString("Alex Kingston"))
            .extract()
            .response();

    List<Account> accounts = result.jsonPath().getList("$");
    assertThat(accounts, not(empty()));
    assertThat(accounts, hasSize(9));
  }
}
