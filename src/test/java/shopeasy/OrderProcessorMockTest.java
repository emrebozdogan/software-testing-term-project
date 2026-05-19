package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Task 5 – Mocks &amp; Stubs (Chapter 6)
 *
 * <p>Target class: {@link OrderProcessor}
 *
 * <p>Use Mockito to mock {@link InventoryService} and {@link PaymentGateway},
 * then test {@link OrderProcessor#process(String, ShoppingCart)} in isolation.
 *
 * <h3>Required scenarios (at least 4)</h3>
 * <ol>
 *   <li><b>Happy path</b> — inventory available, payment succeeds → non-null {@link Order} returned.</li>
 *   <li><b>Inventory failure</b> — {@code isAvailable()} returns {@code false} for at least one item
 *       → method returns {@code null} AND {@code charge()} is <em>never</em> called.</li>
 *   <li><b>Payment failure</b> — inventory OK, {@code charge()} returns {@code false}
 *       → method returns {@code null}.</li>
 *   <li><b>Partial quantity</b> — define the expected behaviour when only some items
 *       pass the inventory check, and write a test for it.</li>
 * </ol>
 *
 * <h3>Verification</h3>
 * Use {@code verify(paymentGateway, never()).charge(...)} to assert that
 * payment is never attempted when inventory is insufficient.
 *
 * <h3>Reflection (add to your report)</h3>
 * Answer: What does mocking allow you to test that you could not test otherwise?
 * What does it prevent you from testing? When is mocking a bad idea?
 */
@ExtendWith(MockitoExtension.class)
class OrderProcessorMockTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderProcessor orderProcessor;

    private ShoppingCart cart;
    private Product widget;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        widget = new Product("P001", "Widget", 25.0, 100);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your mock-based tests below.
    //
    // EXAMPLE STRUCTURE — happy path:
    //
    // @Test
    // void process_inventoryOkAndPaymentOk_returnsOrder() {
    //     cart.addItem(widget, 2);
    //
    //     when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
    //     when(paymentGateway.charge("customer-1", 50.0)).thenReturn(true);
    //
    //     Order order = orderProcessor.process("customer-1", cart);
    //
    //     assertThat(order).isNotNull();
    //     assertThat(order.getCustomerId()).isEqualTo("customer-1");
    //     assertThat(order.getTotal()).isEqualTo(50.0);
    //     verify(paymentGateway).charge("customer-1", 50.0);
    // }
    // -----------------------------------------------------------------------

    @Test
    void process_allConditionsMet_returnsOrderObject() {
        // inventory is available and payment is successful
        cart.addItem(widget, 3);

        when(inventoryService.isAvailable(widget, 3)).thenReturn(true);
        when(paymentGateway.charge("customer-789", 75.0)).thenReturn(true);

        Order result = orderProcessor.process("customer-789", cart);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo("customer-789");
        assertThat(result.getTotal()).isEqualTo(75.0);

        verify(paymentGateway).charge("customer-789", 75.0);
    }

    @Test
    void process_inventoryUnavailable_returnsNullAndAbortsPayment() {
        // item out of stock, payment should not be triggered
        cart.addItem(widget, 10);

        when(inventoryService.isAvailable(widget, 10)).thenReturn(false);
        Order result = orderProcessor.process("customer-789", cart);

        assertThat(result).isNull();

        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    @Test
    void process_paymentDeclined_returnsNull() {
        // inventory ok but payment fails
        cart.addItem(widget, 1);

        when(inventoryService.isAvailable(widget, 1)).thenReturn(true);
        when(paymentGateway.charge("customer-789", 25.0)).thenReturn(false);

        Order result = orderProcessor.process("customer-789", cart);

        assertThat(result).isNull();
    }

    @Test
    void process_partialQuantityFailure_returnsNull() {
        // when specific quantity check fails
        cart.addItem(widget, 50);

        when(inventoryService.isAvailable(widget, 50)).thenReturn(false);

        Order result = orderProcessor.process("customer-789", cart);

        assertThat(result).isNull();

        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

}
