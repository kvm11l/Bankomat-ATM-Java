import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// reprezentuje pojedynczą transakcję klienta
public class Transaction {
    private final String accountNumber; // numer konta klienta
    private final String type;          // typ transakcji: Wpłata/Wypłata
    private final BigDecimal amount;    // kwota transakcji
    private final LocalDateTime time;   // czas transakcji

    public Transaction(String accountNumber, String type, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    // zapis transakcji do pliku w formacie tekstowym
    @Override
    public String toString() {
        // laczymy wszystkie dane transakcji w jedną linijkę rozdzieloną średnikami
        // numer konta ; typ transakcji ; kwota ; data i czas
        return accountNumber + ";" + type + ";" + amount + ";" +
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
