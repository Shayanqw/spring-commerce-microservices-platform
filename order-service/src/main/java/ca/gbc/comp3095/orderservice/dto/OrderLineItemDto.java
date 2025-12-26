package ca.gbc.comp3095.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineItemDto(
        Long id,
        String skuCode,
        BigDecimal price,
        Integer quantity
) {}