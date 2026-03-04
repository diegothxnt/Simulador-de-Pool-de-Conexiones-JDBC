# Simulador de POOL de Conexiones en una Base de Datos

Este proyecto es una herramienta en Java diseñada para simular, someter a estrés y comparar el rendimiento de múltiples conexiones simultáneas a una base de datos. Permite evaluar la diferencia de rendimiento entre abrir conexiones directas tradicionales y utilizar un gestor de Pool de Conexiones.

## 📁 Estructura del Proyecto

El código está organizado en los siguientes módulos:

* **`data/`**: Manejo de datos y registros auxiliares.
    * `Configuracion.java`: Encargado de cargar los parámetros de ejecución desde el archivo properties.
    * `LoggerSimulacion.java`: Registra los tiempos de respuesta, reintentos y el estado (éxito/fallo) de cada muestra en la simulación.
* **`database/`**: Lógica de acceso y gestión de la base de datos.
    * `PoolConexiones.java`: Implementa un pool de conexiones de tamaño fijo.
* **`main/`**: Núcleo de ejecución de la aplicación.
    * `Main.java`: Punto de entrada del programa.
    * `SimuladorConcurrente.java`: Orquesta la creación de hilos concurrentes mediante `ExecutorService` y ejecuta las dos modalidades de prueba (Raw y Pooled).
* **`properties/`**:
    * `config.properties`: Archivo de configuración central. Aquí se definen las credenciales de la BD, la consulta SQL a ejecutar, la cantidad de hilos (muestras), el límite de reintentos y el tamaño del pool.

## ⚙️ Características Principales

* **Simulación Concurrente:** Emplea programación multihilo moderna (`Callable` y `Future`) para simular decenas o cientos de peticiones a la vez.
* **Comparativa Raw vs. Pooled:** * **Modo Raw:** Crea y destruye una conexión nueva por cada petición.
    * **Modo Pooled:** Recicla un número limitado de conexiones pre-creadas para optimizar tiempos y recursos.
* **Tolerancia a Fallos:** Incorpora un sistema de reintentos para conexiones fallidas antes de dar una tarea por perdida.
* **Métricas y Resúmenes:** Al finalizar, calcula y muestra el tiempo total de procesamiento, la tasa de éxito y el promedio de reintentos por petición.

## 👨‍💻 Requisitos y Uso

1. Asegurarse de tener un motor de base de datos ejecutándose (MySQL, PostgreSQL, etc.) y el driver JDBC correspondiente.
2. Configura los valores del archivo `properties/config.properties` con tus credenciales locales y los parámetros de estrés deseados.
3. Compilar usando ```javac -d bin -cp "lib/*" src/data/*.java src/database/*.java src/main/*.java``` 
4. Ejecutar usando ```java -cp "bin;lib/*" main.Main```
5. Cuando lo solicite, especificar la ruta: `src/properties/config.properties`
6. Revisar la salida en consola y los logs generados para analizar los resultados.
