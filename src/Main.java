import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Cargar configuracion
            System.out.print("Ruta del archivo de configuración [config.properties]: ");
            String ruta = scanner.nextLine();
            if (ruta.trim().isEmpty()) ruta = "config.properties";
            
            Configuracion config = new Configuracion(ruta);
            SimuladorConcurrente simulador = new SimuladorConcurrente(config);
        
             // Menu simple
            System.out.println("\n=== SIMULADOR DE CONEXIONES ===");
            System.out.println("1. Simulación RAW");
            System.out.println("2. Simulación POOLED");
            System.out.println("3. Ambas simulaciones");
            System.out.print("Selecciona: ");
            
            String opcion = scanner.nextLine();
            
            // Hilo para detectar freno
            Thread frenoThread = new Thread(() -> {
                System.out.println("Presiona ENTER para activar FRENO...");
                try {
                    System.in.read();
                    simulador.activarFreno();
                } catch (IOException e) {}
            });
            frenoThread.setDaemon(true);
            frenoThread.start();

         // Ejecutar segun opcion
            switch(opcion) {
                case "1":
                    simulador.simularRaw();
                    break;
                case "2":
                    simulador.simularPooled();
                    break;
                case "3":
                    long inicio = System.currentTimeMillis();
                    
                    simulador.simularRaw();
                    simulador.simularPooled();
                    
                    long total = System.currentTimeMillis() - inicio;
                    System.out.println("\n=== COMPARATIVA ===");
                    System.out.println("Revisa los archivos .log generados");
                    System.out.printf("Tiempo total: %d ms%n", total);
                    break;
                default:
                    System.out.println("Opción no válida");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}