package com.sda.vm.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sda.vm.model.Coin;
import com.sda.vm.model.CurrencyType;
import com.sda.vm.model.Product;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IOService {

    public void displayProductMenu(Map<Product, Integer> productStock) {
        // afiseaza lista de produse - DE FACUT CU FORMAT  !!!!
        displayMessage("This is our wonderful list of products: \n");
        displayMessage(String.format("%4s \t %-15s \t %7s \t %7s \t %7s \t",
                "Code", "Product Name", "Size", "Price", "Stock"));
        displayMessage("------------------------------------------------------------");
        for (Product product : productStock.keySet()) {
            displayMessage(String.format("%4s \t %-15s \t %7s \t %7s \t %7s \t",
                    product.getCod().toString(),
                    product.getName(),
                    product.getSize(),
                    product.getPrice().toString(),
                    productStock.get(product).toString()
            ));
        }
        displayMessage("");
    }

    public void displayCoinMenu(Map<Coin, Integer> coinStock, CurrencyType currencyType) {
        // afiseaza tipurile de valuta si tipurile de bacnote
        displayMessage("You can insert this kind of money:");
        displayMessage(String.format("%4s \t %7s \t %7s \t",
                "Code", "Value", "Stock"));
        displayMessage("----------------------------");
        for (Coin coin : coinStock.keySet()) {
            displayMessage(String.format("%4s \t %7s \t %7s \t",
                    coin.getCod().toString(),
                    coin.getValue().toString() + " " + currencyType.toString(),
                    coinStock.get(coin)
            ));
        }
        displayMessage("");
    }

    public void displayMessage(String message) {
        // afiseaza un mesaj
        System.out.println(message);
    }

    public Integer readUserInput(Map<Product, Integer> productStock) {
        Integer productCode = 0;
        // citeste de la tastatura
        Scanner scanner = new Scanner(System.in);
        boolean exitProductMenu = false;
        do {
            displayMessage("Choose your destiny (product code): ");
            productCode = scanner.nextInt();
            for (Product product : productStock.keySet()) {
                if (product.getCod().equals(productCode)) {
                    exitProductMenu = true;
                    displayMessage("You have selected: " + product.getName() + " " + product.getSize() + ". \n");
                }
            }
        } while (!exitProductMenu);
        return productCode; // codul produsului selectat de client
    }

    public String readUserOptionToContinue(Integer rest, CurrencyType currencyType){
        // citeste de la tastatura
        Scanner scanner = new Scanner(System.in);
        displayMessage("You have " + rest + " " + currencyType.toString() + "." +
                " Do you want to select another product ? (Yes / No)");
        return scanner.next();
    }

    // salveaza starea aplicatiei in fisiere .TXT
    public void saveState(Map<Product, Integer> productStock, Map<Coin, Integer> coinStock, CurrencyType currencyType){
        // utilizare gson de la google pentru SERIALIZARE-a unui obiect
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();
        String productStockJson = gson.toJson(productStock);
        String coinStockJson = gson.toJson(coinStock);
        // scriere in fisiere separate pentru stoc produse, stoc coin si stare currency
        writeToFile("src/main/java/com/sda/vm/utils/productsStockJson.txt", productStockJson);
        writeToFile("src/main/java/com/sda/vm/utils/coinsStockJson.txt", coinStockJson);
        writeToFile("src/main/java/com/sda/vm/utils/utils.txt", "Currency: " + currencyType.toString());
    }

    // scrie un String in fisierul <filePath>
    private void writeToFile(String filePath, String textToWriteInFile){
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(textToWriteInFile);
        } catch (IOException e){
            displayMessage("File write error");
        }
    }

    // citeste corect din fisier si returneaza un String care contine JSON-ul meu
    private String readFromFile(String filePath){
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e){
            displayMessage("File reader error");
        }
        return "";
    }

    // citire stoc produse din fisier JSON
    public Map<Product, Integer> readProductsStocksFromJSONFile(String filePath){
//        Map<T, Integer> stockFromJSONFile2;
//          Am incercat sa fac cu generics aceasta metoda dar inca nu i-am dat de cap.
//          Cred ca nu recunoaste tipul clasei la TypeToken<Map<T, Integer>>().getType
//          si genereaza aiurea JSON-ul. Asa ca am pastrat varianta functionala, chiar daca se dubleaza codul...
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        Type mapType = new TypeToken<Map<Product, Integer>>(){}.getType();
        return new Gson().fromJson(readFromFile(filePath), mapType);
    }

    // citire stoc monede din fisier JSON
    public Map<Coin, Integer> readCoinsStocksFromJSONFile(String filePath) {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        Type mapType = new TypeToken<Map<Coin, Integer>>() {
        }.getType();
        return new Gson().fromJson(readFromFile(filePath), mapType);
    }

    // citire tip valuta din fisierul utils.txt
    public CurrencyType readCurrencyTypeFromFile(String filePath){
        CurrencyType currencyType;
        switch (readFromFile(filePath).substring(10,13).toUpperCase()){
            case "EUR":
                currencyType = CurrencyType.EUR;
                break;
            case "RON":
            default:
                currencyType = CurrencyType.RON;  // valoare default daca fisierul nu este construit cum trebuie
        }
        return currencyType;
    }

}
