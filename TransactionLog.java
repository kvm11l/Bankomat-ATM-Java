import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

// odpowiada za zapis transakcji do pliku transactions.txt
public class TransactionLog {
    private final Path logFile;

    public TransactionLog(Path logFile) { this.logFile = logFile; }

    // zapisuje transakcję do pliku (dopisywanie na końcu)
    public void log(Transaction t) throws IOException {
        // zapisujemy transakcję do pliku dziennika
        Files.writeString(logFile,
                t.toString() + System.lineSeparator(), // dodajemy transakcję jako nową linijkę
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, // utwórz plik jeśli go nie ma
                StandardOpenOption.APPEND  // dopisz do istniejącego pliku
        );
    }

}
