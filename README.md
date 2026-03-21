Java Module Project - Delivery 1
Description

This project corresponds to the first delivery of the module. It consists of a Java program that generates plain text files simulating data for products, salesmen, and sales. These files will be used as input for the next phase of the project, where the data will be processed and analyzed.

The main goal is to automate the creation of test data following a specific format defined in the project requirements.

Project Structure
/project
 ├── productos.txt
 ├── vendedores.txt
 ├── ventas_1001.txt
 ├── ventas_1002.txt
 ├── ventas_1003.txt
 └── src/
     └── GenerateInfoFiles.java
Implemented Class
GenerateInfoFiles

This is the main class responsible for generating all required files for the project.

Methods
main

Entry point of the program. It executes the generation of product, salesman, and sales files. It also includes basic exception handling to prevent unexpected crashes during execution.

createProductsFile(int productsCount)

Generates the productos.txt file with a specified number of products. Each line follows this format:

ID;ProductName;Price
createSalesManInfoFile(int salesmanCount)

Generates the vendedores.txt file containing salesman information. Each line follows this format:

DocumentType;DocumentNumber;FirstName;LastName
createSalesMenFile(int randomSalesCount, long id)

Generates a sales file for each salesman. The format is:

DocumentType;DocumentNumber
ProductID;Quantity;
How to Run
Open the project in Eclipse
Run the GenerateInfoFiles class
Verify that the .txt files are created in the project root folder
Important Notes
All generated files strictly follow the required format
No user input is required
Data is generated pseudo-randomly
Each salesman has an individual sales file
Technologies
Java 8
Eclipse IDE
Status

Delivery 1 completed. File generation working correctly.
