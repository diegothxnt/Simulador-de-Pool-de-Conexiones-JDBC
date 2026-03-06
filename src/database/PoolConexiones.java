package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class PoolConexiones {
    private LinkedList<Connection> pool;

    public PoolConexiones(String url, String user, String pass, int size) throws SQLException {
        pool = new LinkedList<>();
        // Llenamos la lista inicial con las conexiones
        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(url, user, pass));
        }
    }

    // 'synchronized' para que solo un hilo pueda entrar a la vez
    public synchronized Connection obtenerConexion() throws InterruptedException {
        while (pool.isEmpty()) {
            wait();
        }  
        return pool.removeFirst(); 
    }
    
    // 'synchronized' para evitar condiciones de carrera al devolver la conexión
    public synchronized void liberarConexion(Connection conn) {
        if (conn != null) {
            pool.addLast(conn);
            notifyAll();
        }
    }
    
    public synchronized void cerrarTodo() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
        pool.clear();
    }
}