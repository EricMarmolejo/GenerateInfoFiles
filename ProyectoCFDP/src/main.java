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
