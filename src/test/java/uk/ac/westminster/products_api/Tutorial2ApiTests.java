package uk.ac.westminster.products_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Tutorial2ApiTests {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void productReturnsAllFields() throws Exception {
        JsonNode created = createProduct();
        JsonNode product = getJson("/products/" + created.get("id").asLong());
        assertEquals(3, product.size());
        assertEquals(created.get("id").asLong(), product.get("id").asLong());
        assertEquals("Laptop", product.get("name").asString());
        assertEquals(999.99, product.get("price").asDouble());
    }

    @Test
    void productUsesTheIdFromThePath() throws Exception {
        JsonNode first = createProduct();
        JsonNode second = createProduct();
        long id = second.get("id").asLong();
        assertNotEquals(first.get("id").asLong(), id);
        assertEquals(id, getJson("/products/" + id).get("id").asLong());
    }

    @Test
    void customerReturnsANestedAddress() throws Exception {
        JsonNode customer = getJson("/customers/1");
        assertEquals(4, customer.size());
        assertEquals(1, customer.get("id").asLong());
        assertEquals("Ada Lovelace", customer.get("name").asString());
        assertEquals("ada@example.com", customer.get("email").asString());

        JsonNode address = customer.get("address");
        assertEquals(3, address.size());
        assertEquals("115 New Cavendish Street", address.get("street").asString());
        assertEquals("London", address.get("city").asString());
        assertEquals("W1W 6UW", address.get("postcode").asString());
    }

    @Test
    void customerUsesTheIdFromThePath() throws Exception {
        assertEquals(7, getJson("/customers/7").get("id").asLong());
    }

    @Test
    void swaggerListsBothEndpoints() throws Exception {
        JsonNode paths = getJson("/v3/api-docs").get("paths");
        assertTrue(paths.has("/products/{id}"));
        assertTrue(paths.has("/customers/{id}"));
        assertTrue(paths.has("/hello"));
        assertTrue(paths.has("/goodbye"));
    }

    private JsonNode createProduct() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/products"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Laptop\",\"price\":999.99}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        return mapper.readTree(response.body());
    }

    private JsonNode getJson(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        return mapper.readTree(response.body());
    }
}
