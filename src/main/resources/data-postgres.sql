insert into vehicle_types (name) values ('SUV'), ('Crossover'), ('Sedan'), ('Truck'), ('Hatchback'), ('Minivan'),
                                        ('Hybrid');

insert into roles (name) values ('ROLE_ADMIN'), ('ROLE_PASSENGER'), ('ROLE_DRIVER');

insert into vehicles (name, registration_number, type_id)
values ('Audi SQ7', 'NS482AL', 1), ('Citroen C4 Picasso', 'NS205MM', 6), ('BMW i3', 'NS943HG', 7),
       ('Peugeot 208', 'NS122MK', 5), ('Volkswagen Jetta', 'NS003ZX', 3);

-- lozinka je svima: sifra123
insert into users (city, email, name, password, phone_number, surname, role_id, provider)
values ('Novi Sad', 'mirko.ivanic@gmail.com', 'Mirko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-5679210', 'Ivanic', 3, 0),
       ('Novi Sad', 'vujadin.savic@gmail.com', 'Vujadin', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-3918507', 'Savic', 3, 0),
       ('Novi Sad', 'branko.lazic@gmail.com', 'Branko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8301397', 'Lazic', 3, 0),
       ('Novi Sad', 'marko.simonovic@gmail.com', 'Marko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-0048629', 'Simonovic', 3, 0),
       ('Novi Sad', 'marko.gobeljic@gmail.com', 'Marko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8475291', 'Gobeljic', 3, 0),
       ('Novi Sad', 'darko.darkovic@gmail.com', 'Darko', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-64-8475222', 'Darkovic', 2, 0),
       ('Novi Sad', 'admin.admin@gmail.com', 'Zoran', '$2a$12$1YdTZA0jjbEM5Ey2piVIpuVvH9vYYvCW69Sau3lFSN7Hw.wscUhYy', '+(381)-62-3321222', 'Bukorac', 1, 0);

insert into drivers (is_available, is_blocked, id, vehicle_id)
values (false, false, 1, 1), (false, false, 2, 3), (false, false, 3, 2), (false, false, 4, 4), (false, false, 5, 5);

insert into passengers (activated, is_blocked, id)
values (true, false, 6);

insert into tags (id, name)
values (1, 'Pet friendly'),
       (2, 'Baby friendly');

insert into drives (start_date, end_date, price, length, inconsistent_drive_reasoning, status, driver)
values ('2022-11-10 14:00:00', '2022-11-10 14:30:00', 300, 50, 'aaaaaa', 0, 1);

insert into drive_passengers (drive_id, passenger_id)
values (1, 6);

insert into drive_tags (drive_id, tag_id)
values (1, 1),
       (1, 2);