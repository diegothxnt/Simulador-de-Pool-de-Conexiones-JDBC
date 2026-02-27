import java.sql.*;
import java.util.concurrent.*;

public class PoolConexiones {
    private BlockingQueue<Connection> pool;
    private Configuracion config;
    
    public PoolConexiones(Configuracion config) throws SQLException {
        this.config = config;
        this.pool = new LinkedBlockingQueue<>(config.getPoolSize());
        
        for (int i = 0; i < config.getPoolSize(); i++) {
            pool.offer(DriverManager.getConnection(
                config.getDbUrl(), config.getDbUser(), config.getDbPassword()));
        }
    }
    
    public Connection obtenerConexion() throws InterruptedException {
        return pool.take(); // Espera si no hay disponibles
    }
    
    public void liberarConexion(Connection conn) {
        if (conn != null) pool.offer(conn);
    }
    
    public void cerrarTodo() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
    }
}