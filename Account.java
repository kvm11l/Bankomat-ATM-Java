import java.math.BigDecimal;
import java.math.RoundingMode;

// klasa reprezentujaca pojedyncze konto bankowe klienta
public class Account {
    private final String accountNumber; // Numer konta klienta (np. "2137")
    private final String pin; // PIN przypisany do konta
    private BigDecimal balance; // Aktualny stan konta

    // konstruktor konta
    public Account(String accountNumber, String pin, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        // Zapisanie salda z dokładnością do 2 miejsca po przecinku
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
    }

    public String getAccountNumber() { return accountNumber; }

    public BigDecimal getBalance() {
        return balance.setScale(2, RoundingMode.HALF_UP);
    }

    // sprawdz czy wprowadzony PIN jest poprawny
    public boolean checkPin(String pin) { return this.pin.equals(pin); }

    // dodaj środki na konto - wpłata
    public void deposit(BigDecimal amount) {
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    // odejmij środki z konta - wypłata
    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    }

    // zwróć reprezentację tekstową konta – zapis do pliku
    @Override
    public String toString() {
        return accountNumber + ";" + pin + ";" + balance;
    }

    // utworz obiekt konta na podstawie danych wczytanych z pliku
    public static Account fromString(String line) {
        String[] parts = line.split(";");                              // rozdzielamy linijkę tekstu po średniku (np. "2137;1234;1000.00")
        // parts[0] = numer konta, parts[1] = PIN, parts[2] = saldo
        return new Account(parts[0], parts[1], new BigDecimal(parts[2]));   // tworzymy nowe konto na podstawie odczytanych danych
    }

}
