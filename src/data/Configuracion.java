package data;
import java.io.*;
import java.util.Properties;

public class Configuracion {
    private Properties props = new Properties();
    
    public Configuracion(String archivo) throws IOException {
        try (InputStream input = new FileInputStream(archivo)) {
            props.load(input);
        }
    }
    
    public String getDbUrl() { return props.getProperty("db.url"); }
    public String getDbUser() { return props.getProperty("db.user"); }
    public String getDbPassword() { return props.getProperty("db.password"); }
    public String getQuery() { return props.getProperty("query"); }
    public int getNumMuestras() { return Integer.parseInt(props.getProperty("num_muestras")); }
    public int getMaxReintentos() { return Integer.parseInt(props.getProperty("max_reintentos")); }
    public int getPoolSize() { return Integer.parseInt(props.getProperty("pool_size")); }
}