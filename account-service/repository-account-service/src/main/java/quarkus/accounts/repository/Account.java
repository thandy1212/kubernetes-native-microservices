package quarkus.accounts.repository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Account {

  @Id @GeneratedValue private Long id;
  private Long accountNumber;
  private Long customerNumber;
  private String customerName;
  private BigDecimal balance;
  private AccountStatus accountStatus = AccountStatus.OPEN;

  public void markOverdrawn() {
    accountStatus = quarkus.accounts.repository.AccountStatus.OVERDRAWN;
  }

  public void removeOverdrawnStatus() {
    accountStatus = quarkus.accounts.repository.AccountStatus.OPEN;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Long accountNumber) {
    this.accountNumber = accountNumber;
  }

  public Long getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(Long customerNumber) {
    this.customerNumber = customerNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public AccountStatus getAccountStatus() {
    return accountStatus;
  }

  public void setAccountStatus(AccountStatus accountStatus) {
    this.accountStatus = accountStatus;
  }
}
