package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class PoolConexiones {
    private ArrayBlockingQueue<Connection> pool;

    public PoolConexiones(String url, String user, String pass, int size) throws SQLException {
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(url, user, pass));
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