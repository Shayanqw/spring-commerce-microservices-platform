// ============================================
// MongoDB Initialization Script
// Purpose: Create application user and database
// Runs automatically on first container startup
// ============================================

// Switch to the product-service database
// This is created by MONGO_INITDB_DATABASE env variable
db = db.getSiblingDB('product-service');

// Create application user with read/write permissions
db.createUser({
    user: 'admin',
    pwd: 'password',
    roles: [
        {
            // Grant read and write permissions
            role: 'readWrite',
            // On the product-service database
            db: 'product-service'
        }
    ]
});

// Create product collection (table in SQL terms)
// MongoDB creates collections automatically, but explicit creation
// allows us to set options and validate it exists
db.createCollection('product');

// Optional: Create indexes for better query performance
// Index on product name for faster searches
db.product.createIndex({ name: 1 });

// Log success message (visible in docker logs)
print('MongoDB initialization completed successfully!');
print('Database: product-service');
print('User: admin');
print('Collection: product');