package uk.ac.westminster.products_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class Tutorial3ApiTests {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void catalogueSupportsTheFullLabSequence() throws Exception {
        assertEquals("[]", request("GET", "/products", null).body());

        JsonNode laptop = mapper.readTree(request("POST", "/products",
                "{\"name\":\"Laptop\",\"price\":899.99}").body());
        assertEquals(1, laptop.get("id").asLong());
        assertEquals("Laptop", laptop.get("name").asString());
        assertEquals(899.99, laptop.get("price").asDouble());

        JsonNode mouse = mapper.readTree(request("POST", "/products",
                "{\"name\":\"Mouse\",\"price\":24.99}").body());
        assertEquals(2, mouse.get("id").asLong());

        JsonNode all = mapper.readTree(request("GET", "/products", null).body());
        assertEquals(2, all.size());
        assertEquals(laptop, all.get(0));
        assertEquals(mouse, all.get(1));
        assertEquals(laptop, mapper.readTree(request("GET", "/products/1", null).body()));

        assertEquals("", request("DELETE", "/products/1", null).body());
        JsonNode remaining = mapper.readTree(request("GET", "/products", null).body());
        assertEquals(1, remaining.size());
        assertEquals(mouse, remaining.get(0));
        assertEquals("", request("GET", "/products/99", null).body());

        JsonNode stand = mapper.readTree(request("POST", "/products",
                "{\"id\":999,\"name\":\"Laptop stand\",\"price\":49.99}").body());
        assertEquals(3, stand.get("id").asLong());
        request("POST", "/products", "{\"name\":\"Laptop case\",\"price\":19.99}");

        JsonNode matches = mapper.readTree(request("GET", "/products/search?name=Lap", null).body());
        assertEquals(2, matches.size());
        assertEquals("Laptop stand", matches.get(0).get("name").asString());
        assertEquals("Laptop case", matches.get(1).get("name").asString());
        assertEquals("[]", request("GET", "/products/search?name=Keyboard", null).body());
        assertEquals("[]", request("GET", "/products/search?name=lap", null).body());

        JsonNode paths = mapper.readTree(request("GET", "/v3/api-docs", null).body()).get("paths");
        assertTrue(paths.get("/products").has("get"));
        assertTrue(paths.get("/products").has("post"));
        assertTrue(paths.get("/products/{id}").has("get"));
        assertTrue(paths.get("/products/{id}").has("delete"));
        assertTrue(paths.get("/products/search").has("get"));
    }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(10));
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), method + " " + path + ": " + response.body());
        return response;
    }
}
