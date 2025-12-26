package ca.gbc.comp3095.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.*;

@Configuration
@Slf4j
public class Routes {

    @Value("${services.product-url}")
    private String productServiceUrl;

    @Value("${services.order-url}")
    private String orderServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Initializing product service route with URL: {}", productServiceUrl);

        return GatewayRouterFunctions.route("product_service")
                .route(
                        // Match both the base path and any subpaths (e.g., /api/product/123)
                        RequestPredicates.path("/api/product/**"),
                        request -> {
                            log.info("Received request for product service: {}", request.uri());
                            try {
                                ServerResponse response = HandlerFunctions.http(productServiceUrl).handle(request);
                                log.info("Response status: {}", response.statusCode());
                                return response;
                            } catch (Exception e) {
                                log.error("Error occurred while routing request: {}", e.getMessage(), e);
                                return ServerResponse.status(500).body("An error occurred");
                            }
                        })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Initializing order service route with URL: {}", orderServiceUrl);

        return GatewayRouterFunctions.route("order_service")
                .route(
                        // Match both the base path and any subpaths (e.g., /api/order/123)
                        RequestPredicates.path("/api/order/**"),
                        request -> {
                            log.info("Received request for order service: {}", request.uri());
                            try {
                                ServerResponse response = HandlerFunctions.http(orderServiceUrl).handle(request);
                                log.info("Response status: {}", response.statusCode());
                                return response;
                            } catch (Exception e) {
                                log.error("Error occurred while routing request: {}", e.getMessage(), e);
                                return ServerResponse.status(500).body("An error occurred");
                            }
                        })
                .build();
    }
}