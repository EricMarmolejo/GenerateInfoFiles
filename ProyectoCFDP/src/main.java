import java.io.*;
import java.util.*;

public class main {

    public static void main(String[] args) {
        try {
            Map<Integer, String> productNames = new HashMap<>();
            Map<Integer, Double> productPrices = new HashMap<>();

            loadProducts(productNames, productPrices);
            Map<String, String> sellers = loadSellers();

            Map<String, Double> sellerTotals = new HashMap<>();
            Map<Integer, Integer> productTotals = new HashMap<>();

            File folder = new File(".");

            for (File file : folder.listFiles()) {
                if (file.getName().startsWith("ventas_")) {
                    processSalesFile(file, productPrices, sellerTotals, productTotals);
                }
            }

            generateSellerReport(sellers, sellerTotals);
            generateProductReport(productNames, productPrices, productTotals);

            System.out.println("Proceso completado");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Cargar productos (ahora incluye nombre)
    static void loadProducts(Map<Integer, String> names,
                             Map<Integer, Double> prices) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("productos.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");

            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);

            names.put(id, name);
            prices.put(id, price);
        }

        br.close();
    }

    // Cargar vendedores
    static Map<String, String> loadSellers() throws IOException {
        Map<String, String> map = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader("vendedores.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            String id = parts[1];
            String name = parts[2] + " " + parts[3];
            map.put(id, name);
        }

        br.close();
        return map;
    }

    // Procesar ventas
    static void processSalesFile(File file,
                                Map<Integer, Double> productPrices,
                                Map<String, Double> sellerTotals,
                                Map<Integer, Integer> productTotals) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        String[] header = line.split(";");
        String sellerId = header[1];

        double total = sellerTotals.getOrDefault(sellerId, 0.0);

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");

            int productId = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);

            double price = productPrices.getOrDefault(productId, 0.0);

            total += price * quantity;

            productTotals.put(productId,
                    productTotals.getOrDefault(productId, 0) + quantity);
        }

        sellerTotals.put(sellerId, total);
        br.close();
    }

    // Reporte vendedores
    static void generateSellerReport(Map<String, String> sellers,
                                 Map<String, Double> totals) throws IOException {

    List<String> sellerIds = new ArrayList<>(sellers.keySet());

    FileWriter writer = new FileWriter("reporte_vendedores.csv");

    List<Map.Entry<String, Double>> list = new ArrayList<>();

    for (String id : sellerIds) {
        double total = totals.getOrDefault(id, 0.0);
        list.add(new AbstractMap.SimpleEntry<>(id, total));
    }

    list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

    for (Map.Entry<String, Double> entry : list) {
        String name = sellers.get(entry.getKey());
        writer.write(name + ";" + entry.getValue() + "\n");
    }

    writer.close();
}

    // Reporte productos
    static void generateProductReport(Map<Integer, String> names,
                                      Map<Integer, Double> prices,
                                      Map<Integer, Integer> totals) throws IOException {

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(totals.entrySet());

        list.sort((a, b) -> b.getValue() - a.getValue());

        FileWriter writer = new FileWriter("reporte_productos.csv");

        for (Map.Entry<Integer, Integer> entry : list) {
            int productId = entry.getKey();
            String name = names.get(productId);

            writer.write(name + ";" +
                    prices.get(productId) + ";" +
                    entry.getValue() + "\n");
        }

        writer.close();
    }
}