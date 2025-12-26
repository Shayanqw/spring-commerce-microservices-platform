print('Mongo Init Script - START');

db = db.getSiblingDB('admin');

if (db.system.users.find({ user: 'admin' }).count() === 0) {
    db.createUser({
        user: 'admin',
        pwd: 'password',
        roles: [{ role: 'root', db: 'admin' }]
    });
    print('Admin user created');
} else {
    print('Admin user already exists');
}

db = db.getSiblingDB('product-service');

if (db.system.users.find({ user: 'productAdmin' }).count() === 0) {
    db.createUser({
        user: 'productAdmin',
        pwd: 'password',
        roles: [{ role: 'readWrite', db: 'product-service' }]
    });
    print('ProductAdmin user created');
} else {
    print('ProductAdmin user already exists');
}

if (!db.getCollectionNames().includes('user')) {
    db.createCollection('user');
    print('User collection created');
} else {
    print('User collection already exists');
}

print('Mongo Init Script - END');