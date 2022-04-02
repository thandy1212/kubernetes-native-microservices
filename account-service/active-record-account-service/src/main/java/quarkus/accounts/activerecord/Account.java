package quarkus.accounts.activerecord;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.Entity;

@Entity
public class Account extends PanacheEntity {

  public Long accountNumber;
  public Long customerNumber;
  public String customerName;
  public BigDecimal balance;
  public AccountStatus accountStatus = AccountStatus.OPEN;

  public static long totalAccountsForCustomer(Long customerNumber) {
    return find("customerNumber", customerNumber).count();
  }

  public static Account findByAccountNumber(Long accountNumber) {
    return find("accountNumber", accountNumber).firstResult();
  }

  public void markOverdrawn() {
    accountStatus = AccountStatus.OVERDRAWN;
  }

  public void removeOverdrawnStatus() {
    accountStatus = AccountStatus.OPEN;
  }

  public void close() {
    accountStatus = AccountStatus.CLOSED;
    balance = BigDecimal.valueOf(0);
  }

  public void withdrawFunds(BigDecimal amount) {
    balance = balance.subtract(amount);
  }

  public void addFunds(BigDecimal amount) {
    balance = balance.add(amount);
  }

  /**
   * Accounts are mutable entities meaning they do not have traditional equality in the sense of
   * field by field comparison. Instead, equality is determined solely by the accountNumber of the
   * two objects.
   *
   * @param obj - the object to check against
   * @return true iff obj is an Account and obj.getAccountNumber().equals(this.getAccountNumber())
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Account)) {
      return false;
    }
    Account other = (Account) obj;
    return Objects.equals(accountNumber, other.accountNumber)
        && Objects.equals(customerNumber, other.customerNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountNumber, customerNumber);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
        .add("accountNumber=" + accountNumber)
        .add("customerNumber=" + customerNumber)
        .add("customerName='" + customerName + "'")
        .add("balance=" + balance)
        .add("accountStatus=" + accountStatus)
        .add("id=" + id)
        .toString();
  }
}
