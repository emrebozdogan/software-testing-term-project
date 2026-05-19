package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 2 – Structural Testing &amp; Code Coverage (Chapter 3)
 *
 * <p>Target class: {@link ShoppingCart}
 *
 * <h3>Workflow</h3>
 * <ol>
 *   <li>Write an initial test suite based on the specification (Javadoc of ShoppingCart).</li>
 *   <li>Run {@code mvn test} to generate the JaCoCo report:
 *       <pre>  target/site/jacoco/index.html</pre></li>
 *   <li>Open the report, navigate to {@code ShoppingCart}, and identify uncovered branches.</li>
 *   <li>Add tests specifically to cover those branches until branch coverage &gt;= 80%.</li>
 *   <li>Take a screenshot of the final JaCoCo summary and put it in {@code report/jacoco-screenshot.png}.</li>
 * </ol>
 *
 * <h3>Branches to think about</h3>
 * <ul>
 *   <li>{@code addItem}: product already in cart vs. new product</li>
 *   <li>{@code removeItem}: product found vs. not found in cart</li>
 *   <li>{@code updateQuantity}: product found vs. not found, quantity valid vs. invalid</li>
 *   <li>{@code applyDiscount}: zero discount, positive discount</li>
 *   <li>{@code total}: empty cart vs. non-empty cart</li>
 * </ul>
 *
 * <h3>Bonus (PIT Mutation Testing)</h3>
 * Run: {@code mvn org.pitest:pitest-maven:mutationCoverage}
 * <br>Examine the HTML report in {@code target/pit-reports/}. Find two surviving mutants,
 * explain why each survived, and describe a test that would kill it. Add this analysis
 * to your reflection report.
 */
class ShoppingCartStructuralTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        apple  = new Product("P001", "Apple",  1.50, 100);
        banana = new Product("P002", "Banana", 0.80, 50);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    //
    // Start with happy-path tests, then add tests that target specific branches.
    //
    // HINT: Run `mvn test` after every few tests to see coverage progress.
    // -----------------------------------------------------------------------

    @Test
    void addItem_NewProduct_AddsToCart() {
        cart.addItem(apple, 2);

        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isEqualTo(3);
    }

    @Test
    void addItem_ExistingProduct_UpdatesQuantity() {
        cart.addItem(apple, 2);
        // same product diff quantity hits if condition
        cart.addItem(apple, 3);

        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isEqualTo(7.50);
    }

    @Test
    void total_EmptyCart_ReturnsZero() {
        double total = cart.total();

        assertThat(total).isEqualTo(0.0);
    }

    @Test
    void removeItem_ProductFound_RemovesFromCart() {
        cart.addItem(apple, 4);
        cart.addItem(banana, 3);
        // remove apple using its id
        cart.removeItem(apple.getId());

        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isCloseTo(2.4, within(0.001));
    }

    @Test
    void removeItem_ProductNotFound_DoesNothing() {
        cart.addItem(apple, 2);
        // remove banana (not exists) using its id
        cart.removeItem(banana.getId());
        // get the apple item after removing banana (not exists)
        String item = cart.getItems().get(0).getProduct().getName();

        assertThat(item).isEqualTo("Apple");
        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isEqualTo(3);
    }

    @Test
    void updateQuantity_ProductFound_UpdatesSuccessfully() {
        cart.addItem(apple, 2);
        // update apple's quantity using getId()
        cart.updateQuantity(apple.getId(), 5);

        assertThat(cart.total()).isEqualTo(7.50);
    }

    @Test
    void updateQuantity_ProductNotFound_ThrowsException() {
        cart.addItem(apple, 1);
        // update quantity of a product doesn't in the cart
        assertThatThrownBy(() -> cart.updateQuantity(banana.getId(), 5)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantity_InvalidQuantity_ThrowsException() {
        cart.addItem(apple, 4);
        // update apple's quantity to 0
        assertThatThrownBy(() -> cart.updateQuantity(apple.getId(), 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void applyDiscount_PositiveDiscount_ReducesTotal() {
        cart.addItem(apple, 4);
        // apply 10% discount
        double total = cart.applyDiscount(10.0);

        assertThat(total).isEqualTo(5.40);
    }

    @Test
    void applyDiscount_ZeroDiscount_ReturnsSameTotal() {
        cart.addItem(apple, 4);
        // apply 0% discount
        double total = cart.applyDiscount(0.0);

        assertThat(total).isEqualTo(6.00);
    }

    @Test
    void clear_WithItemsInCart_EmptiesTheCart() {
        cart.addItem(apple, 4);
        cart.addItem(banana, 2);
        // delete all items in cart
        cart.clear();

        assertThat(cart.itemCount()).isEqualTo(0);
        assertThat(cart.total()).isEqualTo(0.0);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void clear_EmptyCart_RemainsEmpty() {
        // delete all items in empty cart
        cart.clear();

        assertThat(cart.itemCount()).isEqualTo(0);
        assertThat(cart.total()).isEqualTo(0.0);
        assertThat(cart.getItems()).isEmpty();
    }

}
