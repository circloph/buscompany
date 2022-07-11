DROP DATABASE IF EXISTS buscompany;
CREATE DATABASE buscompany;
USE buscompany;

CREATE TABLE roles (
    id int AUTO_INCREMENT,
    name ENUM('USER', 'ADMIN'),
    PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE users (
    id int AUTO_INCREMENT,
    roleId int,
    lastname varchar(100),
    firstname varchar(100),
    patronymic varchar(100),
    login varchar(100),
    password varchar(100),
    enabled BOOLEAN,
    UNIQUE KEY (login),
    KEY (login),
    PRIMARY KEY(id),
    FOREIGN KEY(roleId) REFERENCES roles(id)
)  ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE clients (
    userId int,
    email varchar(100) DEFAULT NULL,
    numberPhone varchar(100) DEFAULT NULL,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
)  ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE administration (
    userId int,
    position varchar(100) DEFAULT NULL,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
)  ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE buses (
    id int AUTO_INCREMENT,
    busName varchar(100),
    placeCount int,
    PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trips (
    id int AUTO_INCREMENT,
    userId int,
    busName varchar(100),
    fromStation varchar(100),
    toStation varchar(100),
    start varchar(100),
    duration varchar(100),
    price int,
    approved BOOLEAN,
    KEY busName (busName),
    KEY toStation (toStation),
    KEY fromStation (fromStation),
    PRIMARY KEY(id),
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE schedule (
    tripId int,
    fromDate varchar(100),
    toDate varchar(100),
    period varchar(100),
    FOREIGN KEY (tripId) REFERENCES trips (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE dates (
    id int AUTO_INCREMENT,
    dayName varchar(100),
    KEY dayName (dayName),
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip_dates (
    tripId int,
    dateId int,
    amountPlace int,
    FOREIGN KEY (tripId) REFERENCES trips (id) ON DELETE CASCADE,
    FOREIGN KEY (dateId) REFERENCES dates (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE orders (
    id int AUTO_INCREMENT,
    tripId int,
    userId int,
    date varchar(100),
    PRIMARY KEY(id),
    KEY date (date),
    FOREIGN KEY (tripId) REFERENCES trips (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE passengers (
    id int AUTO_INCREMENT,
    firstName varchar(100),
    lastName varchar(100),
    passport varchar(100),
    PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE orders_passengers (
    orderId int,
    passengerId int,
    FOREIGN KEY (orderId) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (passengerId) REFERENCES passengers (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE places (
    orderId int,
    passengerId int,
    place int,
    FOREIGN KEY (orderId) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (passengerId) REFERENCES passengers (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE session_user (
    userId int,
    sessionId varchar(100),
    expiration varchar(100)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

insert into buses (busName, placeCount) values ("MERCEDES", 24);
insert into buses (busName, placeCount) values ("SCANIA", 20);

insert into roles (id, name) values (1, 'USER');
insert into roles (id, name) values (2, 'ADMIN');

CREATE TRIGGER before_trips_delete BEFORE DELETE on trips
FOR EACH ROW
    DELETE dates from dates join trip_dates on dates.id = trip_dates.dateId 
    where trip_dates.tripId = old.id;

CREATE TRIGGER before_orders_delete BEFORE DELETE on orders
FOR EACH ROW
    DELETE passengers from passengers join orders_passengers on passengers.id = orders_passengers.passengerId 
    where orders_passengers.orderId = old.id;
