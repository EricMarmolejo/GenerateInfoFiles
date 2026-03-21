import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    static Random random = new Random();

    public static void main(String[] args) {
        try {
            createProductsFile(10);
            createSalesManInfoFile(5);

            for (int i = 1; i <= 5; i++) {
                createSalesMenFile(5, 1000 + i);
            }

            System.out.println(">>>===  Archivos generados correctamente  ===<<<");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void createProductsFile(int productsCount) throws IOException {
        FileWriter writer = new FileWriter("productos.txt");

        for (int i = 1; i <= productsCount; i++) {
            writer.write(i + ";Producto" + i + ";" + (random.nextInt(500) + 50) + "\n");
        }

        writer.close();
    }

    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        FileWriter writer = new FileWriter("vendedores.txt");

        String[] nombres = {"Juan", "Maria", "Pedro", "Ana", "Luis", "Carlos", "Laura", "Andres", "Sofia", "Diego"};
        String[] apellidos = {"Perez", "Gomez", "Ruiz", "Lopez", "Torres", "Martinez", "Castro", "Ramirez", "Vargas", "Moreno"};

        for (int i = 1; i <= salesmanCount; i++) {
            String nombre = nombres[random.nextInt(nombres.length)];
            String apellido = apellidos[random.nextInt(apellidos.length)];

            writer.write("CC;" + (1000 + i) + ";" + nombre + ";" + apellido + "\n");
        }

        writer.close();
    }

    public static void createSalesMenFile(int randomSalesCount, long id) throws IOException {
        FileWriter writer = new FileWriter("ventas_" + id + ".txt");

        writer.write("CC;" + id + "\n");

        for (int i = 0; i < randomSalesCount; i++) {
            int productId = random.nextInt(10) + 1;
            int quantity = random.nextInt(10) + 1;

            writer.write(productId + ";" + quantity + ";\n");
        }

        writer.close();
    }
}