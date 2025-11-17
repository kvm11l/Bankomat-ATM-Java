# Bankomat-ATM-Java
Konsolowa symulacja bankomatu napisana w języku Java, umożliwiająca podstawowe operacje bankowe takie jak wpłaty, wypłaty, sprawdzanie salda oraz zarządzanie kontami użytkowników.


## Funkcjonalności
### Dla użytkownika
- Logowanie do konta za pomocą numeru konta i PIN-u
- Sprawdzanie salda konta
- Wpłaty gotówki - tylko banknoty (kwoty podzielne przez 10)
- Wypłaty gotówki - z walidacją stanu konta i dostępności środków w bankomacie
- Bezpieczne wylogowanie z automatycznym zapisem danych

### Dla administratora
- Ukryty tryb admina (hasło: 9999)
- Podgląd stanu gotówki w bankomacie
- Dodawanie/Usuwanie gotówki z rezerwy bankomatu
- Zarządzanie zasobami bankomatu

### System
- Automatyczne tworzenie plików danych przy pierwszym uruchomieniu
- Zapis historii transakcji z datą i godziną
- Trwałe przechowywanie danych kont i stanu bankomatu
- Walidacja danych wejściowych i obsługa błędów

## Struktura projektu
- ATMApp.java - główna klasa aplikacji
- Bank.java - zarządzanie kontami użytkowników
- Account.java - konto użytkownika
- ATM.java - bankomat - stan gotówki i informacje
- Transaction.java - transakcja (wpłata/wypłata)
- TransactionLog.java - zapisywanie historii transakcji
- accounts.txt - przykładowe dane kont użytkowników
- atm.txt - informacje o bankomacie
- transactions.txt - historia wszystkich transakcji

## Przepływ działania
1. Uruchomienie aplikacji - java ATMApp 
2. Wybieranie opcji z menu głównego:
	- 1 - Logowanie do konta
	- 2 - Informacje o bankomacie
	- 3 - Zakończ program
	- 0 - Tryb administratora (ukryte)
3. Po zalogowaniu możliwe operacje to:
	- 1 - Sprawdzenie salda 
	- 2 - Wpłacenie gotówki - tylko kwoty podzielne przez 10
	- 3 - Wypłata gotówki - z walidacją dostępności
	- 4 - Wylogowanie się

## Formaty plików
`accounts.txt`\
numer_konta;PIN;saldo\
2137;1234;1100.00\
1001;0000;250.50

`transactions.txt`\
numer_konta;typ_transakcji;kwota;data_czas\
2137;Wpłata;100;2024-01-15 14:30:25\
2137;Wypłata;200;2024-01-15 14:35:10

`atm.txt`\
gotówka_w_bankomacie;adres;informacje_publiczne;kod_bankomatu\
5900.00;ul. Przykładowa 1, 00-001 Miasto;Dostępny 24/7;ATM-2137

## Bezpieczeństwo
- Walidacja PIN-u - sprawdzanie poprawności podczas logowania
- Sprawdzanie stanu konta - przed każdą wypłatą
- Walidacja kwot - tylko dodatnie kwoty podzielne przez 10
- Obsługa wyjątków - zabezpieczenie przed błędami użytkownika

## Możliwe rozszerzenia
- Szyfrowanie PIN-ów i danych wrażliwych
- Przelewy między kontami
- Zmiana PIN-u przez użytkownika
- Limity dzienne wypłat
- Interfejs graficzny (GUI)
- Połączenie z bazą danych

## Aktualne ograniczenia
- PIN-y przechowywane jako plain text
- Brak szyfrowania plików danych
- Single-user operation (brak wielowątkowości)


