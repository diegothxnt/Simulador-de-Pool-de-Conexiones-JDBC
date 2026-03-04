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

    // Agregamos 'synchronized' para que solo un hilo pueda entrar a la vez
    public synchronized Connection obtenerConexion() throws InterruptedException {
        // Usamos un bucle 'while' en lugar de un 'if' para evitar despertares falsos (spurious wakeups)
        while (pool.isEmpty()) {
            wait(); // El hilo suelta el candado y se duerme hasta que alguien lo despierte
        }
        
        // Saca la primera conexión de la lista y se la entrega al hilo
        return pool.removeFirst(); 
    }
    
    // También debe ser 'synchronized' para evitar condiciones de carrera al devolver la conexión
    public synchronized void liberarConexion(Connection conn) {
        if (conn != null) {
            pool.addLast(conn); // Devuelve la conexión al final de la fila
            notifyAll(); // ¡Grita! Despierta a todos los hilos que estaban dormidos en el wait()
        }
    }
    
    public synchronized void cerrarTodo() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
        pool.clear(); // Vaciamos la lista por limpieza
    }
}