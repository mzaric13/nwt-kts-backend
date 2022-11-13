insert into vehicle_types (name) values ('SUV'), ('Crossover'), ('Sedan'), ('Truck'), ('Hatchback'), ('Minivan'),
                                        ('Hybrid');

insert into roles (name) values ('admin'), ('passenger'), ('driver');

insert into vehicles (name, registration_number, type_id)
values ('Audi SQ7', 'NS482AL', 1), ('Citroen C4 Picasso', 'NS205MM', 6), ('BMW i3', 'NS943HG', 7),
       ('Peugeot 208', 'NS122MK', 5), ('Volkswagen Jetta', 'NS003ZX', 3);

insert into users (city, email, name, password, phone_number, surname, role_id)
values ('Novi Sad', 'mirko.ivanic@gmail.com', 'Mirko', 'Ivanic.Mirko0', '+(381)-64-5679210', 'Ivanic', 3),
       ('Novi Sad', 'vujadin.savic@gmail.com', 'Vujadin', 'Savic.Vujadin0', '+(381)-64-3918507', 'Savic', 3),
       ('Novi Sad', 'branko.lazic@gmail.com', 'Branko', 'Lazic.Branko0', '+(381)-64-8301397', 'Lazic', 3),
       ('Novi Sad', 'marko.simonovic@gmail.com', 'Marko', 'Simonovic.Marko0', '+(381)-64-0048629', 'Simonovic', 3),
       ('Novi Sad', 'marko.gobeljic@gmail.com', 'Marko', 'Gobeljic.Marko0', '+(381)-64-8475291', 'Gobeljic', 3),
       ('Novi Sad', 'darko.darkovic@gmail.com', 'Darko', 'Darkovic.Darko0', '+(381)-64-8475222', 'Darkovic', 2);

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