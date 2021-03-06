package com.company;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main1 {
    public static void main(String[] args) throws IOException, NullPointerException {

        ArrayList<Trader> traders = new ArrayList<>();
        ArrayList<Order> OrderBook = new ArrayList<>();
        StockExchange stockExchange = new StockExchange();
        boolean order = false;
        File file = new File("Output.txt");
        PrintWriter printWriter = new PrintWriter(file);
        Scanner scanner = new Scanner(new File("C:\\Users\\MEHAK AGRAWAL\\Desktop\\SampleInput.txt"));

        while (scanner.hasNextLine()) {
            String Line = scanner.nextLine();
            String[] LineSplit = Line.split("[,:{} ]");

            switch (LineSplit[0]) {
                case "Add":
                    switch (LineSplit[1]) {
                        case "scrip" -> {
                            StockExchange.addStocks(
                                    LineSplit[3], LineSplit[7],
                                    Double.parseDouble(LineSplit[10]), Double.parseDouble(LineSplit[13]),
                                    Double.parseDouble(LineSplit[16]), Double.parseDouble(LineSplit[19])
                            );
                            printWriter.println("Added scrip: " + LineSplit[3] + " has been initiated with class Stock");
                        }
                        case "user" -> {
                            traders.add(new Trader(LineSplit[3], Double.parseDouble(LineSplit[6]), createHashMap(Line)));
                            printWriter.println("Added user: " + LineSplit[3] + " has been initiated with class Trader");
                        }
                    }
                    break;

                case "Place":
                    String name = LineSplit[5];
                    String type = LineSplit[9];
                    String scrip = LineSplit[13];
                    double qty = Double.parseDouble(LineSplit[16]);
                    double rate = Double.parseDouble(LineSplit[20]);
                    for (Trader i : traders) {
                        if (name.equals(i.name)) {
                            if (type.equals("sell")) {
                                if (stockExchange.checkTicker(scrip) && i.holdings.get(scrip) >= qty) {
                                    OrderBook.add(new Order(name, type, scrip, qty, rate));
                                }
                            } else if (type.equals("buy")) {
                                if (i.funds > qty * rate) {
                                    OrderBook.add(new Order(name, type, scrip, qty, rate));
                                }
                            }
                        }
                    }
                    printWriter.println("Order placed: " + Line);
                    break;

                case "Show":
                    switch (LineSplit[1]) {
                        case "Orderbook" -> printWriter.println(OrderBook);
                        case "sector" -> {
                            String sector = LineSplit[3];
                            printWriter.println("Showing stocks by of " + sector + " sector");
                            for (int i = 0; i < stockExchange.NSE.size(); i++) {
                                if (stockExchange.NSE.get(i).type.equals(sector)) {
                                    printWriter.println(stockExchange.NSE.get(i).toString());
                                }
                            }
                        }
                        case "Scrips" -> {
                            printWriter.println("Showing scrips: \n");
                            for (int i = 0; i < stockExchange.NSE.size(); i++)
                                printWriter.println(StockExchange.NSE.get(i).toString());
                        }
                        case "Users" -> {
                            printWriter.println("Showing Users: \n");
                            for (Trader i : traders) {
                                printWriter.println(i.toString());
                            }
                        }
                    }
                case "Delete":
                    switch (LineSplit[1]) {
                        case "scrip" -> {
                            printWriter.println("Scrip Deleted: " + LineSplit[3]);
                            StockExchange.NSE.remove(LineSplit[3]);
                            for (Trader i : traders)
                                i.holdings.remove(LineSplit[3]);
                        }
                        case "User" -> {
                            printWriter.println("User deleted: " + LineSplit[3]);
                            traders.remove(LineSplit[3]);
                            for (int i = 0; i < StockExchange.NSE.size(); i++) {
                                StockExchange.NSE.remove(LineSplit[3]);
                            }
                        }
                    }
                case "Exit":
                    printWriter.println("Market Closed");
            }
        }
        printWriter.close();
    }
    public void execute(ArrayList<Order> OrderBook,ArrayList<Trader> traders,StockExchange stockExchange) {
        ArrayList<Order> BuyOrder = new ArrayList<>();
        ArrayList<Order> SellOrder = new ArrayList<>();
        double qtyNet = 0.0;
        Stock stock;
        for (Order i : OrderBook) {
            if (i.type.equals("buy")) {
                BuyOrder.add(i);
            }
            if (i.type.equals("sell")) {
                SellOrder.add(i);
            }
        }
        for (Order i : BuyOrder) {
            for (Order j : SellOrder) {
                if (i.scrip.equals(j.scrip)) {
                    String scrip = i.scrip;
                    double askPrice = j.getRate();
                    double bidPrice = i.getRate();
                    double qty1 = i.getQty(); //Quantity req by buyer
                    double qty2 = j.getQty(); //Quantity by seller
                    if (askPrice <= bidPrice) {
                        if (qty1 >= qty2) {
                            qtyNet = qty2;
                        } else if (qty1 < qty2) {
                            qtyNet = qty1;
                        }
                        Trader buyer;
                        Trader seller;
                        for( Trader k: traders){
                            if(i.name.equals(k.name))
                                buyer = k;
                        }

                        for( Trader k: traders){
                            if(j.name.equals(k.name))
                                seller = k;
                        }

                        for(int l=0;l<stockExchange.NSE.size();l++){
                            if(stockExchange.NSE.get(l).ticker.equals(scrip)){
                                stock = stockExchange.NSE.get(l);
                            }
                        }
                    }
                }
            }
        }
    }

    public static HashMap<String, Integer> createHashMap(String string) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        if (string.endsWith("None")) {

        } else {
            String holdings = string.substring(string.indexOf("{") + 1, string.indexOf("}"));
            String[] holdingsSplit = holdings.split("[,: ]");
            for (int i = 0; i < holdingsSplit.length; i++) {
                if (holdingsSplit[i].length() == 0)
                    continue;
                else {
                    hashMap.put(holdingsSplit[i], Integer.parseInt(holdingsSplit[++i]));
                }
            }
        }
        return hashMap;
    }
}







