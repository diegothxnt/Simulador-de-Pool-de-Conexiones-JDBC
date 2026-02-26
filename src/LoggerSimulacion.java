import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class LoggerSimulacion {
    private PrintWriter writer;
    private String tipoSimulacion;
    
    public LoggerSimulacion(String tipo) throws IOException {
        this.tipoSimulacion = tipo;
        this.writer = new PrintWriter(new FileWriter(tipo + "_log_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log"));
    }
    
    public synchronized void logMuestra(int id, boolean exitosa, int intento, long tiempoMs) {
        String estado = exitosa ? "EXITOSA" : "FALLIDA";
        writer.printf("[%s] ID: %d | %s | Intento: %d | Tiempo: %d ms%n",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
            id, estado, intento, tiempoMs);
        writer.flush();
    }