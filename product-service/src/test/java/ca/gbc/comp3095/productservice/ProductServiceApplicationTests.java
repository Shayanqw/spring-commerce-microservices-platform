package ca.gbc.comp3095.productservice;

import ca.gbc.comp3095.productservice.dto.ProductRequest;
import ca.gbc.comp3095.productservice.dto.ProductResponse;
import ca.gbc.comp3095.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ProductServiceApplicationTests {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(
            DockerImageName.parse("mongo:latest")
    );

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // Configure RestAssured for API testing
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Clear database before each test
        productRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify container is running
        assertTrue(mongoDBContainer.isRunning());
        assertNotNull(port);
    }

    private ProductRequest getProductRequest() {
        return new ProductRequest(
                "Test Product",
                "Test Product Description",
                BigDecimal.valueOf(199.99)
        );
    }

    @Test
    void createProduct() {
        ProductRequest productRequest = getProductRequest();

        RestAssured.given()
                .contentType("application/json")
                .body(productRequest)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201);

        // Verify product was saved to database
        assertEquals(1, productRepository.findAll().size());

        // Verify the saved product details
        var savedProduct = productRepository.findAll().get(0);
        assertEquals("Test Product", savedProduct.getName());
        assertEquals("Test Product Description", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(199.99), savedProduct.getPrice());
    }

    @Test
    void getAllProducts() {
        // First, create a product
        ProductRequest productRequest = getProductRequest();

        RestAssured.given()
                .contentType("application/json")
                .body(productRequest)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201);

        // Then, get all products
        List<ProductResponse> products = RestAssured.given()
                .when()
                .get("/api/product")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", ProductResponse.class);

        // Verify we have 1 product
        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).name());
        assertEquals("Test Product Description", products.get(0).description());
        assertEquals(BigDecimal.valueOf(199.99), products.get(0).price());
    }

    @Test
    void updateProduct() {
        // First, create a product
        ProductRequest productRequest = getProductRequest();

        RestAssured.given()
                .contentType("application/json")
                .body(productRequest)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201);

        // Get the created product ID
        String productId = productRepository.findAll().get(0).getId();

        // Update the product
        ProductRequest updateRequest = new ProductRequest(
                "Updated Product",
                "Updated Description",
                BigDecimal.valueOf(299.99)
        );

        RestAssured.given()
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .put("/api/product/" + productId)
                .then()
                .statusCode(204);

        // Verify the update
        var updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(299.99), updatedProduct.getPrice());
    }

    @Test
    void deleteProduct() {
        // First, create a product
        ProductRequest productRequest = getProductRequest();

        RestAssured.given()
                .contentType("application/json")
                .body(productRequest)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201);

        // Get the created product ID
        String productId = productRepository.findAll().get(0).getId();

        // Delete the product
        RestAssured.given()
                .when()
                .delete("/api/product/" + productId)
                .then()
                .statusCode(204);

        // Verify deletion
        assertEquals(0, productRepository.findAll().size());
        assertTrue(productRepository.findById(productId).isEmpty());
    }
}