import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

// glowna klasa aplikacji, obsługuje menu główne, logowanie, operacje na koncie i tryb administratora.
public class ATMApp {

    // ścieżki do plików tekstowych (konta, dane bankomatu, historia transakcji), Path - sciezka do pliku, static poniewaz wartosc ta sie nie zmienia
    static final Path ACCOUNTS_FILE = Path.of("accounts.txt");
    static final Path ATM_FILE = Path.of("atm.txt");
    static final Path TRANSACTIONS_FILE = Path.of("transactions.txt");

    // hasło administratora (ukryta opcja menu "0")
    static final String ADMIN_PASSWORD = "9999";

    public static void main(String[] args) throws IOException {
        // scanner do odczytu danych od użytkownika
        Scanner sc = new Scanner(System.in);

        // wczytanie kont, bankomatu i logów (utworzenie plików jeśli nie istnieją)
        Bank bank = Bank.loadOrCreateDefault(ACCOUNTS_FILE);
        ATM atm = ATM.loadOrCreateDefault(ATM_FILE);
        TransactionLog tlog = new TransactionLog(TRANSACTIONS_FILE);

        boolean running = true; // flaga głównej pętli
        while (running) {
            // Wyświetlenie menu głównego
            System.out.println("\n-----MENU GŁÓWNE----");
            System.out.println("1) Logowanie");
            System.out.println("2) Informacje o bankomacie");
            System.out.println("3) Zakończ");
            System.out.print("Twój wybór: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": // logowanie do konta
                    handleLogin(sc, bank, atm, tlog);
                    break;
                case "2": // publiczne informacje o bankomacie
                    System.out.println("Adres: " + atm.getAddress());
                    System.out.println("Kod: " + atm.getAtmCode());
                    System.out.println("Info: " + atm.getPublicInfo());
                    System.out.println(
                            "Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    break;
                case "3": // zakończenie programu
                    bank.save(ACCOUNTS_FILE); // zapis stanu kont
                    atm.save(ATM_FILE);       // zapis stanu bankomatu
                    running = false;
                    break;
                case "0": // ukryte menu administratora
                    System.out.print("Hasło admina: ");
                    if (sc.nextLine().equals(ADMIN_PASSWORD))
                        adminMenu(sc, atm);
                    break;
                default: // obsługa błędnego wyboru
                    System.out.println("Niepoprawny wybór.");
            }
        }
        sc.close(); // zamknięcie Scannera
    }

    // obsługa logowania i menu konta użytkownika.
    private static void handleLogin(Scanner sc, Bank bank, ATM atm, TransactionLog tlog) throws IOException {
        // Pobranie danych logowania
        System.out.print("Nr konta: ");
        String accNum = sc.nextLine();
        System.out.print("PIN: ");
        String pin = sc.nextLine();

        // próba autoryzacji, Optional aby uniknąć NullPointerException w przypadku braku konta
        Optional<Account> accOpt = bank.authenticate(accNum, pin);

        // jeśli nie znaleziono konta (Optional pusty)
        if (accOpt.isEmpty()) {
            System.out.println("Błędne dane logowania.");
            return; // kończymy logowanie
        }

        // jeśli znaleziono konto, pobieramy je z Optionala
        Account acc = accOpt.get();
        boolean logged = true; // ustawiamy flagę - użytkownik zalogowany
        while (logged) {
            // menu operacji na koncie
            System.out.println("\n--- MENU KONTA ---");
            System.out.println("1) Saldo");
            System.out.println("2) Wpłata");
            System.out.println("3) Wypłata");
            System.out.println("4) Wyloguj");
            String c = sc.nextLine();

            switch (c) {
                case "1": // sprawdzenie salda
                    System.out.println("Saldo: " + acc.getBalance());
                    break;

                case "2": // wpłata
                    System.out.print("Kwota wpłaty: ");
                    try {
                        BigDecimal in = new BigDecimal(sc.nextLine());
                        if (isValidBanknoteAmount(in)) {
                            acc.deposit(in);       // zwiększamy saldo
                            atm.addCash(in);       // zwiększamy rezerwę bankomatu
                            tlog.log(new Transaction(acc.getAccountNumber(), "Wpłata", in)); // zapis do logu
                            System.out.println("Udana wpłata " + in + " PLN");
                        } else {
                            System.out.println("Kwota musi być dodatnia i podzielna przez 10.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format kwoty. Użyj formatu np. 100.00");
                    }
                    break;

                case "3": // wypłata
                    System.out.print("Kwota wypłaty: ");
                    try {
                        BigDecimal out = new BigDecimal(sc.nextLine());
                        if (!isValidBanknoteAmount(out)) {
                            System.out.println("Kwota musi być dodatnia i podzielna przez 10.");
                        } else if (acc.getBalance().compareTo(out) < 0) {
                            System.out.println("Za mało środków na koncie.");
                        } else if (atm.getCashReserve().compareTo(out) < 0) {
                            System.out.println("Bankomat nie ma wystarczającej ilości gotówki.");
                        } else {
                            acc.withdraw(out);       // zmniejsz saldo
                            atm.removeCash(out);     // zmniejsz rezerwę bankomatu
                            tlog.log(new Transaction(acc.getAccountNumber(), "Wypłata", out)); // zapis do logu
                            System.out.println("Udana wypłata " + out + " PLN");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format kwoty. Użyj formatu np. 100.00");
                    }
                    break;

                case "4": // wylogowanie
                    bank.save(ACCOUNTS_FILE); // zapisz stan kont
                    atm.save(ATM_FILE);       // zapisz stan bankomatu
                    logged = false;
                    break;
            }
        }
    }

    // obsługa menu administratora (ukryta opcja "0").
    private static void adminMenu(Scanner sc, ATM atm) throws IOException {
        boolean adm = true;
        while (adm) {
            // wyświetlenie menu admina
            System.out.println("--- MENU ADMINA ---");
            System.out.println("1) Ilość gotówki w bankomacie");
            System.out.println("2) Dodaj gotówkę");
            System.out.println("3) Zabierz gotówkę");
            System.out.println("4) Wyjście");

            switch (sc.nextLine()) {
                case "1": // sprawdzenie rezerwy
                    System.out.println(atm.getHiddenInfo());
                    break;

                case "2": // dodanie gotówki
                    System.out.print("Kwota: ");
                    try {
                        BigDecimal add = new BigDecimal(sc.nextLine());
                        atm.addCash(add);
                        atm.save(ATM_FILE);
                        System.out.println("Dodano " + add + " PLN do bankomatu.");
                    } catch (NumberFormatException e) {                 // jesli podany String zabezpieczenie przed błędem
                        System.out.println("Błędny format kwoty. Użyj formatu np. 100.00");
                    }
                    break;

                case "3": // zabranie gotówki
                    System.out.print("Kwota: ");
                    try {
                        BigDecimal remove = new BigDecimal(sc.nextLine());
                        if (atm.getCashReserve().compareTo(remove) < 0) {
                            System.out.println("Bankomat nie ma wystarczającej ilości gotówki.");
                        } else {
                            atm.removeCash(remove);
                            atm.save(ATM_FILE);
                            System.out.println("Usunięto " + remove + " PLN z bankomatu.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błędny format kwoty. Użyj formatu np. 100.00");
                    }
                    break;

                case "4": // wyjście z menu admina
                    adm = false;
                    break;
            }
        }
    }

    // walidacja kwoty – sprawdzamy czy kwota jest dodatnia i podzielna przez 10 (same banknoty).
    private static boolean isValidBanknoteAmount(BigDecimal amount) {
        return amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && amount.remainder(new BigDecimal("10")).compareTo(BigDecimal.ZERO) == 0;
    }
}
