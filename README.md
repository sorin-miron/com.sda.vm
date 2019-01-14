# com.sda.vm
Functionalitate
---------------

- meniu
- insert coins [1, 5, 10]
- select product (doar cate unul)
- deliver product
- [OPTIONAL] Select Product -> daca mai are bani se poate duce la Select product
- pay Rest -> apoi se intoarce la Meniu



Ce se afiseaza la meniu:
------------------------

cod	Name	Price	Size
-----------------------------
101	Cola	10 lei	0.5l
102	Cola	5 lei	0.25l



Ce obiecte avem nevoie ?
------------------------

PACHET MODEL
- Product
-- cod
-- name
-- price
-- size (0.5l, 0.25l)


- Coin
-- cod (201, 202, 203)
-- valoare (1, 5, 10)

- Currency (o sa fie enum)
-- RON
-- EUR

public enum CurrencyType(){
final static String RON, EUR;
}

CurrencyType.RON.toString();


Ce clase avem nevoie ?
----------------------

- Map<Product product, Integer noOfProduct> productStock
- Map<Coin coin, Integer noOfCoins> coinStock

PACHET SERVICE
- IOService -> comunicare cu consola
-- displayProductStock()
-- displayCoinStock()
-- displayMessage()
-- integer readUserInput()


PACHET VENDINGMACHINE
- VendingMachine
-- productStock
-- coinStock
-- IOService
-- currency (doar una)

-- [OPIONAL] start() -> initializare din fisier.

-- void run(){
--- IOService.displayProductMenu(productStock);
--- IOService.displayCoinMenu(coinStock) --> lista de monezi care pot fi introduse
--- sum = this.insertCoins();
--- IOService.displayProductMenu(productMenu);
--- option = this.selectProduct();
--- rest == this.deliverProduc(option, suma);
--- [OPTIONAL] Select Product -> daca mai are bani se poate duce la Select product
--- this.payRest();
--- [OPTIONAL] this.saveState(); --> salvare stare stocuri si monezi in fisier (salvam in format JSON, utilizand GSON de la google)
--- this.run();
}

-- Integer insertCoins(); --> suma
-- Integer selectProduct(); --> codul produsului
-- Integer deliverProduct(option, suma); --> restul daca am suficienti bani
-- payRest(rest);
