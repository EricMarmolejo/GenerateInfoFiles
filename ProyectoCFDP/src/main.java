import java.io.*;
import java.util.*;

/**
 * Clase principal del sistema de gestion de ventas. Procesa los archivos de entrada
 * generados por {@link GenerateInfoFiles} y produce dos reportes CSV ordenados.
 *
 * <p>Al ejecutarse, el programa realiza las siguientes tareas:</p>
 * <ol>
 *   <li>Carga la informacion de productos desde {@code productos.txt}.</li>
 *   <li>Carga la informacion de vendedores desde {@code vendedores.txt}.</li>
 *   <li>Procesa todos los archivos {@code ventas_*.txt} del directorio actual,
 *       acumulando totales por vendedor y por producto.</li>
 *   <li>Genera {@code reporte_vendedores.csv} con vendedores ordenados de mayor
 *       a menor por dinero recaudado.</li>
 *   <li>Genera {@code reporte_productos.csv} con productos ordenados de mayor
 *       a menor por cantidad vendida.</li>
 * </ol>
 *
 * <p><b>Extras implementados:</b></p>
 * <ul>
 *   <li>Extra A: soporta multiples archivos de ventas por vendedor; los totales
 *       se acumulan correctamente entre todos los archivos del mismo vendedor.</li>
 *   <li>Extra C: detecta y reporta en consola lineas con formato incorrecto,
 *       IDs de producto inexistentes y cantidades negativas, continuando el
 *       procesamiento sin interrumpirse.</li>
 * </ul>
 *
 * @author Eric Camilo Marmolejo B.
 * @author Juan Ortega Gomez
 * @author David Esteban Urquijo
 * @version 2.0
 */
public class main {

    /**
     * Punto de entrada del programa. Coordina la carga de datos, el procesamiento
     * de ventas y la generacion de reportes.
     *
     * @param args argumentos de linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        try {
            Map<Integer, String> productNames = new HashMap<>();
            Map<Integer, Double> productPrices = new HashMap<>();
            loadProducts(productNames, productPrices);

            Map<String, String> sellers = loadSellers();

            Map<String, Double> sellerTotals = new HashMap<>();
            Map<Integer, Integer> productTotals = new HashMap<>();

            File folder = new File(".");
            File[] files = folder.listFiles();

            if (files == null) {
                System.out.println("Error: no se pudo acceder al directorio de trabajo.");
                return;
            }

            for (File file : files) {
                if (file.getName().startsWith("ventas_") && file.getName().endsWith(".txt")) {
                    processSalesFile(file, productPrices, productNames, sellerTotals, productTotals);
                }
            }

            generateSellerReport(sellers, sellerTotals);
            generateProductReport(productNames, productPrices, productTotals);

            System.out.println(">>>===  Proceso completado correctamente  ===<<<");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Carga la informacion de productos desde el archivo {@code productos.txt}.
     *
     * <p>Cada linea del archivo debe tener el formato:</p>
     * <pre>IDProducto;NombreProducto;PrecioPorUnidad</pre>
     *
     * @param names  mapa donde se almacenan los nombres de productos, indexado por ID
     * @param prices mapa donde se almacenan los precios de productos, indexado por ID
     * @throws IOException si el archivo no existe o no puede leerse
     */
    static void loadProducts(Map<Integer, String> names,
                             Map<Integer, Double> prices) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader("productos.txt"))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(";");

                if (parts.length < 3) {
                    System.out.println("ADVERTENCIA [productos.txt:" + lineNumber
                            + "]: linea con formato incorrecto, se omite: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                names.put(id, name);
                prices.put(id, price);
            }
        }
    }

    /**
     * Carga la informacion de vendedores desde el archivo {@code vendedores.txt}.
     *
     * <p>Cada linea del archivo debe tener el formato:</p>
     * <pre>TipoDocumento;NumeroDocumento;Nombres;Apellidos</pre>
     *
     * @return mapa con el nombre completo de cada vendedor, indexado por numero de documento
     * @throws IOException si el archivo no existe o no puede leerse
     */
    static Map<String, String> loadSellers() throws IOException {
        Map<String, String> sellers = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("vendedores.txt"))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(";");

                if (parts.length < 4) {
                    System.out.println("ADVERTENCIA [vendedores.txt:" + lineNumber
                            + "]: linea con formato incorrecto, se omite: " + line);
                    continue;
                }

                String id = parts[1].trim();
                String fullName = parts[2].trim() + " " + parts[3].trim();
                sellers.put(id, fullName);
            }
        }

        return sellers;
    }

    /**
     * Procesa un archivo de ventas y acumula los totales por vendedor y por producto.
     *
     * <p>El archivo debe comenzar con una linea de encabezado con el formato:</p>
     * <pre>TipoDocumento;NumeroDocumento</pre>
     * <p>Las lineas siguientes representan ventas con el formato:</p>
     * <pre>IDProducto;Cantidad;</pre>
     *
     * <p><b>Extra A:</b> si el mismo vendedor tiene varios archivos de ventas, este
     * metodo acumula los totales correctamente gracias a {@code getOrDefault}.</p>
     *
     * <p><b>Extra C:</b> detecta y reporta en consola los siguientes errores sin
     * interrumpir el procesamiento:</p>
     * <ul>
     *   <li>Lineas con formato incorrecto (menos de 2 campos)</li>
     *   <li>IDs de producto que no existen en el catalogo</li>
     *   <li>Cantidades negativas o igual a cero</li>
     * </ul>
     *
     * @param file          archivo de ventas a procesar
     * @param productPrices mapa de precios de productos indexado por ID
     * @param productNames  mapa de nombres de productos indexado por ID (para validacion)
     * @param sellerTotals  mapa acumulador de ingresos por vendedor (modificado en sitio)
     * @param productTotals mapa acumulador de cantidades vendidas por producto (modificado en sitio)
     * @throws IOException si el archivo no puede leerse
     */
    static void processSalesFile(File file,
                                 Map<Integer, Double> productPrices,
                                 Map<Integer, String> productNames,
                                 Map<String, Double> sellerTotals,
                                 Map<Integer, Integer> productTotals) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("ADVERTENCIA [" + file.getName() + "]: archivo vacio, se omite.");
                return;
            }

            String[] header = headerLine.split(";");
            if (header.length < 2) {
                System.out.println("ADVERTENCIA [" + file.getName()
                        + "]: encabezado con formato incorrecto, se omite el archivo.");
                return;
            }

            String sellerId = header[1].trim();
            double accumulatedTotal = sellerTotals.getOrDefault(sellerId, 0.0);

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                String[] parts = line.split(";");
                if (parts.length < 2) {
                    System.out.println("ADVERTENCIA [" + file.getName() + ":" + lineNumber
                            + "]: formato incorrecto, se omite la linea: " + line);
                    continue;
                }

                int productId;
                int quantity;

                try {
                    productId = Integer.parseInt(parts[0].trim());
                    quantity = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    System.out.println("ADVERTENCIA [" + file.getName() + ":" + lineNumber
                            + "]: valores no numericos, se omite la linea: " + line);
                    continue;
                }

                if (!productNames.containsKey(productId)) {
                    System.out.println("ADVERTENCIA [" + file.getName() + ":" + lineNumber
                            + "]: ID de producto inexistente (" + productId + "), se omite la linea.");
                    continue;
                }

                if (quantity <= 0) {
                    System.out.println("ADVERTENCIA [" + file.getName() + ":" + lineNumber
                            + "]: cantidad invalida (" + quantity + ") para producto " + productId
                            + ", se omite la linea.");
                    continue;
                }

                double price = productPrices.get(productId);
                accumulatedTotal += price * quantity;

                int currentTotal = productTotals.getOrDefault(productId, 0);
                productTotals.put(productId, currentTotal + quantity);
            }

            sellerTotals.put(sellerId, accumulatedTotal);
        }
    }

    /**
     * Genera el archivo {@code reporte_vendedores.csv} con los vendedores ordenados
     * de mayor a menor por dinero recaudado.
     *
     * <p>Cada linea del archivo tiene el formato:</p>
     * <pre>NombreCompleto;TotalDineroRecaudado</pre>
     *
     * @param sellers mapa de nombres completos de vendedores indexado por ID
     * @param totals  mapa de totales de ingresos por vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    static void generateSellerReport(Map<String, String> sellers,
                                     Map<String, Double> totals) throws IOException {

        List<Map.Entry<String, Double>> sellerList = new ArrayList<>();

        for (String id : sellers.keySet()) {
            double total = totals.getOrDefault(id, 0.0);
            sellerList.add(new AbstractMap.SimpleEntry<>(id, total));
        }

        sellerList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        try (FileWriter writer = new FileWriter("reporte_vendedores.csv")) {
            for (Map.Entry<String, Double> entry : sellerList) {
                String name = sellers.get(entry.getKey());
                writer.write(name + ";" + entry.getValue() + "\n");
            }
        }
    }

    /**
     * Genera el archivo {@code reporte_productos.csv} con los productos ordenados
     * de mayor a menor por cantidad total vendida.
     *
     * <p>Cada linea del archivo tiene el formato:</p>
     * <pre>NombreProducto;PrecioPorUnidad;CantidadTotalVendida</pre>
     *
     * @param names   mapa de nombres de productos indexado por ID
     * @param prices  mapa de precios de productos indexado por ID
     * @param totals  mapa de cantidades totales vendidas por producto
     * @throws IOException si ocurre un error al escribir el archivo
     */
    static void generateProductReport(Map<Integer, String> names,
                                      Map<Integer, Double> prices,
                                      Map<Integer, Integer> totals) throws IOException {

        List<Map.Entry<Integer, Integer>> productList = new ArrayList<>(totals.entrySet());

        productList.sort((a, b) -> b.getValue() - a.getValue());

        try (FileWriter writer = new FileWriter("reporte_productos.csv")) {
            for (Map.Entry<Integer, Integer> entry : productList) {
                int productId = entry.getKey();
                String name = names.get(productId);
                double price = prices.get(productId);
                int quantitySold = entry.getValue();
                writer.write(name + ";" + price + ";" + quantitySold + "\n");
            }
        }
    }
}
