package com.sda.vm.machine;

import com.sda.vm.model.Coin;
import com.sda.vm.model.CurrencyType;
import com.sda.vm.model.Product;
import com.sda.vm.service.IOService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class VandingMachine {
    private Map<Product, Integer> productStock;
    private Map<Coin, Integer> coinStock;
    private IOService machineService;
    private CurrencyType currencyType;

/*    void start(){
        // initializare din fisier
    }*/

    public VandingMachine() {
//        initializare lista produse
        productStock = new LinkedHashMap<>();
        Product p1 = new Product(101, "apa plata", 1, "0.50L");
        Product p2 = new Product(102, "apa minerala", 2, "0.50L");
        Product p3 = new Product(103, "cola", 3, "0.50L");
        Product p4 = new Product(104, "cola", 4, "0.25L");
        Product p5 = new Product(105, "fanta", 3, "0.50L");
        productStock.put(p1, 10);
        productStock.put(p2, 9);
        productStock.put(p3, 10);
        productStock.put(p4, 10);
        productStock.put(p5, 10);


/*      // AICI m-am oprit cu citirea starii din fisier :) Trebuie sa mai studiez si o fac pana la urma
        try {
            productStock IL INITIALIZEZ DIN machineService.loadState("src/main/java/com/sda/vm/utils/productStockJson.txt");
        } catch (NullPointerException e){
            System.out.println("Eroare: " + e.toString());
        }*/

//        initializare lista monede
        coinStock = new LinkedHashMap<>();
        Coin c1 = new Coin(101, 1);
        Coin c2 = new Coin(102, 5);
        Coin c3 = new Coin(103, 10);
        coinStock.put(c1, 5);
        coinStock.put(c2, 2);
        coinStock.put(c3, 10);

//        initializare servicii
        machineService = new IOService();

//        initializare tip valuta
        currencyType = CurrencyType.RON;
    }

    public void run() {
        Integer sum = 0;
        Integer option;
        Integer rest = 0;
        Boolean selectAnotherProduct = false;

        machineService.displayProductMenu(productStock); // afiseaza stocul de produse
        machineService.displayCoinMenu(coinStock, currencyType); // tipurile de bacnote care pot fi utilizate + valuta
        while(sum <= 0) {   // nu-l las sa treaca mai departe daca nu introduce nici un ban
            sum = insertCoins(); // suma introdusa de client
        }
        machineService.displayProductMenu(productStock); // afiseaza din nou stocul de produse
        do {
            option = selectProduct(); // codul produsului dorit de utilizator
            rest = deliverProduct(option, sum); // livreaza produsul daca este in stoc
            sum = rest; // actualizare suma de bani disponibila pentru a putea reapela deliverProduct cu restul de bani ramasi
            selectAnotherProduct = selectAnotherProduct(rest);
        } while (selectAnotherProduct); // [OPTIONAL] Select Product -> daca mai are bani se poate duce la Select product
        payRest(rest); // plateste restul daca sunt suficienti bani

        // [OPTIONAL.level 2 :D ] salvare stare stocuri si monezi in fisier, in format JSON, utilizand GSON de la google
        // stocul de produse este salvat in fisierul productStockJson.txt
        // stocul de bani este salvat in fisierul coinsStockJson.txt
        // valuta curenta este salvata in fisierul utils.txt
        // daca fisierele nu exista le creaza la prima salvare. Directorul UTILS trebuie sa existe in com.sda.vm
        machineService.saveState(productStock, coinStock, currencyType);

        run();
    }

    private Integer insertCoins() {
        Scanner scanner = new Scanner(System.in);
        int sum = 0;
        boolean exitCoinMenu = false;
        do {
            machineService.displayMessage("Please insert coin (1, 5, 10). Type 7 to finish.");
            int coin = scanner.nextInt();
            while (coin != 1 && coin != 5 && coin != 10 && coin != 7) {
                machineService.displayMessage("Please insert the correct coin (1, 5, 10). Type 7 to finish.");
                coin = scanner.nextInt();
            }
            if (coin == 7) {
                exitCoinMenu = true;
            } else {
                sum += coin;
                updateCoinStock(coinStock, coin, 1); // introduc moneda in stock
            }
        } while (!exitCoinMenu);
        machineService.displayMessage("You have entered " + sum + " " + currencyType + ".\n");
        return sum; // suma introdusa de client
    }

    private Integer selectProduct() {
        return machineService.readUserInput(productStock); // codul produsului selectat
    }

    private Integer deliverProduct(Integer option, Integer sum) {
        int rest = sum; // daca nu-i ajung banii pentru produs, restul devine suma disponibila inainte de selectare produs
        for (Product product : productStock.keySet()) {
            if (product.getCod().equals(option)) {                 // am gasit produsul dorit
                if (product.getPrice() <= sum) {               // a introdus suficienti bani
                    if (productStock.get(product) > 0) {      // stocul produsului este suficient
                        // livrare produs
                        machineService.displayMessage("You have received the product ! :)");
                        // actualizare stoc produs
                        productStock.put(product, productStock.get(product) - 1);
                        // actualizare rest
                        rest = sum - product.getPrice();
                    }
                }
            }
        }
        return rest;
    }

    private void payRest(Integer rest) {
        // scade restul din stocul de monezi
        int initialRest = rest; // pastrez variabila rest pentru a putea afisa cat a primit din rest :D
        System.out.println("You must receive " + rest + " " + currencyType + ". \n");
        int[] coins = {10, 5, 1};
        for (int i = 0; i < 3; i++) {
            for (Coin coin : coinStock.keySet()) {
                if (coin.getValue().equals(coins[i])) {
                    while (rest >= coins[i] && coinStock.get(coin) > 0) {
                        updateCoinStock(coinStock, coins[i], -1); // scot o moneda din stock
                        rest -= coins[i];  // scad moneda si din rest
                    }
                }
            }
        }
        initialRest -= rest; // suma primita din rest
        System.out.println("You received " + initialRest + " " + currencyType + " :D Thank you ! Bye Bye ! :) \n \n \n");
    }

    // modifica valoarea stocului de monede cu +/-1
    private void updateCoinStock(Map<Coin, Integer> coinStock, Integer receivedCoin, Integer plusMinusOne) {
        for (Coin coin : coinStock.keySet()) {
            if (coin.getValue().equals(receivedCoin)) {
                coinStock.put(coin, coinStock.get(coin) + plusMinusOne);
            }
        }
    }

    // loop pe alegere produs
    private boolean selectAnotherProduct(Integer rest){
        boolean selectAnotherProduct = false;
        if(rest > 0){
            if(machineService.readUserOptionToContinue(rest, currencyType).toUpperCase().compareTo("YES") == 0){
                selectAnotherProduct = true;
            }
        }
        return selectAnotherProduct;
    }
}
