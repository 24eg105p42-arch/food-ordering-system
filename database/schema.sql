-- Online Food Ordering System - MySQL 8 schema
-- Reference DDL. With spring.jpa.hibernate.ddl-auto=update, Hibernate will create
-- these tables from your @Entity classes; use this file to design and cross-check.

CREATE DATABASE IF NOT EXISTS food_ordering_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_ordering_db;

-- ---------------------------------------------------------------
-- users: one table for all three roles
-- ---------------------------------------------------------------
CREATE TABLE users (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(150) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,                     -- BCrypt hash
  phone       VARCHAR(20),
  role        ENUM('CUSTOMER','RESTAURANT_ADMIN','SUPER_ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE addresses (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  label       VARCHAR(30),                               -- Home / Work / Other
  line1       VARCHAR(200) NOT NULL,
  line2       VARCHAR(200),
  city        VARCHAR(80) NOT NULL,
  state       VARCHAR(80) NOT NULL,
  pincode     VARCHAR(10) NOT NULL,
  is_default  BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- catalogue
-- ---------------------------------------------------------------
CREATE TABLE categories (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(80) NOT NULL UNIQUE,               -- Pizza, Biryani, Desserts...
  image_url   VARCHAR(500)
) ENGINE=InnoDB;

CREATE TABLE restaurants (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_id      BIGINT NOT NULL,                         -- a RESTAURANT_ADMIN user
  name          VARCHAR(150) NOT NULL,
  description   TEXT,
  address       VARCHAR(300) NOT NULL,
  city          VARCHAR(80) NOT NULL,
  phone         VARCHAR(20),
  image_url     VARCHAR(500),
  open_now      BOOLEAN NOT NULL DEFAULT TRUE,
  approved      BOOLEAN NOT NULL DEFAULT FALSE,          -- Super Admin approval
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id),
  INDEX idx_restaurants_city (city)
) ENGINE=InnoDB;

CREATE TABLE food_items (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  restaurant_id  BIGINT NOT NULL,
  category_id    BIGINT NOT NULL,
  name           VARCHAR(150) NOT NULL,
  description    TEXT,
  price          DECIMAL(10,2) NOT NULL,
  image_url      VARCHAR(500),
  is_veg         BOOLEAN NOT NULL DEFAULT TRUE,
  available      BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_food_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
  CONSTRAINT fk_food_category   FOREIGN KEY (category_id)   REFERENCES categories(id),
  CONSTRAINT chk_food_price CHECK (price >= 0),
  INDEX idx_food_name (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- cart: one active cart per customer, tied to a single restaurant
-- ---------------------------------------------------------------
CREATE TABLE cart (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT NOT NULL UNIQUE,
  restaurant_id  BIGINT,                                 -- set when first item is added
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_cart_user       FOREIGN KEY (user_id)       REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_cart_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE cart_items (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  cart_id      BIGINT NOT NULL,
  food_item_id BIGINT NOT NULL,
  quantity     INT NOT NULL DEFAULT 1,
  CONSTRAINT fk_cartitems_cart FOREIGN KEY (cart_id)      REFERENCES cart(id) ON DELETE CASCADE,
  CONSTRAINT fk_cartitems_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE,
  CONSTRAINT chk_cartitems_qty CHECK (quantity > 0),
  UNIQUE KEY uq_cart_food (cart_id, food_item_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- orders
-- ---------------------------------------------------------------
CREATE TABLE orders (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  restaurant_id    BIGINT NOT NULL,
  address_id       BIGINT,                               -- link for convenience
  delivery_address VARCHAR(500) NOT NULL,                -- snapshot: survives address edits/deletes
  total_amount     DECIMAL(10,2) NOT NULL,
  status           ENUM('PLACED','ACCEPTED','REJECTED','PREPARING',
                        'OUT_FOR_DELIVERY','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PLACED',
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user       FOREIGN KEY (user_id)       REFERENCES users(id),
  CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
  CONSTRAINT fk_orders_address    FOREIGN KEY (address_id)    REFERENCES addresses(id) ON DELETE SET NULL,
  INDEX idx_orders_user (user_id),
  INDEX idx_orders_restaurant_status (restaurant_id, status)
) ENGINE=InnoDB;

-- name and price are copied at order time so later menu edits never change past orders
CREATE TABLE order_items (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id     BIGINT NOT NULL,
  food_item_id BIGINT,
  item_name    VARCHAR(150) NOT NULL,
  unit_price   DECIMAL(10,2) NOT NULL,
  quantity     INT NOT NULL,
  CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id)     REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_orderitems_food  FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL,
  CONSTRAINT chk_orderitems_qty CHECK (quantity > 0)
) ENGINE=InnoDB;

CREATE TABLE payments (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id        BIGINT NOT NULL UNIQUE,
  method          ENUM('COD','UPI','CARD') NOT NULL,
  status          ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
  amount          DECIMAL(10,2) NOT NULL,
  transaction_id  VARCHAR(100),
  paid_at         TIMESTAMP NULL,
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- reviews: one review per customer per food item per order
-- ---------------------------------------------------------------
CREATE TABLE reviews (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id       BIGINT NOT NULL,
  food_item_id  BIGINT NOT NULL,
  order_id      BIGINT NOT NULL,                         -- proves the customer actually ordered it
  rating        TINYINT NOT NULL,
  comment       VARCHAR(1000),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_user  FOREIGN KEY (user_id)      REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_reviews_food  FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE,
  CONSTRAINT fk_reviews_order FOREIGN KEY (order_id)     REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
  UNIQUE KEY uq_review (user_id, food_item_id, order_id)
) ENGINE=InnoDB;
