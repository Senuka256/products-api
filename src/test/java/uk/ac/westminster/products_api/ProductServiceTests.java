package uk.ac.westminster.products_api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTests {

    @Test
    void assignsIdsAndDoesNotReuseDeletedIds() {
        ProductService service = new ProductService();
        assertTrue(service.getAllProducts().isEmpty());

        Product laptop = service.addProduct(new Product(99L, "Laptop", 899.99));
        Product mouse = service.addProduct(new Product(null, "Mouse", 24.99));
        assertEquals(1L, laptop.getId());
        assertEquals(2L, mouse.getId());
        assertEquals(2, service.getAllProducts().size());

        service.deleteProduct(1L);
        assertTrue(service.getProductById(1L).isEmpty());
        assertSame(mouse, service.getProductById(2L).orElseThrow());

        Product mug = service.addProduct(new Product(null, "Mug", 8.50));
        assertEquals(3L, mug.getId());
        service.deleteProduct(99L);
        assertEquals(2, service.getAllProducts().size());
    }

    @Test
    void findsAndDeletesIdsAboveTheLongCacheRange() {
        ProductService service = new ProductService();
        for (int i = 1; i <= 130; i++) {
            service.addProduct(new Product(null, "Product " + i, i));
        }

        assertEquals("Product 130", service.getProductById(Long.valueOf("130"))
                .orElseThrow().getName());
        service.deleteProduct(Long.valueOf("130"));
        assertTrue(service.getProductById(130L).isEmpty());
        assertEquals(129, service.getAllProducts().size());
    }

    @Test
    void searchesByPartialNameWithCaseSensitiveMatches() {
        ProductService service = new ProductService();
        service.addProduct(new Product(null, "Laptop", 899.99));
        service.addProduct(new Product(null, "Mouse", 24.99));
        service.addProduct(new Product(null, "Laptop stand", 49.99));

        assertEquals(2, service.findByName("Lap").size());
        assertEquals("Mouse", service.findByName("ous").getFirst().getName());
        assertTrue(service.findByName("lap").isEmpty());
        assertTrue(service.findByName("Keyboard").isEmpty());
    }
}
