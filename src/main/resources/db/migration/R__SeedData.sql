INSERT INTO accounts (account_number) VALUES
('ACC123'),
('ACC124');

INSERT INTO meter_reads (account_id, meter_id, reading, read_date, type) VALUES
(1, 101, 150.0, '2023-04-10', 'GAS'),
(1, 102, 250.0, '2023-04-10', 'ELEC'),
(2, 103, 175.0, '2023-04-10', 'GAS'),
(1, 101, 250.0, '2023-05-10', 'GAS'),
(1, 102, 360.0, '2023-05-10', 'ELEC');
