import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Clase encargada de generar archivos de prueba pseudoaleatorios para el sistema
 * de gestion de ventas. Produce archivos de productos, vendedores y ventas
 * que sirven como entrada para la clase {@code main}.
 *
 * <p>Al ejecutarse, genera los siguientes archivos en el directorio de trabajo:</p>
 * <ul>
 *   <li>{@code productos.txt} - informacion de productos disponibles</li>
 *   <li>{@code vendedores.txt} - informacion de vendedores registrados</li>
 *   <li>{@code ventas_XXXX.txt} - archivos de ventas por vendedor</li>
 * </ul>
 *
 * <p><b>Extras implementados:</b></p>
 * <ul>
 *   <li>Extra A: se generan multiples archivos de ventas para el mismo vendedor.</li>
 *   <li>Extra C: se genera un archivo con datos invalidos para probar la deteccion
 *       de errores en la clase {@code main}.</li>
 * </ul>
 *
 * @author Eric Camilo Marmolejo B.
 * @author Juan Ortega Gomez
 * @author David Esteban Urquijo
 * @version 2.0
 */
public class GenerateInfoFiles {

    /** Generador de numeros pseudoaleatorios compartido por todos los metodos. */
    static Random random = new Random();

    /**
     * Punto de entrada del programa. Genera todos los archivos de prueba necesarios.
     *
     * <p>Crea 10 productos, 5 vendedores, archivos de ventas para cada vendedor
     * (incluyendo un segundo archivo para el vendedor 1001 para demostrar el soporte
     * de multiples archivos por vendedor), y un archivo con datos invalidos para
     * probar la deteccion de errores.</p>
     *
     * @param args argumentos de linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        try {
            createProductsFile(10);
            createSalesManInfoFile(5);

            String[] nombres = {"Maria", "Diego", "Carlos", "Sofia", "Pedro"};
            for (int i = 1; i <= 5; i++) {
                createSalesMenFile(5, nombres[i - 1], 1000 + i);
            }

            createSalesMenFile(3, "Maria", 1001);
            createInvalidSalesFile();

            System.out.println(">>>===  Archivos generados correctamente  ===<<<");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Crea el archivo {@code productos.txt} con informacion pseudoaleatoria de productos.
     *
     * <p>Cada linea del archivo tiene el formato:</p>
     * <pre>IDProducto;NombreProducto;PrecioPorUnidad</pre>
     *
     * @param productsCount cantidad de productos a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        try (FileWriter writer = new FileWriter("productos.txt")) {
            for (int i = 1; i <= productsCount; i++) {
                int price = random.nextInt(500) + 50;
                writer.write(i + ";Producto" + i + ";" + price + "\n");
            }
        }
    }

    /**
     * Crea el archivo {@code vendedores.txt} con informacion pseudoaleatoria de vendedores.
     *
     * <p>Cada linea del archivo tiene el formato:</p>
     * <pre>TipoDocumento;NumeroDocumento;Nombres;Apellidos</pre>
     *
     * <p>Los nombres y apellidos se seleccionan aleatoriamente de listas predefinidas
     * de nombres reales colombianos.</p>
     *
     * @param salesmanCount cantidad de vendedores a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        String[] nombres = {
            "Juan", "Maria", "Pedro", "Ana", "Luis",
            "Carlos", "Laura", "Andres", "Sofia", "Diego"
        };
        String[] apellidos = {
            "Perez", "Gomez", "Ruiz", "Lopez", "Torres",
            "Martinez", "Castro", "Ramirez", "Vargas", "Moreno"
        };

        try (FileWriter writer = new FileWriter("vendedores.txt")) {
            for (int i = 1; i <= salesmanCount; i++) {
                String nombre = nombres[random.nextInt(nombres.length)];
                String apellido = apellidos[random.nextInt(apellidos.length)];
                writer.write("CC;" + (1000 + i) + ";" + nombre + ";" + apellido + "\n");
            }
        }
    }

    /**
     * Crea un archivo de ventas pseudoaleatorio para un vendedor especifico.
     *
     * <p>Si el vendedor ya tiene un archivo de ventas existente, se crea un archivo
     * adicional con un sufijo numerico para soportar multiples archivos por vendedor
     * (Extra A). El formato del archivo es:</p>
     * <pre>
     * TipoDocumento;NumeroDocumento
     * IDProducto1;Cantidad1;
     * IDProducto2;Cantidad2;
     * </pre>
     *
     * @param randomSalesCount numero de lineas de ventas a generar
     * @param name             nombre del vendedor (usado para nombrar el archivo)
     * @param id               numero de documento del vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id)
            throws IOException {

        String baseFileName = "ventas_" + id;
        String fileName = baseFileName + ".txt";
        java.io.File existingFile = new java.io.File(fileName);

        if (existingFile.exists()) {
            int suffix = 2;
            while (new java.io.File(baseFileName + "_" + suffix + ".txt").exists()) {
                suffix++;
            }
            fileName = baseFileName + "_" + suffix + ".txt";
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("CC;" + id + "\n");

            for (int i = 0; i < randomSalesCount; i++) {
                int productId = random.nextInt(10) + 1;
                int quantity = random.nextInt(10) + 1;
                writer.write(productId + ";" + quantity + ";\n");
            }
        }
    }

    /**
     * Crea un archivo de ventas con datos invalidos para demostrar la deteccion
     * de errores (Extra C).
     *
     * <p>El archivo {@code ventas_error_demo.txt} contiene:</p>
     * <ul>
     *   <li>Una linea con un ID de producto inexistente (ID 99)</li>
     *   <li>Una linea con cantidad negativa</li>
     *   <li>Una linea con formato incorrecto</li>
     * </ul>
     *
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createInvalidSalesFile() throws IOException {
        try (FileWriter writer = new FileWriter("ventas_error_demo.txt")) {
            writer.write("CC;1001\n");
            writer.write("99;5;\n");
            writer.write("1;-3;\n");
            writer.write("formato_malo\n");
            writer.write("2;4;\n");
        }
    }
}
