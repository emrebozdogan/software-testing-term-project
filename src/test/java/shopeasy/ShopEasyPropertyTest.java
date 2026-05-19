package shopeasy;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 4 – Property-Based Testing (Chapter 5)
 *
 * <p>Target classes: {@link PriceCalculator}, {@link ShoppingCart}
 *
 * <p>Using jqwik, define and test at least <strong>3 distinct properties</strong>.
 * You must use at least one custom {@code @Provide} method.
 *
 * <h3>Suggested properties (you may use these or design your own)</h3>
 * <ul>
 *   <li><b>Monotonicity</b> – For any fixed base and tax, increasing the discount
 *       rate never increases the final price.</li>
 *   <li><b>Identity</b> – A 0% discount and 0% tax returns exactly the base price.</li>
 *   <li><b>Boundedness</b> – The result is always &gt;= 0.</li>
 *   <li><b>Cart commutativity</b> – Adding product A then B yields the same total
 *       as adding B then A.</li>
 *   <li><b>Discount transitivity</b> – Applying a 10% then another 10% discount via
 *       {@code applyDiscount} is equivalent to a single call with the compounded rate
 *       (think carefully: is this actually true for this implementation?).</li>
 * </ul>
 *
 * <h3>For each property, include a comment that answers:</h3>
 * <ol>
 *   <li>What does this property mean in plain English?</li>
 *   <li>What class of bugs would this property catch?</li>
 * </ol>
 *
 * <h3>If jqwik finds a failing case</h3>
 * Do not just fix the test. Investigate the root cause and explain it in your
 * reflection report (include the counterexample jqwik printed).
 */
class ShopEasyPropertyTest {

    // -----------------------------------------------------------------------
    // TODO: Write your properties below.
    //
    // EXAMPLE STRUCTURE:
    //
    // /**
    //  * Property: The final price is always non-negative.
    //  * Bug class caught: any implementation path that produces a negative result
    //  *                   (e.g., discount > 100 applied to negative base).
    //  */
    // @Property
    // void finalPriceIsNeverNegative(
    //         @ForAll @DoubleRange(min = 0, max = 10_000) double base,
    //         @ForAll @DoubleRange(min = 0, max = 100)   double discount,
    //         @ForAll @DoubleRange(min = 0, max = 100)   double tax) {
    //
    //     PriceCalculator calc = new PriceCalculator();
    //     double result = calc.calculate(base, discount, tax);
    //     assertThat(result).isGreaterThanOrEqualTo(0.0);
    // }
    //
    // // Custom provider example:
    // @Provide
    // Arbitrary<Product> validProducts() {
    //     return Combinators.combine(
    //             Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(5),
    //             Arbitraries.doubles().between(0.01, 500.0)
    //     ).as((name, price) -> new Product("P-" + name, name, price, 100));
    // }
    // -----------------------------------------------------------------------

    private final PriceCalculator calculator = new PriceCalculator();

    // property 1: identity
    // what it means: 0 discount and 0 tax returns the exact base price.
    // bugs caught: catches accidental math errors like hardcoded additions.
    @Property
    void identityProperty(@ForAll @DoubleRange(min = 0.0, max = 10000.0) double basePrice) {
        double result = calculator.calculate(basePrice, 0.0, 0.0);
        assertThat(result).isEqualTo(basePrice);
    }

    // property 2: boundedness
    // what it means: final price is always between 0 and max possible price with tax.
    // bugs caught: catches negative prices or applying tax/discount in the wrong direction.
    @Property
    void boundednessProperty(
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double base,
            @ForAll("validPercentages") double discount,
            @ForAll("validPercentages") double tax) {

        double result = calculator.calculate(base, discount, tax);

        assertThat(result).isGreaterThanOrEqualTo(0.0);
        double maxPossible = base * (1.0 + (tax / 100.0));
        // adding 0.000001 for floating-point precision
        assertThat(result).isLessThanOrEqualTo(maxPossible + 0.000001);
    }

    // custom provide method
    @Provide
    Arbitrary<Double> validPercentages() {
        return Arbitraries.doubles().between(0.0, 100.0);
    }

    // property 3: cart commutativity
    // what it means: adding item a then b gives the same total as adding b then a.
    // bugs caught: catches state-dependent bugs where the order of operations wrongly affects total.
    @Property
    void cartCommutativity(
            @ForAll @IntRange(min = 1, max = 50) int qty1,
            @ForAll @IntRange(min = 1, max = 50) int qty2) {

        Product p1 = new Product("P1", "Item A", 10.0, 100);
        Product p2 = new Product("P2", "Item B", 20.0, 100);

        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItem(p1, qty1);
        cart1.addItem(p2, qty2);

        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItem(p2, qty2);
        cart2.addItem(p1, qty1);

        assertThat(cart1.total()).isEqualTo(cart2.total());
        assertThat(cart1.itemCount()).isEqualTo(cart2.itemCount());
    }
}
