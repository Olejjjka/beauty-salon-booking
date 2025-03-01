INSERT INTO clients (name, phone, login, password) VALUES
                                                       ('Иван', '+79001112233', 'ivan', 'password1'),
                                                       ('Мария', '+79004445566', 'maria', 'password2');

INSERT INTO masters (name, phone, login, password) VALUES
                                                       ('Алексей', '+79007778899', 'alexey', 'password3'),
                                                       ('Анна', '+79006665544', 'anna', 'password4');

INSERT INTO beauty_services (beauty_service_name, price) VALUES
                                                             ('Стрижка', 1500.00),
                                                             ('Маникюр', 1200.00);

INSERT INTO master_beauty_services (master_id, beauty_service_id) VALUES
                                                                      (1, 1),
                                                                      (1, 2),
                                                                      (2, 2);

INSERT INTO appointments (client_id, master_id, beauty_service_id, date, time, status) VALUES
                                                                                           (1, 1, 1, '2025-03-05', '10:00', 'CONFIRMED'),
                                                                                           (2, 2, 2, '2025-03-06', '14:30', 'COMPLETED');