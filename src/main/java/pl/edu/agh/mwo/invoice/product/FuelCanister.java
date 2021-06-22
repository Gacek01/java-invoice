package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class FuelCanister extends Product {

    private final BigDecimal exciseTax;

    public FuelCanister(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"));
        exciseTax = new BigDecimal("5.56");
    }

    public BigDecimal getPriceWithTax() {
        return super.getPriceWithTax().add(exciseTax);
    }

    public BigDecimal getExcise() {
        return exciseTax;
    }
}