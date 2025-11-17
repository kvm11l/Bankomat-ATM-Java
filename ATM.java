import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

// przechowuje dane bankomatu – ilość gotówki, adres, info publiczne i kod
public class ATM {
    // ukryta ilość gotówki w bankomacie
    private BigDecimal cashReserve;

    // adres lokalizacji bankomatu
    private String address;

    // informacje widoczne dla użytkownika
    private String publicInfo;

    // kod identyfikacyjny bankomatu
    private String atmCode;

    public ATM(BigDecimal cashReserve, String address, String publicInfo, String atmCode) {
        this.cashReserve = cashReserve.setScale(2, RoundingMode.HALF_UP);
        this.address = address;
        this.publicInfo = publicInfo;
        this.atmCode = atmCode;
    }

    public BigDecimal getCashReserve() { return cashReserve; }
    public String getAddress() { return address; }
    public String getPublicInfo() { return publicInfo; }
    public String getAtmCode() { return atmCode; }

    // dodaj gotówkę do rezerwy
    public void addCash(BigDecimal amount) { cashReserve = cashReserve.add(amount); }

    // zabierz gotówkę z rezerwy
    public void removeCash(BigDecimal amount) { cashReserve = cashReserve.subtract(amount); }

    // zwroc informacje ukryte (tylko dla admina)
    public String getHiddenInfo() { return "Rezerwa: " + cashReserve + " PLN"; }


    // zapisz dane bankomatu do pliku
    public void save(Path path) throws IOException {
        // utworz linijkę tekstu zawierającą wszystkie dane bankomatu
        String line = cashReserve + ";" + address + ";" + publicInfo + ";" + atmCode;

        // zapisz linijkę do pliku, nadpisz jego zawartość
        Files.writeString(path, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,              // utwórz plik jeśli nie istnieje
                StandardOpenOption.TRUNCATE_EXISTING);  // wyczyść plik jeśli już istnieje
    }


    // wczytaj dane bankomatu lub utworz domyślne
    public static ATM loadOrCreateDefault(Path path) throws IOException {
        if (Files.exists(path)) {
            // jeśli plik istnieje, wczytywana jest jego zawartość i tworzony jest obiekt ATM na podstawie danych z pliku
            String[] p = Files.readString(path).trim().split(";");
            return new ATM(new BigDecimal(p[0]), p[1], p[2], p[3]);
        } else {
            // jeśli plik nie istnieje, tworzymy nowy bankomat z domyślnymi danymi
            ATM atm = new ATM(new BigDecimal("5000.00"),
                    "ul. Przykładowa 1, 00-001 Miasto",
                    "Dostępny 24/7",
                    "ATM-6789");
            atm.save(path); // zapisujemy go do pliku
            return atm;
        }
    }

}
