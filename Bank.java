import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

// przechowuje listę kont i obsługuje logowanie oraz zapis/odczyt do pliku.
public class Bank {
    // lista wszystkich kont w banku
    private final List<Account> accounts = new ArrayList<>();

    // dodaj konto do listy
    public void addAccount(Account a) { accounts.add(a); }

    // proba zalogowania klienta na podstawie numeru konta i PIN-u
    public Optional<Account> authenticate(String accountNumber, String pin) {
        // przeszukaj listę kont (stream)
        return accounts.stream()
                // filtruj: numer konta się zgadza i PIN też
                .filter(a -> a.getAccountNumber().equals(accountNumber) && a.checkPin(pin))
                // zwróc pierwsze znalezione konto (Optional może być pusty)
                .findFirst();
    }


    // zapis wszystkich kont do pliku
    public void save(Path path) throws IOException {
        // zamiana każdego konta na tekst (Account.toString())
        List<String> lines = accounts.stream()
                .map(Account::toString)
                .collect(Collectors.toList());

        // zapis listy linii do pliku (każde konto w osobnej linii)
        Files.write(path, lines, StandardCharsets.UTF_8);
    }


    // ładowanie kont z pliku lub utworzenie pliku z przykładowymi kontami
    public static Bank loadOrCreateDefault(Path path) throws IOException {
        Bank b = new Bank();
        if (Files.exists(path)) {
            // jeśli plik istnieje to wczytaj wszystkie linie
            for (String l : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (!l.trim().isEmpty())
                    b.addAccount(Account.fromString(l)); // dodaje konto do listy
            }
        } else {
            // jeśli plik nie istnieje - tworzy przykładowe konta
            b.addAccount(new Account("2137", "1234", new BigDecimal("1000.00")));
            b.addAccount(new Account("1001", "0000", new BigDecimal("250.50")));
            b.save(path); // zapisujemy je do pliku
        }
        return b; // zwraca gotowy obiekt banku
    }
}
