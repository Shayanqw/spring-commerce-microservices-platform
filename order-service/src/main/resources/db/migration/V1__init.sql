CREATE TABLE orders (
    id BIGSERIAL NOT NULL,
    order_number VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE order_line_items (
    id BIGSERIAL NOT NULL,
    sku_code VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    order_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_line_items_order_id ON order_line_items(order_id);