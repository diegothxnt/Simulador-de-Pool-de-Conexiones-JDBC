import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class SimuladorConcurrente {
    private Configuracion config;
    private volatile boolean frenoActivo = false;
    
    public SimuladorConcurrente(Configuracion config) {
        this.config = config;
    }
    
    public void simularRaw() throws Exception {
        System.out.println("=== INICIANDO SIMULACIÓN RAW ===");
        LoggerSimulacion logger = new LoggerSimulacion("RAW");
        
        long inicioTotal = System.currentTimeMillis();
        
        // Usamos un executor para manejar los hilos
        ExecutorService executor = Executors.newFixedThreadPool(config.getNumMuestras());
        List<Future<ResultadoMuestra>> futuros = new ArrayList<>();
        
        // Lanzar todos los hilos
        for (int i = 0; i < config.getNumMuestras(); i++) {
            final int id = i + 1;
            Callable<ResultadoMuestra> tarea = () -> {
                int reintentos = 0;
                boolean exitoso = false;
                long tiempoInicio = System.currentTimeMillis();
                
                while (!exitoso && reintentos < config.getMaxReintentos() && !frenoActivo) {
                    reintentos++;
                    try (Connection conn = DriverManager.getConnection(
                            config.getDbUrl(), config.getDbUser(), config.getDbPassword());
                         Statement stmt = conn.createStatement()) {
                        
                        stmt.execute(config.getQuery());
                        exitoso = true;
                        
                    } catch (SQLException e) {
                        if (reintentos >= config.getMaxReintentos()) break;
                    }
                }
                
                long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
                logger.logMuestra(id, exitoso, reintentos, tiempoTotal);
                
                return new ResultadoMuestra(id, exitoso, reintentos);
            };
            
            futuros.add(executor.submit(tarea));
        }
        
        // Esperar a que todos terminen
        int exitosas = 0;
        int totalReintentos = 0;
        
        for (Future<ResultadoMuestra> futuro : futuros) {
            ResultadoMuestra res = futuro.get();
            if (res.exitosa) exitosas++;
            totalReintentos += res.reintentos;
        }
        
        executor.shutdown();
        long tiempoTotal = System.currentTimeMillis() - inicioTotal;
        
        // Log resumen
        logger.logResumen(
            config.getNumMuestras(),
            exitosas,
            config.getNumMuestras() - exitosas,
            tiempoTotal,
            (double) totalReintentos / config.getNumMuestras()
        );
        
        System.out.printf("RAW completado - %d/%d exitosas - Tiempo: %d ms%n", 
            exitosas, config.getNumMuestras(), tiempoTotal);
    }
    
    public void simularPooled() throws Exception {
        System.out.println("=== INICIANDO SIMULACIÓN POOLED ===");
        LoggerSimulacion logger = new LoggerSimulacion("POOLED");
        PoolConexiones pool = new PoolConexiones(config);
        
        long inicioTotal = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(config.getNumMuestras());
        List<Future<ResultadoMuestra>> futuros = new ArrayList<>();
        
        for (int i = 0; i < config.getNumMuestras(); i++) {
            final int id = i + 1;
            Callable<ResultadoMuestra> tarea = () -> {
                int reintentos = 0;
                boolean exitoso = false;
                long tiempoInicio = System.currentTimeMillis();
                
                while (!exitoso && reintentos < config.getMaxReintentos() && !frenoActivo) {
                    reintentos++;
                    Connection conn = null;
                    try {
                        conn = pool.obtenerConexion();
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(config.getQuery());
                            exitoso = true;
                        }
                    } catch (Exception e) {
                        // Si falla, no hacemos nada especial
                    } finally {
                        if (conn != null) pool.liberarConexion(conn);
                    }
                }
                
                long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
                logger.logMuestra(id, exitoso, reintentos, tiempoTotal);
                
                return new ResultadoMuestra(id, exitoso, reintentos);
            };
            
            futuros.add(executor.submit(tarea));
        }
        
        int exitosas = 0;
        int totalReintentos = 0;
        
        for (Future<ResultadoMuestra> futuro : futuros) {
            ResultadoMuestra res = futuro.get();
            if (res.exitosa) exitosas++;
            totalReintentos += res.reintentos;
        }
        
        executor.shutdown();
        pool.cerrarTodo();
        long tiempoTotal = System.currentTimeMillis() - inicioTotal;
        
        logger.logResumen(
            config.getNumMuestras(),
            exitosas,
            config.getNumMuestras() - exitosas,
            tiempoTotal,
            (double) totalReintentos / config.getNumMuestras()
        );
        
        System.out.printf("POOLED completado - %d/%d exitosas - Tiempo: %d ms%n", 
            exitosas, config.getNumMuestras(), tiempoTotal);
    }
    
    public void activarFreno() {
        this.frenoActivo = true;
        System.out.println("¡FRENO ACTIVADO! Deteniendo simulación...");
    }
    
    // Clase interna para resultados
    private static class ResultadoMuestra {
        int id;
        boolean exitosa;
        int reintentos;
        
        ResultadoMuestra(int id, boolean exitosa, int reintentos) {
            this.id = id;
            this.exitosa = exitosa;
            this.reintentos = reintentos;
        }
    }
}