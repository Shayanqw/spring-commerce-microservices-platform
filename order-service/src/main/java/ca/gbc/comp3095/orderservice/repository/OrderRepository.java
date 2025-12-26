package ca.gbc.comp3095.orderservice.repository;

import ca.gbc.comp3095.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // No methods needed - JpaRepository provides all CRUD operations
}