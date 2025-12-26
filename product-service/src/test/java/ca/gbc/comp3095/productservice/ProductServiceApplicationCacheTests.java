package ca.gbc.comp3095.productservice;

import ca.gbc.comp3095.productservice.dto.ProductRequest;
import ca.gbc.comp3095.productservice.dto.ProductResponse;
import ca.gbc.comp3095.productservice.model.Product;
import ca.gbc.comp3095.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductServiceApplicationCacheTests {

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redisContainer = new GenericContainer(DockerImageName.parse("redis:7.4.3"))
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(120));

    @Container
    @ServiceConnection(name = "mongodb")
    static MongoDBContainer mongodbContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0"))
            .withStartupTimeout(Duration.ofSeconds(120));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    ProductRepository productRepository;

    @MockitoSpyBean
    ProductRepository productRepositorySpy;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(){
        productRepository.deleteAll();
        Cache cache = cacheManager.getCache("PRODUCT_CACHE");
        if (cache != null) {
            cache.clear();
        }
        System.out.println("Redis container port: " + redisContainer.getMappedPort(6379));
        System.out.println("MongoDB container URI: " + mongodbContainer.getReplicaSetUrl());
    }

    @Test
    void testCreateProductAndCacheIt() throws Exception {

        ProductRequest productRequest =
                new ProductRequest("Samsung TV", "Samsung TV - 2025", BigDecimal.valueOf(2000));

        MvcResult result = mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(productRequest.name()))
                .andReturn();

        ProductResponse createdProduct =
                objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);
        String productId = createdProduct.id();

        assertTrue(productRepository.findById(productId).isPresent(), "Product should exist in database");

        Cache cache = cacheManager.getCache("PRODUCT_CACHE");
        assertNotNull(cache, "Cache 'PRODUCT_CACHE' should exist");
        ProductResponse cachedProduct = cache.get(productId, ProductResponse.class);
        assertNotNull(cachedProduct, "Product should be cached");
        assertEquals(productId, cachedProduct.id(), "Cached product ID should match");

    }

    @Test
    void testGetProductsAndVerifyCache() throws Exception {

        Product product = Product.builder()
                .name("Text Book")
                .description("COMP3095 Text Book")
                .price(BigDecimal.valueOf(100))
                .build();

        productRepository.save(product);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product.getName()));

        Mockito.verify(productRepositorySpy, Mockito.times(1)).findAll();

        Mockito.clearInvocations(productRepositorySpy);

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product.getName()));

        Mockito.verify(productRepositorySpy, Mockito.times(0)).findAll();
    }

    @Test
    void testUpdateProductAndVerifyCache() throws Exception {

        Product product = Product.builder()
                .name("Laptop")
                .description("Basic Laptop")
                .price(BigDecimal.valueOf(2000L))
                .build();

        productRepository.save(product);

        ProductRequest updatedProductRequest = new ProductRequest(
                "Gaming Laptop", "Gaming Laptop Pro", BigDecimal.valueOf(3000L));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/product/{productId}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductRequest)))
                .andExpect(status().isNoContent());

        Cache cache = cacheManager.getCache("PRODUCT_CACHE");
        assertNotNull(cache);

        String cachedProductId = cache.get(product.getId(), String.class);

        assertNotNull(cachedProductId);
        Assertions.assertEquals(product.getId(), cachedProductId);

    }

    @Test
    void testDeleteProductAndVerifyCacheEviction() throws Exception {

        Product product = Product.builder()
                .name("Laptop")
                .description("Basic Laptop")
                .price(BigDecimal.valueOf(2000L))
                .build();
        productRepository.save(product);

        Cache cache = cacheManager.getCache("PRODUCT_CACHE");
        assertNotNull(cache);
        cache.put(product.getId(), product.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/{productId}", product.getId()))
                .andExpect(status().isNoContent());

        Cache.ValueWrapper cachedProduct = cache.get(product.getId());
        assertNull(cachedProduct, "Product should have been evicted from cache after delete.");

    }

}