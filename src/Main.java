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