insert into vehicle_types (name, multiplier) values ('SUV', 1.5), ('Crossover', 1.4), ('Sedan', 1.15), ('Truck', 3), ('Hatchback', 1), ('Minivan', 2.3),
                                        ('Hybrid', 1.7);

insert into roles (name) values ('ROLE_ADMIN'), ('ROLE_PASSENGER'), ('ROLE_DRIVER');

-- stajalista
insert into points (latitude, longitude) values (45.238548, 19.848225), (45.243097, 19.836284), (45.256863, 19.844129),
                                                (45.255055, 19.810161), (45.246540, 19.849282);

insert into vehicles (name, registration_number, type_id)
values ('Audi SQ7', 'NS482AL', 1), ('Citroen C4 Picasso', 'NS205MM', 6), ('BMW i3', 'NS943HG', 7),
       ('Peugeot 208', 'NS122MK', 5), ('Volkswagen Jetta', 'NS003ZX', 3);

-- lozinka je svima: sifra123
insert into users (city, email, name, password, phone_number, surname, role_id, provider, picture)
values ('Novi Sad', 'mirko.ivanic@gmail.com', 'Mirko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-5679210', 'Ivanic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'vujadin.savic@gmail.com', 'Vujadin', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-3918507', 'Savic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'branko.lazic@gmail.com', 'Branko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8301397', 'Lazic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'marko.simonovic@gmail.com', 'Marko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-0048629', 'Simonovic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'marko.gobeljic@gmail.com', 'Marko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8475291', 'Gobeljic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'darko.darkovic@gmail.com', 'Darko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8475222', 'Darkovic', 2, 0, 'default.jpg'),
       ('Novi Sad', 'admin.admin@gmail.com', 'Zoran', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-62-3321222', 'Bukorac', 1, 0, 'default.jpg'),
       ('Novi Sad', 'nikola.nikolic@gmail.com', 'Nikola', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-62-3321333', 'Nikolic', 2, 0, 'default.jpg'),
       ('Novi Sad', 'voja.admin@gmail.com', 'Voja', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-62-3321444', 'Vojanovic', 2, 0, 'default.jpg');

insert into drivers (is_available, is_blocked, id, vehicle_id, location_id)
values (false, false, 1, 1, 1), (false, false, 2, 3, 2), (false, false, 3, 2, 3),
       (false, false, 4, 4, 4), (false, false, 5, 5, 5);

insert into passengers (activated, is_blocked, id, tokens)
values (true, false, 6, 600), (true, false, 8, 600), (true, false, 9, 600);

insert into tags (id, name)
values (1, 'Pet friendly'),
       (2, 'Baby friendly');

insert into routes (route_name, route_idx, length, expected_time)
values ('PUSKINOVA 27 - KISACKA 15', 0, 6.00, 12.00);

insert into route_waypoints (route_id, point_id)
values (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

insert into drives (start_date, end_date, price, length, status, driver, route_id)
values ('2023-01-23 14:00:00', '2023-01-23 14:30:00', 4, 25, 3, 1, 1),
       ('2023-02-03 15:00:00', '2023-02-3 15:12:00', 3, 20, 3, 1, 1);

insert into drive_passengers (drive_id, passenger_id)
values (1, 6), (1, 8), (1, 9), (2, 6), (2, 8);

insert into drive_tags (drive_id, tag_id)
values (1, 1),
       (1, 2);

insert into ratings (driver_rating, vehicle_rating, comment, drive_id, passenger_id)
values (5, 3, 'All good', 1, 6), (4, 4, 'Nice driver', 1, 8);