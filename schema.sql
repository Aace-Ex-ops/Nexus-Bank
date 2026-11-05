CREATE DATABASE IF NOT EXISTS bank_db;
USE bank_db;

-- Clear old versions to maintain data integrity with the new dual-balance system
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

-- 1. USERS TABLE
-- Removed single 'balance' column and added 'savings_balance' and 'current_balance'
CREATE TABLE users (
    account_no VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    pin VARCHAR(255) NOT NULL,          -- Stores the PIN
    savings_balance DOUBLE DEFAULT 0.0, -- Independent savings bucket
    current_balance DOUBLE DEFAULT 0.0, -- Independent current bucket
    email VARCHAR(100),
    mobile VARCHAR(15),                 -- Used for Mobile Pay lookups
    address TEXT,                       -- Editable in My Account
    profile_pic_path VARCHAR(255),      -- Path to custom profile image
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TRANSACTIONS TABLE
-- Links to users and survives the dual-balance logic
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE,  -- 'TXN-A1B2C3D4' logic
    account_no VARCHAR(20),             -- Owner of the record
    type VARCHAR(50),                   -- e.g., 'SAVINGS DEPOSIT', 'CURRENT WITHDRAW'
    amount DOUBLE,                      -- Value of transaction
    related_account VARCHAR(20),        -- Recipient or Sender account number
    trans_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Ensures history is deleted if the account is deleted via My Account page
    FOREIGN KEY (account_no) REFERENCES users(account_no) ON DELETE CASCADE
);