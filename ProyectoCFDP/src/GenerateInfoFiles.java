import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    static Random random = new Random();

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

    public static void createProductsFile(int productsCount) throws IOException {
        try (FileWriter writer = new FileWriter("productos.txt")) {
            for (int i = 1; i <= productsCount; i++) {
                int price = random.nextInt(500) + 50;
                writer.write(i + ";Producto" + i + ";" + price + "\n");
            }
        }
    }

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
