CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS meter_reads (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   account_id BIGINT,
   meter_id BIGINT,
   reading DOUBLE NOT NULL,
   read_date DATE NOT NULL,
   type VARCHAR(10) NOT NULL,
FOREIGN KEY (account_id) REFERENCES accounts(id)
);
