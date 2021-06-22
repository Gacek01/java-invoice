package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import pl.edu.agh.mwo.invoice.product.BottleOfWine;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Map<Product, Integer> products = new HashMap<Product, Integer>();
    private final int invoiceNumber = Math.abs(new Random().nextInt());
    private LocalDate issueDate = LocalDate.now();
    private final LocalDate carrierDayHoliday = LocalDate.of(2021, 4, 26);

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        if (products.get(product) == null) {
            products.put(product, quantity);
        } else {
            products.put(product, products.get(product) + quantity);
        }
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }
    
    public void printInvoice() {
        System.out.println("Invoice date: " + issueDate);
        System.out.println("Invoice number: " + this.getInvoiceNumber());
        for (Product product : products.keySet()) {
            System.out.println("Product name: " + product.getName()
                + "| quantity: " + products.get(product)
                + "| unit price: " + carrierDayPriceChange(product));
        }
        System.out.println("Total number of items: " + products.size());
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }
    
    public int getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(int year, int month, int day) {
        issueDate = LocalDate.of(year, month, day);
    }
    
    public BigDecimal carrierDayPriceChange(Product product) {
        if (product instanceof FuelCanister && issueDate.getDayOfMonth()
            == carrierDayHoliday.getDayOfMonth() && issueDate.getMonthValue()
            == carrierDayHoliday.getMonthValue()) {
            BigDecimal reducedPrice = product.getPriceWithTax()
                    .subtract(product.getPrice().multiply(product.getTaxPercent()));
            return reducedPrice;
        }
        return product.getPriceWithTax();
    }
}
