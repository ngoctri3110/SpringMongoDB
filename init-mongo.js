// Create collections and initial data
db = db.getSiblingDB('ecommerce');

// Create collections
db.createCollection('users');
db.createCollection('products');
db.createCollection('categories');
db.createCollection('orders');
db.createCollection('reviews');
db.createCollection('carts');
db.createCollection('inventory');
db.createCollection('paymentTransactions');

print('MongoDB collections created successfully');
