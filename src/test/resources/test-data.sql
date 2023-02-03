insert into roles (name) values ('ROLE_ADMIN'), ('ROLE_PASSENGER'), ('ROLE_DRIVER');

insert into vehicle_types (name, multiplier) values ('SUV', 1.5), ('Crossover', 1.4), ('Sedan', 1.35), ('Truck', 3), ('Hatchback', 1.25), ('Minivan', 2.3),
                                                    ('Hybrid', 1.7);

insert into points (latitude, longitude) values (45.238548, 19.848225), (45.243097, 19.836284), (45.256863, 19.844129),
                                                (45.255055, 19.810161), (45.246540, 19.849282);

insert into vehicles (name, registration_number, type_id)
values ('Audi SQ7', 'NS482AL', 1), ('Citroen C4 Picasso', 'NS205MM', 6), ('Volkswagen Jetta', 'NS003ZX', 3);

insert into users (city, email, name, password, phone_number, surname, role_id, provider, picture)
values ('Novi Sad', 'darko.darkovic@gmail.com', 'Darko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy',
        '+(381)-64-8475222', 'Darkovic', 2, 0, 'default.jpg'),
       ('Novi Sad', 'mirko.ivanic@gmail.com', 'Mirko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy',
        '+(381)-64-5679210', 'Ivanic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'branko.lazic@gmail.com', 'Branko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy',
        '+(381)-64-5679210', 'Lazic', 3, 0, 'default.jpg'),
       ('Novi Sad', 'vujadin.savic@gmail.com', 'Vujadin', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy',
        '+(381)-64-3918507', 'Savic', 3, 0, 'default.jpg');

insert into tags (id, name)
values (1, 'Pet friendly');

insert into drivers (is_available, is_blocked, id, vehicle_id, location_id, has_future_drive)
values (false, false, 2, 1, 1, true), (false , false, 3, 2, 1, false), (false, false, 4, 3, 5, false);

insert into passengers (activated, is_blocked, id, tokens)
values (true, false, 1, 500);

insert into routes (route_name, route_idx, length, expected_time)
values ('PUSKINOVA 27 - KISACKA 15', 0, 300.00, 1000.00);

insert into route_waypoints (route_id, point_id)
values (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

insert into temp_drives (drive_id, length, num_accepted_passengers, price, start_date, status, route_id, vehicle_type_id)
values (null, 3, 0, 300, '2023-01-31 14:00:00', 4, 1, 1);

insert into temp_drive_passengers (temp_drive_id, passenger_id)
values (1, 1);

insert into drives (start_date, end_date, price, length, status, driver, route_id)
values ('2023-01-23 14:00:00', '2023-01-23 14:30:00', 4, 25, 3, 2, 1),
       ('2023-02-04 14:00:00', '2023-02-04 14:15:00', 4.25, 1023, 2, 3, 1),
       ('2023-02-04 15:00:00', '2023-02-04 15:15:00', 4.25, 1023, 2, 4, 1),
       ('2023-02-04 15:00:00', '2023-02-04 15:15:00', 4.25, 1023, 2, 3, 1);

insert into drive_passengers (drive_id, passenger_id)
values (1, 1);

insert into drive_tags (drive_id, tag_id)
values (1, 1);



