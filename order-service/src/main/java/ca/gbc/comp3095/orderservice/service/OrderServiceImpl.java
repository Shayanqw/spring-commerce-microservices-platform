package ca.gbc.comp3095.orderservice.service;

import ca.gbc.comp3095.orderservice.client.InventoryClient;
import ca.gbc.comp3095.orderservice.dto.OrderLineItemDto;
import ca.gbc.comp3095.orderservice.dto.OrderRequest;
import ca.gbc.comp3095.orderservice.model.Order;
import ca.gbc.comp3095.orderservice.model.OrderLineItem;
import ca.gbc.comp3095.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Override
    public void placeOrder(OrderRequest orderRequest) {

        // Check if all products are in stock
        boolean allInStock = orderRequest.orderLineItemDtoList()
                .stream()
                .allMatch(orderLineItem ->
                        inventoryClient.isInStock(orderLineItem.skuCode(), orderLineItem.quantity()));

        if (!allInStock) {
            throw new RuntimeException("One or more products are not in stock");
        }

        // Map OrderLineItemDto to OrderLineItem entity
        List<OrderLineItem> orderLineItems = orderRequest.orderLineItemDtoList()
                .stream()
                .map(this::mapOrderLineItemDtoToOrderLineItem)
                .toList();

        // Create order
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        // Save order
        orderRepository.save(order);
        log.info("Order {} placed successfully", order.getOrderNumber());
    }

    private OrderLineItem mapOrderLineItemDtoToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .skuCode(orderLineItemDto.skuCode())
                .price(orderLineItemDto.price())
                .quantity(orderLineItemDto.quantity())
                .build();
    }
}