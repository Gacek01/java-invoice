package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.BottleOfWine;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }
    
    @Test
    public void testInvoiceHasNumber() {
        int invoiceNumber = invoice.getInvoiceNumber();
        Assert.assertTrue(invoiceNumber > 0);
    }
    
    @Test
    public void testTwoInvoicesHaveDifferentNumber() {
        int invoiceNumber1 = invoice.getInvoiceNumber();
        int invoiceNumber2 = new Invoice().getInvoiceNumber();
        Assert.assertNotEquals(invoiceNumber1, invoiceNumber2);
    }
    
    @Test
    public void testAddOneDuplicateProduct() {
        OtherProduct bagietka = new OtherProduct("Bagietka", new BigDecimal("6.5"));
        invoice.addProduct(bagietka);
        invoice.addProduct(bagietka);
        int quantity1 = invoice.getProducts().get(bagietka);
        invoice.addProduct(bagietka);
        int quantity2 = invoice.getProducts().get(bagietka);
        Assert.assertEquals(quantity2 - quantity1, 1);
    }
    @Test
    public void testAddManyDuplicateProducts() {
        OtherProduct bagietka = new OtherProduct("Bagietka", new BigDecimal("7"));
        invoice.addProduct(bagietka);
        int quantity1 = invoice.getProducts().get(bagietka);
        invoice.addProduct(bagietka, 7);
        int quantity2 = invoice.getProducts().get(bagietka);
        Assert.assertNotEquals(quantity2 - quantity1, quantity1);
    }
    
    @Test
    public void testAddDifferentProducts() {
        OtherProduct bagietka = new OtherProduct("Bagietka", new BigDecimal("6.25"));
        OtherProduct rogal = new OtherProduct("Rogal", new BigDecimal("3.5"));
        invoice.addProduct(bagietka);
        int quantity1 = invoice.getProducts().get(bagietka);
        invoice.addProduct(rogal);
        int quantity2 = invoice.getProducts().get(bagietka);
        Assert.assertEquals(quantity1, quantity2);
    }
    
    @Test
    public void testCarrierDayTaxReducedForFuel() {
        invoice.setIssueDate(2021, 4, 26);
        FuelCanister fuelCanister = new FuelCanister("Fuel Canister", new BigDecimal("80"));
        invoice.addProduct(fuelCanister);
        BigDecimal reducedPrice = invoice.carrierDayPriceChange(fuelCanister);
        Assert.assertEquals(fuelCanister.getPrice().add(fuelCanister.getExcise()), reducedPrice);
    }

    @Test
    public void testCasualDayTaxNotReducedforFuel() {
        invoice.setIssueDate(2021, 6, 22);
        FuelCanister fuelCanister = new FuelCanister("Fuel canister", new BigDecimal("70"));
        invoice.addProduct(fuelCanister);
        BigDecimal reducedPrice = invoice.carrierDayPriceChange(fuelCanister);
        Assert.assertNotEquals(fuelCanister.getPrice().add(fuelCanister.getExcise()), reducedPrice);
    }
    
    @Test
    public void testCarrierDayTaxNotReducedForWine() {
        invoice.setIssueDate(2021, 4, 26);
        BottleOfWine wine = new BottleOfWine("Bottle of Wine", new BigDecimal("50"));
        invoice.addProduct(wine);
        BigDecimal reducedPrice = invoice.carrierDayPriceChange(wine);
        Assert.assertEquals(wine.getPriceWithTax(), reducedPrice);
    }

    @Test
    public void testCarrierDayOtherProducts() {
        invoice.setIssueDate(2021, 4, 26);
        Product bagietka = new OtherProduct("Bagietka", new BigDecimal("6"));
        invoice.addProduct(bagietka);
        BigDecimal reducedPrice = invoice.carrierDayPriceChange(bagietka);
        Assert.assertEquals(bagietka.getPriceWithTax(), reducedPrice);
    }
}
