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
        this.productStock = new LinkedHashMap<>();
        Product p1 = new Product(101, "apa plata", 1, "0.50L");
        Product p2 = new Product(102, "apa minerala", 2, "0.50L");
        Product p3 = new Product(103, "cola", 3, "0.50L");
        Product p4 = new Product(104, "cola", 4, "0.25L");
        Product p5 = new Product(105, "fanta", 3, "0.50L");
        this.productStock.put(p1, 10);
        this.productStock.put(p2, 9);
        this.productStock.put(p3, 10);
        this.productStock.put(p4, 10);
        this.productStock.put(p5, 10);

//        initializare lista monede
        this.coinStock = new LinkedHashMap<>();
        Coin c1 = new Coin(101, 1);
        Coin c2 = new Coin(102, 5);
        Coin c3 = new Coin(103, 10);
        this.coinStock.put(c1, 5);
        this.coinStock.put(c2, 2);
        this.coinStock.put(c3, 10);

//        initializare servicii
        this.machineService = new IOService();

//        initializare tip valuta
        this.currencyType = CurrencyType.RON;
    }

    public void run() {
        machineService.displayProductMenu(this.productStock); // afiseaza stocul de produse
        machineService.displayCoinMenu(this.coinStock, this.currencyType); // tipurile de bacnote care pot fi utilizate + valuta
        Integer sum = this.insertCoins(); // suma introdusa de client
        machineService.displayProductMenu(this.productStock); // afiseaza stocul de produse
        Integer option = this.selectProduct(); // codul produsului dorit de utilizator
        Integer rest = this.deliverProduct(option, sum); // livreaza produsul daca este in stoc
        this.payRest(rest); // plateste restul daca sunt suficienti bani
//        this.saveState(); --> salvare stare stocuri si monezi in fisier, in format JSON, utilizand GSON de la google
        this.run();
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
                this.updateCoinStock(this.coinStock, coin, 1); // introduc moneda in stock
            }
        } while (!exitCoinMenu);
        machineService.displayMessage("You have entered " + sum + " " + this.currencyType + ".\n");
        return sum; // suma introdusa de client
    }

    private Integer selectProduct() {
        return machineService.readUserInput(productStock); // codul produsului selectat
    }

    private Integer deliverProduct(Integer option, Integer sum) {
        int rest = 0;
        for (Product product : productStock.keySet()) {
            if (product.getCod().equals(option)) {                 // am gasit produsul dorit
                if (product.getPrice() <= sum) {               // a introdus suficienti bani
                    if (productStock.get(product) > 0) {      // stocul produsului este suficient
                        // livrare produs
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
        int initialRest = rest;
        System.out.println("You must receive " + rest + " " + currencyType + ". \n");
        int[] coins = {10, 5, 1};
        for (int i = 0; i < 3; i++) {
            for (Coin coin : coinStock.keySet()) {
                if (coin.getValue().equals(coins[i])) {
                    while (rest >= coins[i] && coinStock.get(coin) > 0) {
                        this.updateCoinStock(this.coinStock, coins[i], -1); // scot o moneda din stock
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
}