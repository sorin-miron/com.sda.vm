package com.sda.vm.service;

import com.sda.vm.model.Coin;
import com.sda.vm.model.CurrencyType;
import com.sda.vm.model.Product;

import java.util.Map;
import java.util.Scanner;

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
            displayMessage("Choose your destiny / product ! :) ");
            productCode = scanner.nextInt();
            for (Product product : productStock.keySet()) {
                if (product.getCod().equals(productCode)) {
                    exitProductMenu = true;
                    displayMessage("You have selected this product: " + product.getName() + " " + product.getSize() + ". \n");
                }
            }
        } while (!exitProductMenu);
        return productCode; // codul produsului selectat de client
    }

}