insert into roles (name) values ('ROLE_ADMIN'), ('ROLE_PASSENGER'), ('ROLE_DRIVER');

insert into vehicle_types (name) values ('SUV'), ('Crossover'), ('Sedan'), ('Truck'), ('Hatchback'), ('Minivan'),
                                        ('Hybrid');

insert into points (latitude, longitude) values (45.238548, 19.848225), (45.243097, 19.836284), (45.256863, 19.844129),
                                                (45.255055, 19.810161), (45.246540, 19.849282);

insert into vehicles (name, registration_number, type_id)
values ('Audi SQ7', 'NS482AL', 1);

insert into users (city, email, name, password, phone_number, surname, role_id, provider, picture)
values ('Novi Sad', 'darko.darkovic@gmail.com', 'Darko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8475222', 'Darkovic', 2, 0, 'default.jpg'),
       ('Novi Sad', 'mirko.ivanic@gmail.com', 'Mirko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-5679210', 'Ivanic', 3, 0, 'default.jpg');

insert into drivers (is_available, is_blocked, id, vehicle_id, location_id)
values (false, false, 2, 1, 1);

insert into passengers (activated, is_blocked, id, tokens)
values (true, false, 1, 100);