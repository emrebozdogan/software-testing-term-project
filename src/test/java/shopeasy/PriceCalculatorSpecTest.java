package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 1 – Specification-Based Testing (Chapter 2)
 *
 * <p>Target class: {@link PriceCalculator}
 *
 * <p>Your goal is to test {@code PriceCalculator.calculate(basePrice, discountRate, taxRate)}
 * using the domain testing technique from Chapter 2:
 * <ol>
 *   <li>Identify equivalence partitions for each input dimension.</li>
 *   <li>Identify boundary values between partitions (on-point / off-point).</li>
 *   <li>Write at least 10 meaningful test cases that cover both partitions and boundaries.</li>
 *   <li>Use {@code @ParameterizedTest} with {@code @CsvSource} for tests that share structure.</li>
 *   <li>Add a comment above each test method explaining which partition or boundary it covers.</li>
 * </ol>
 *
 * <h3>Input dimensions to consider</h3>
 * <ul>
 *   <li><b>basePrice</b>  – zero, positive, very large</li>
 *   <li><b>discountRate</b> – 0 (no discount), (0,100) typical, 100 (full discount)</li>
 *   <li><b>taxRate</b>    – 0 (no tax), (0,100) typical, 100 (100% tax)</li>
 * </ul>
 */
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

     // EXAMPLE STRUCTURE (replace with real cases):

     /** Boundary: zero base price - the result must always be 0.0 regardless of discount and tax rates */
     @Test
     void zeroPriceAlwaysReturnsZero() {
         double basePrice = 0.0;
         // discount rate can be anything
         double discountRate = 10;
         // tax rate can be anything
         double taxRate = 5;

         assertThat(calculator.calculate(basePrice, discountRate, taxRate)).isEqualTo(0.0);
     }

     /** Boundary: discountRate is 100% - full discount wipes price to 0.0
      * tax should not increase the price if the item is free */
     @Test
     void discountRateHundredMeansFullDiscount() {
         // base price can be anything
         double basePrice = 50.0;
         double discountRate = 100;
         // tax rate can be anything
         double taxRate = 5;

         double result = calculator.calculate(basePrice, discountRate, taxRate);

         assertThat(result).isEqualTo(0.0);
     }

     /** Boundary: discountRate is 0% - no discount is applied, only tax should be added to the base price */
     @Test
     void discountRateZeroMeansNoDiscount() {
         double basePrice = 150.0;
         double discountRate = 0;
         double taxRate = 10;

         double result = calculator.calculate(basePrice, discountRate, taxRate);

         assertThat(result).isEqualTo(165.0);
     }

    /** Boundary: taxRate is 0% - no tax rate is applied, only discount should be effect the base price */
    @Test
    void taxRateZeroMeansNoTax() {
        double basePrice = 100.0;
        double discountRate = 50;
        double taxRate = 0;

        double result = calculator.calculate(basePrice, discountRate, taxRate);

        assertThat(result).isEqualTo(50.0);
    }

    /** Boundary: base price exceeds max double value, so it should be infinite */
    @Test
    void exceedingMaxDoubleReturnsInfinity() {
        double basePrice = Double.MAX_VALUE;
        double discountRate = 0;
        double taxRate = 100;

        double result = calculator.calculate(basePrice, discountRate, taxRate);
        assertThat(result).isInfinite();
    }


     /** Partitions & Boundaries covered for nominal values
      *  Checks regular calculation formulas with various rates */
     @ParameterizedTest(name = "base={0}, disc={1}%, tax={2}% => expected={3}")
     @CsvSource({
         "100.0, 10.0, 20.0, 108.0",
         "200.0,  0.0, 10.0, 220.0",
         "9999999.9, 10.0, 100.0, 17999999.82",
         "0.0, 0.0, 100.0, 0.0",
         "0.0, 0.0, 0.0, 0.0",
         "520.0, 0.0, 0.0, 520.0"

     })
     void typicalValues(double base, double disc, double tax, double expected) {
         assertThat(calculator.calculate(base, disc, tax)).isCloseTo(expected, within(0.001));
     }


}
