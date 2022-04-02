package quarkus.accounts.jpa;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

@Entity
@NamedQuery(name = "Accounts.findAll", query = "SELECT a from Account a ORDER BY a.accountNumber")
@NamedQuery(
    name = "Accounts.findByAccountNumber",
    query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
public class Account {

  private Long accountNumber;
  private Long customerNumber;
  private String customerName;
  private BigDecimal balance;
  private AccountStatus accountStatus = AccountStatus.OPEN;

  @Id
  @SequenceGenerator(
      name = "accountSequence",
      sequenceName = "accounts_id_seq",
      allocationSize = 1,
      initialValue = 10)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSequence")
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(Long customerNumber) {
    this.customerNumber = customerNumber;
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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Long getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Long accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public AccountStatus getAccountStatus() {
    return accountStatus;
  }

  public void setAccountStatus(AccountStatus accountStatus) {
    this.accountStatus = accountStatus;
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
    return Objects.equals(getAccountNumber(), other.getAccountNumber())
        && Objects.equals(customerNumber, other.customerNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAccountNumber(), customerNumber);
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
