use msc;

DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users(
	first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    user_email varchar(100) NOT NULL,
    password_hash varchar(1000) NOT NULL,
    user_role varchar(20) NOT NULL,
    city varchar(255),
    pass_change_date date,
    last_log_in_time datetime,
    creation_date datetime,
    active bool NOT NULL,
    state tinyint,
    recovery_token varchar(50),
    auth_token varchar(50),
    
    PRIMARY KEY (user_email)
);

INSERT INTO `msc`.`users` (first_name, last_name, user_email, password_hash, user_role, city, state, active)
VALUES ('Nazar', 'Babii', 'nazar@telclic.net', '$2a$10$ST6AzsGqb35P0VTB9qY1nOnjvuL7q1vc6sXhj0P0JVi1JxFc5PhbW', '1', 'Dublin', 3, true);

INSERT INTO `msc`.`users` (first_name, last_name, user_email, password_hash, user_role, city, state, active)
VALUES ('Andrew', 'Reikin', 'andrew@telclic.net', '$2a$10$ST6AzsGqb35P0VTB9qY1nOnjvuL7q1vc6sXhj0P0JVi1JxFc5PhbW', '1', 'Dublin', 3, true);

INSERT INTO `msc`.`users` (first_name, last_name, user_email, password_hash, user_role, city, state, active)
VALUES ('Jeffrey', 'Roe', 'jeffrey@telclic.net', '$2a$10$ST6AzsGqb35P0VTB9qY1nOnjvuL7q1vc6sXhj0P0JVi1JxFc5PhbW', '1,0,0,0,0', 'Dublin', 3, true);

INSERT INTO `msc`.`users` (first_name, last_name, user_email, password_hash, user_role, city, state, active)
VALUES ('Mithran', 'Abraham', 'mithran@sremium.com', '$2a$10$ST6AzsGqb35P0VTB9qY1nOnjvuL7q1vc6sXhj0P0JVi1JxFc5PhbW', '1,0,0,0,0', 'Dublin', 3, true);

INSERT INTO `msc`.`users` (first_name, last_name, user_email, password_hash, user_role, city, state, active)
VALUES ('Sean', 'O\'Dwyer', 'sean@telclic.net', '$2a$10$ST6AzsGqb35P0VTB9qY1nOnjvuL7q1vc6sXhj0P0JVi1JxFc5PhbW', '1,0,0,0,0', 'Dublin', 3, true);
use msc;


drop table if exists report_used_assets;
drop table if exists repair_reports_operators;
drop table if exists routine_review_bikes_checked;
drop table if exists routine_review;
drop table if exists report_used_assets;
drop table if exists report_repair_reasons;
drop table if exists repair_jobs;
drop table if exists repair_reports;
drop table if exists bike_stations;
drop table if exists bikes;
drop table if exists report_reasons;
drop table if exists repair_reasons;
drop table if exists stock_usage_report_assets;
drop table if exists stock_usage_reports;
drop table if exists central_depot;
drop table if exists assets_transfer_queue;
drop table if exists assets_current_values;
drop table if exists assets_marginal_values;
drop table if exists request_products_types_list;
drop table if exists distribution_assets;
drop table if exists distribution;
drop table if exists stock_requests;
drop table if exists split_purchase_orders_queue;
drop table if exists purchase_orders_products;
drop table if exists purchase_orders;
drop table if exists products;
drop table if exists suppliers;
drop table if exists types_of_assets;

drop table if exists operations_log;
drop table if exists system_operations;
drop table if exists system_parameters;
drop table if exists states;
drop table if exists `schemes`;

CREATE TABLE IF NOT EXISTS suppliers(
	id int auto_increment NOT NULL,
	`name` varchar(100) NOT NULL,
    phone varchar(15) NULL,
    email varchar(100) NULL,
    contact varchar (255) NULL,
    website varchar(1000) NULL,
    fulfillment tinyint(1) NULL,
    
    PRIMARY KEY (id)
);

INSERT INTO  suppliers (`name`, phone, email, contact, website, fulfillment)
VALUES ('FULFILL_Sup', '(021) 482 3711', '', '', '', 1);

INSERT INTO  suppliers (`name`, phone, email, contact, website, fulfillment)
VALUES ('STATION_Sup', '(021) 483 3711', '', '', '', 0);

INSERT INTO  suppliers (`name`, phone, email, contact, website, fulfillment)
VALUES ('BIKE_Sup', '(021) 483 3711', '', '', '', 0);

INSERT INTO  suppliers (`name`, phone, email, contact, website, fulfillment)
VALUES ('TRUCK_Sup', '(021) 483 3711', '', '', '', 0);


CREATE TABLE IF NOT EXISTS types_of_assets(
	id int auto_increment NOT NULL,
    asset_group varchar(255) NOT NULL,
    type_name varchar(255) NOT NULL,
    group_name varchar(255) NOT NULL,

    PRIMARY KEY(id)
);

CREATE UNIQUE INDEX unique_name ON types_of_assets (group_name);

CREATE TABLE IF NOT EXISTS products(
	id int auto_increment NOT NULL,
	product_name varchar(500) NULL,
    delivery_time int(10) NULL,
    supplier_id int NOT NULL,
    type_id int NOT NULL,
    min_order int null,
    
    PRIMARY KEY (id),
    
    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id),
    
    FOREIGN KEY (type_id)
    REFERENCES types_of_assets(`id`)
);

INSERT INTO  types_of_assets (asset_group, type_name) VALUES ('Bike', 'Bottom bracket');
INSERT INTO  products (product_name, supplier_id, type_id)
VALUES ('', 3, 1);



INSERT INTO  types_of_assets (asset_group, type_name) VALUES ('Station', 'Bollards');
INSERT INTO  products (product_name, supplier_id, type_id)
VALUES ('', 2, 11);


INSERT INTO  types_of_assets (asset_group, type_name) VALUES ('Truck', 'Spring Bracket');
INSERT INTO  products (product_name, supplier_id, type_id)
VALUES ('',4, 41);


INSERT INTO  types_of_assets (asset_group, type_name) VALUES ('Fulfillment', 'Card Reader');
INSERT INTO  products (product_name, supplier_id, type_id)
VALUES ('', 1, 50);




create table if not exists `schemes`(
	`name` varchar(100) NOT NULL,
    
    PRIMARY KEY (`name`)
);

insert into `schemes` values ('Cork');
insert into `schemes` values ('Dublin');
insert into `schemes` values ('Limerick');
insert into `schemes` values ('Galway');


create table if not exists assets_marginal_values(
	product_type_id int NOT NULL,
    order_value int NOT NULL,
    lower_value int NOT NULL,
    `scheme` varchar(100) NOT NULL,
    
    PRIMARY KEY (product_type_id, `scheme`),
    
    FOREIGN KEY (product_type_id)
    REFERENCES types_of_assets(`id`),
    
    foreign key (`scheme`)
    references `schemes`(`name`)
);

insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (1, 150, 100, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (2, 1500, 30, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (3, 150, 60, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (4, 70, 80, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (5, 10, 10, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (6, 16, 15, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (7, 1000, 19, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (8, 150, 40, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (9, 350, 90, 'Cork');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (10, 850, 100, 'Cork');

insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (1, 90, 100, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (2, 10, 30, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (3, 50, 60, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (4, 70, 80, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (5, 16, 10, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (6, 19, 15, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (7, 100, 19, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (8, 190, 40, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (9, 50, 90, 'Dublin');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (10, 350, 100, 'Dublin');

insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (1, 90, 100, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (2, 10, 30, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (3, 50, 60, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (4, 70, 80, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (5, 16, 10, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (6, 19, 15, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (7, 100, 19, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (8, 190, 40, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (9, 50, 90, 'Limerick');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (10, 350, 100, 'Limerick');

insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (1, 90, 100, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (2, 10, 30, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (3, 50, 60, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (4, 70, 80, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (5, 16, 10, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (6, 19, 15, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (7, 100, 19, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (8, 190, 40, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (9, 50, 90, 'Galway');
insert into assets_marginal_values(product_type_id, order_value, lower_value, `scheme`)
values (10, 350, 100, 'Galway');


create table if not exists assets_current_values(
	product_type_id int NOT NULL,
    quantity int NOT NULL,
    `scheme` varchar(100) NOT NULL,
    
	PRIMARY KEY (product_type_id, `scheme`),
    
    FOREIGN KEY (product_type_id)
    REFERENCES types_of_assets(`id`),
    
    foreign key (`scheme`)
    references `schemes`(`name`)
);

insert into assets_current_values values (1, 200, 'Cork');
insert into assets_current_values values (2, 300, 'Cork');
insert into assets_current_values values (3, 20, 'Cork');
insert into assets_current_values values (4, 90, 'Cork');
insert into assets_current_values values (5, 218, 'Cork');
insert into assets_current_values values (6, 460, 'Cork');
insert into assets_current_values values (7, 200, 'Cork');
insert into assets_current_values values (8, 2590, 'Cork');
insert into assets_current_values values (9, 301, 'Cork');
insert into assets_current_values values (10, 120, 'Cork');

insert into assets_current_values values (1, 20, 'Dublin');
insert into assets_current_values values (2, 70, 'Dublin');
insert into assets_current_values values (3, 10, 'Dublin');
insert into assets_current_values values (4, 50, 'Dublin');
insert into assets_current_values values (5, 117, 'Dublin');
insert into assets_current_values values (6, 46, 'Dublin');
insert into assets_current_values values (7, 203, 'Dublin');
insert into assets_current_values values (8, 359, 'Dublin');
insert into assets_current_values values (9, 31, 'Dublin');
insert into assets_current_values values (10, 111, 'Dublin');

insert into assets_current_values values (1, 20, 'Limerick');
insert into assets_current_values values (2, 70, 'Limerick');
insert into assets_current_values values (3, 10, 'Limerick');
insert into assets_current_values values (4, 50, 'Limerick');
insert into assets_current_values values (5, 117, 'Limerick');
insert into assets_current_values values (6, 46, 'Limerick');
insert into assets_current_values values (7, 203, 'Limerick');
insert into assets_current_values values (8, 359, 'Limerick');
insert into assets_current_values values (9, 31, 'Limerick');
insert into assets_current_values values (10, 111, 'Limerick');

insert into assets_current_values values (1, 20, 'Galway');
insert into assets_current_values values (2, 70, 'Galway');
insert into assets_current_values values (3, 10, 'Galway');
insert into assets_current_values values (4, 50, 'Galway');
insert into assets_current_values values (5, 117, 'Galway');
insert into assets_current_values values (6, 46, 'Galway');
insert into assets_current_values values (7, 203, 'Galway');
insert into assets_current_values values (8, 359, 'Galway');
insert into assets_current_values values (9, 31, 'Galway');
insert into assets_current_values values (10, 111, 'Galway');

create table if not exists assets_transfer_queue(
	id int auto_increment not null,
    product_type_id int not null,
    transfer_from_scheme varchar(100) not null,
    transfer_to_scheme varchar(100) not null,
    quantity int not null,
    
    primary key (id),
    
    foreign key (product_type_id)
    references types_of_assets (id),
    
    foreign key (transfer_from_scheme)
    references `schemes` (`name`),
    
    foreign key (transfer_to_scheme)
    references `schemes` (`name`)
);


create table if not exists system_operations(
	action_code int NOT NULL,
    action_group varchar(100) NOT NULL,
    action_name varchar(100) NOT NULL,
    
    primary key (action_code)
);

insert into system_operations values (1100, 'LogIn', 'User Login');
insert into system_operations values (1101, 'LogIn', 'User Logout');
insert into system_operations values (1102, 'LogIn', 'User Change Password');
insert into system_operations values (1103, 'LogIn', 'User Forgot Password');
insert into system_operations values (1104, 'User Management', 'Add New User');
insert into system_operations values (1105, 'User Management', 'Delete User');
insert into system_operations values (1106, 'User Management', 'Change User');
insert into system_operations values (1107, 'User Management', 'Reset Password');
insert into system_operations values (1200, 'Asset Profile', 'Add New Asset');
insert into system_operations values (1201, 'Asset Profile', 'Delete Asset');
insert into system_operations values (1202, 'Asset Profile', 'Change Asset');
insert into system_operations values (1300, 'Scheme Stock Control', 'Change Lower Values');
insert into system_operations values (1301, 'Scheme Stock Control', 'Save Transfer Record');
insert into system_operations values (1302, 'Scheme Stock Control', 'Change Transfer Record');
insert into system_operations values (1303, 'Scheme Stock Control', 'Delete Transfer Record');
insert into system_operations values (1304, 'Scheme Stock Control', 'Clear Transfer Que');
insert into system_operations values (1305, 'Scheme Stock Control', 'Create Distributions');
insert into system_operations values (1400, 'Supplier Profile', 'Add New Supplier');
insert into system_operations values (1401, 'Supplier Profile', 'Change Supplier');
insert into system_operations values (1402, 'Supplier Profile', 'Delete Supplier');
insert into system_operations values (1500, 'Stock Requests', 'Merge All Stock Requests');
insert into system_operations values (1501, 'Stock Requests', 'Merge Stock Request');
insert into system_operations values (1502, 'Stock Requests', 'Save Transfer Record');
insert into system_operations values (1503, 'Stock Requests', 'Create Distribution');
insert into system_operations values (1504, 'Stock Requests', 'Add Manual Stock Request');
insert into system_operations values (1600, 'Purchase Orders', 'Add New Order');
insert into system_operations values (1601, 'Purchase Orders', 'Send Order');
insert into system_operations values (1602, 'Purchase Orders', 'Edit Item Amount');
insert into system_operations values (1603, 'Purchase Orders', 'Delete Order Item');
insert into system_operations values (1604, 'Purchase Orders', 'Split Order Item');
insert into system_operations values (1605, 'Purchase Orders', 'Delete Queue Record');
insert into system_operations values (1606, 'Purchase Orders', 'Clear Queue Records');
insert into system_operations values (1607, 'Purchase Orders', 'Merge Queue');
insert into system_operations values (1608, 'Purchase Orders', 'Close Order');
insert into system_operations values (1700, 'Distributions', 'Ship Distribution');
insert into system_operations values (1701, 'Distributions', 'Close Distribution');
insert into system_operations values (1800, 'System Error', 'Exception');
insert into system_operations values (1900, 'Stock Balance', 'Use Stocks');
insert into system_operations values (1901, 'Stock Balance', 'Place Stocks');

create table if not exists operations_log(
	id int auto_increment NOT NULL,
	action_code int NOT NULL,
    user_email varchar(100) NOT NULL,
    data_object varchar(20000) NULL,
    time_stamp datetime NOT NULL,
    
    primary key(id),
    
    foreign key (action_code)
    references system_operations (action_code)
);

create table if not exists states(
	id int NOT NULL,
    `name` varchar(100) NOT NULL,
    `type` int NOT NULL,
    
    primary key(id)
);

insert into states value (1, 'New', 1);
insert into states value (2, 'Merged', 1);
insert into states value (3, 'Distributed', 1);
insert into states value (4, 'Fulfilled', 1);
insert into states value (11, 'New', 2);
insert into states value (12, 'Sent', 2);
insert into states value (13, 'Partially Fulfilled', 2);
insert into states value (14, 'Fulfilled', 2);
insert into states value (21, 'Issued', 3);
insert into states value (22, 'Received', 3);
insert into states value (23, 'Shipped', 3);
insert into states value (31, 'Used', 4);
insert into states value (32, 'Placed', 4);
insert into states value (41, 'New', 5);
insert into states value (42, 'In Progress', 5);
insert into states value (43, 'Done', 5);
insert into states values (44, 'Pending', 5);

-- currently not using
create table if not exists central_depot(
	product_type_id int NOT NULL,
    amount int NOT NULL,
    
    primary key (product_type_id),
    
    foreign key (product_type_id)
    references types_of_assets (id)
);


create table if not exists stock_requests(
	id int auto_increment NOT NULL,
    `scheme` varchar(100) NOT NULL,
    state_id int NOT NULL,
    manual tinyint(1) NULL,
    notes varchar(2000),
    creation_date datetime,
    state_change_date datetime,
    
    primary key (id),
        
    foreign key (`scheme`)
    references `schemes`(`name`),
    
    foreign key (state_id)
    references states(id)
    
);

create table if not exists request_products_types_list(
	id_request int NOT NULL,
    id_prod_type int NOT NULL,
    order_value int NOT NULL,
    
    primary key (id_request, id_prod_type),
    
    foreign key (id_request)
    references stock_requests(id),
    
    foreign key (id_prod_type)
    references types_of_assets(id)
);

create table if not exists purchase_orders(
	id int auto_increment NOT NULL,
    id_supplier int NOT NULL,
    id_state int NOT NULL,
    invoice varchar(200) NULL,
    notes varchar(2000) NULL,
    state_change_date datetime,
    `comment` varchar(10000) NULL,
    
    primary key (id),
    
    foreign key (id_supplier)
    references suppliers(id),
    
    foreign key (id_state)
    references states(id)
);

create table if not exists purchase_orders_products(
	id_purchase_order int NOT NULL,
    id_product int NOT NULL,
    amount int NOT NULL,
    confirmed int NULL,
    
    primary key (id_purchase_order, id_product),
    
    foreign key (id_purchase_order)
    references purchase_orders(id),
    
    foreign key (id_product)
    references products (id)
    
);

create table if not exists split_purchase_orders_queue(
	id int auto_increment NOT NULL,
    id_product int NOT NULL,
    quantity int NOT NULL,
    old_order_id int,
    
    primary key (id),
    
    foreign key (id_product)
    references products (id)
);


create table if not exists distribution(
	id int auto_increment NOT NULL,
    stock_request_id int NULL,
    scheme_from_name varchar(100) NULL,
    scheme_to_name varchar(100) NOT NULL,
    state_id int NOT NULL,
    creation_date datetime,
    state_change_date datetime,
    notes varchar(2000),
    
    primary key (id),
    
    foreign key (stock_request_id)
    references stock_requests(id),
    
    foreign key (scheme_from_name)
    references schemes(`name`),
    
    foreign key (scheme_to_name)
    references schemes(`name`),
    
    foreign key (state_id)
    references states(id)
);

create table if not exists distribution_assets(
	dist_id int NOT NULL,
    type_of_asset_id int NOT NULL,
    quantity int NOT NULL,
    
    primary key (dist_id, type_of_asset_id),

	foreign key (dist_id)
    references distribution(id),
    
    foreign key (type_of_asset_id)
    references types_of_assets(id)
);

create table if not exists stock_usage_reports(
	id int auto_increment NOT NULL,
    scheme_name varchar(100) NOT NULL,
    creation_date datetime,
    state_id int NOT NULL,
    notes varchar(2000),
    
    primary key (id),
    
    foreign key (scheme_name)
    references `schemes`(`name`),
    
    foreign key (state_id)
    references states(id)
);

create table if not exists stock_usage_report_assets(
	report_id int NOT NULL,
    type_of_asset_id int NOT NULL,
    amount int,
    
    primary key(report_id, type_of_asset_id),
    
    foreign key (report_id)
    references stock_usage_reports(id),
    
    foreign key (type_of_asset_id)
    references types_of_assets(id)
);


create table if not exists bike_stations(
	id int auto_increment NOT NULL,
    scheme_name varchar(100) NOT NULL,
    location varchar(255) NOT NULL,
    geo_lat double,
    geo_long double,
    
    primary key(id),
    
    foreign key (scheme_name)
    references `schemes`(`name`)
);

insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2003, 'Cork', 'Bandfield', 51.89580557, -8.4891363);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2011, 'Cork', 'Bishop St.', 51.89468826, -8.4790268);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2026, 'Cork', 'Brian Boru Bridge', 51.900405, -8.465153);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2027, 'Cork', 'Bus Station', 51.89951532, -8.46695074);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2012, 'Cork', 'Camden Quay', 51.901054, -8.473342);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2031, 'Cork', 'Clontarf Street', 51.8984818691871, -8.46562933177544);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2020, 'Cork', 'Coburg St.', 51.90155283, -8.47056736);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2023, 'Cork', 'College of Commerce', 51.8953, -8.469797);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2029, 'Cork', 'Cork City Hall', 51.897, -8.466);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2025, 'Cork', 'Cork School of Music', 51.8963170750549, -8.46809252166047);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2013, 'Cork', 'Corn Market St.', 51.9, -8.477);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2004, 'Cork', 'Dyke Parade', 51.89718531, -8.48458467);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2021, 'Cork', 'Emmet Place', 51.9002081221452, -8.47270466388061);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2024, 'Cork', 'Father Mathew Statue', 51.89967344, -8.4706278);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2002, 'Cork', 'Fitzgerald\'s Park', 51.89555327, -8.49341266);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2001, 'Cork', 'Gaol Walk', 51.893604, -8.494174);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2017, 'Cork', 'Grand Parade', 51.8974802317581, -8.47536977381303);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2009, 'Cork', 'Grattan St.', 51.8984737, -8.47977966);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2034, 'Cork', 'Kent East', 51.90115, -8.4589);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2032, 'Cork', 'Kent Station', 51.90196195, -8.45821512);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2033, 'Cork', 'Kent West', 51.90128, -8.45919);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2014, 'Cork', 'Lapp\'s Quay', 51.898144, -8.465735);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2030, 'Cork', 'Lower Glanmire Rd.', 51.90137057, -8.46411816);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2005, 'Cork', 'Mercy Hospital', 51.89911495, -8.48225676);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2008, 'Cork', 'North Main St.', 51.89974733, -8.47844005);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2018, 'Cork', 'Peace Park', 51.8961946992686, -8.47347588279142);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2007, 'Cork', 'Pope\'s Quay', 51.901632, -8.477385);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2019, 'Cork', 'South Gate Bridge', 51.8954943912587, -8.47586514429047);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2016, 'Cork', 'South Main St.', 51.8969460530689, -8.47689553164735);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2022, 'Cork', 'South Mall', 51.8968385551608, -8.46989982762231);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2006, 'Cork', 'St. Fin Barre\'s Bridge', 51.89710212, -8.48196155);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2015, 'Cork', 'St. Patricks St.', 51.89850471, -8.47261531);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(2010, 'Cork', 'Wandesford Quay', 51.896492, -8.48004);

insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3006, 'Limerick', 'Abbey Bridge', 52.66642, -8.618478);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3009, 'Limerick', 'Arthur\'s Quay', 52.664822, -8.626348);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3016, 'Limerick', 'Baker Place', 52.660052, -8.627593);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3008, 'Limerick', 'Bedford Row', 52.663725, -8.628751);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3013, 'Limerick', 'Bishop\'s Quay', 52.662172, -8.631993);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3001, 'Limerick', 'Clancy\'s Strand', 52.668746, -8.627809);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3019, 'Limerick', 'Colbert Station', 52.659188, -8.62527);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3004, 'Limerick', 'Island Road Junction', 52.668627, -8.620756);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3002, 'Limerick', 'King John\'s Castle', 52.669927, -8.624656);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3022, 'Limerick', 'Mary Immaculate College', 52.65317, -8.638653);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3020, 'Limerick', 'Mount Saint Alphonsus', 52.655574, -8.635766);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3023, 'Limerick', 'Mount Saint Vincent', 52.652563, -8.635751);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3017, 'Limerick', 'Newenham St.', 52.658635, -8.632447);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3012, 'Limerick', 'Newtown Mahon', 52.660023, -8.62021);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3007, 'Limerick', 'O\'Callaghan Strand', 52.665794, -8.631321);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3021, 'Limerick', 'O\'Connell Avenue', 52.65511, -8.634783);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3014, 'Limerick', 'O\'Connell Street', 52.661273, -8.629543);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3018, 'Limerick', 'Pery Square', 52.658147, -8.629443);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3015, 'Limerick', 'Roches Street', 52.661081, -8.625007);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3011, 'Limerick', 'Saint John\'s Cathedral', 52.662964, -8.61797);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3003, 'Limerick', 'Saint Mary\'s Cathedral', 52.667706, -8.624409);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3005, 'Limerick', 'The Granary', 52.6664, -8.62267);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(3010, 'Limerick', 'The Milk Market', 52.663496, -8.62168);

insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4027, 'Galway', 'Bodkin', 53.281832, -9.047589);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4009, 'Galway', 'Brown Doorway', 53.275, -9.05);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4003, 'Galway', 'Cathedral', 53.275835, -9.057245);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4017, 'Galway', 'Claddagh Basin', 53.269278, -9.057244);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4006, 'Galway', 'County Hall', 53.276, -9.048);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4022, 'Galway', 'Eyre Square South', 53.274255, -9.048138);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4016, 'Galway', 'Fairgreen', 53.274, -9.045);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4021, 'Galway', 'Father Burke Road', 53.26758, -9.058958);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4007, 'Galway', 'Galway City Hall', 53.276, -9.043);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4020, 'Galway', 'Gaol Road', 53.274, -9.057);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4029, 'Galway', 'Glenina', 53.278353, -9.017439);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4002, 'Galway', 'Headford Road', 53.2783491544269, -9.05069143207334);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4026, 'Galway', 'Kingfisher, NUIG', 53.281582, -9.063993);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4028, 'Galway', 'Lough Atalia', 53.280081, -9.0356);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4012, 'Galway', 'Mainguard Street', 53.27204, -9.054343);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4015, 'Galway', 'Merchants Gate', 53.272138, -9.050262);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4019, 'Galway', 'New Dock Street', 53.270334, -9.052764);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4008, 'Galway', 'Newtownsmith', 53.274235, -9.054032);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4023, 'Galway', 'O\'Shaughnessy Bridge', 53.277241, -9.057436);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4031, 'Galway', 'Park & Ride NUIG', 53.288501, -9.069596);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4025, 'Galway', 'Sacred Heart (Seamus Quirke Rd)', 53.277007, -9.073295);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4024, 'Galway', 'University Hospital Galway', 53.277191, -9.063869);
insert into bike_stations(id, scheme_name, location, geo_lat, geo_long) values(4005, 'Galway', 'Wood Quay', 53.2763776707119, -9.05307962898119);



create table if not exists bikes(
	id int auto_increment NOT NULL,
    scheme_name varchar(100) NOT NULL,
    `number` varchar(255) NOT NULL,
    
    primary key(id),
    
    foreign key (scheme_name)
    references `schemes`(`name`)
);

insert into bikes(scheme_name, `number`) values('Cork','20222');
insert into bikes(scheme_name, `number`) values('Cork','20224');
insert into bikes(scheme_name, `number`) values('Cork','20225');
insert into bikes(scheme_name, `number`) values('Cork','20229');
insert into bikes(scheme_name, `number`) values('Cork','20233');
insert into bikes(scheme_name, `number`) values('Cork','20238');
insert into bikes(scheme_name, `number`) values('Cork','20240');
insert into bikes(scheme_name, `number`) values('Cork','20248');
insert into bikes(scheme_name, `number`) values('Cork','20250');
insert into bikes(scheme_name, `number`) values('Cork','20254');
insert into bikes(scheme_name, `number`) values('Cork','20256');
insert into bikes(scheme_name, `number`) values('Cork','20266');
insert into bikes(scheme_name, `number`) values('Cork','20281');
insert into bikes(scheme_name, `number`) values('Cork','20283');
insert into bikes(scheme_name, `number`) values('Cork','20294');
insert into bikes(scheme_name, `number`) values('Cork','20300');
insert into bikes(scheme_name, `number`) values('Cork','20306');
insert into bikes(scheme_name, `number`) values('Cork','20309');
insert into bikes(scheme_name, `number`) values('Cork','20312');
insert into bikes(scheme_name, `number`) values('Cork','20314');
insert into bikes(scheme_name, `number`) values('Cork','20317');
insert into bikes(scheme_name, `number`) values('Cork','20318');
insert into bikes(scheme_name, `number`) values('Cork','20320');
insert into bikes(scheme_name, `number`) values('Cork','20339');
insert into bikes(scheme_name, `number`) values('Cork','20345');
insert into bikes(scheme_name, `number`) values('Cork','20355');
insert into bikes(scheme_name, `number`) values('Cork','20358');
insert into bikes(scheme_name, `number`) values('Cork','20359');
insert into bikes(scheme_name, `number`) values('Cork','20366');
insert into bikes(scheme_name, `number`) values('Cork','20369');
insert into bikes(scheme_name, `number`) values('Cork','20371');
insert into bikes(scheme_name, `number`) values('Cork','20375');
insert into bikes(scheme_name, `number`) values('Cork','20381');
insert into bikes(scheme_name, `number`) values('Cork','20382');
insert into bikes(scheme_name, `number`) values('Cork','20383');
insert into bikes(scheme_name, `number`) values('Cork','20401');
insert into bikes(scheme_name, `number`) values('Cork','20414');
insert into bikes(scheme_name, `number`) values('Cork','20415');
insert into bikes(scheme_name, `number`) values('Cork','20418');
insert into bikes(scheme_name, `number`) values('Cork','20421');
insert into bikes(scheme_name, `number`) values('Cork','20424');
insert into bikes(scheme_name, `number`) values('Cork','20426');
insert into bikes(scheme_name, `number`) values('Cork','20427');
insert into bikes(scheme_name, `number`) values('Cork','20428');
insert into bikes(scheme_name, `number`) values('Cork','20429');
insert into bikes(scheme_name, `number`) values('Cork','20430');
insert into bikes(scheme_name, `number`) values('Cork','20431');
insert into bikes(scheme_name, `number`) values('Cork','20432');
insert into bikes(scheme_name, `number`) values('Cork','20433');
insert into bikes(scheme_name, `number`) values('Cork','20434');
insert into bikes(scheme_name, `number`) values('Cork','20435');
insert into bikes(scheme_name, `number`) values('Cork','20436');
insert into bikes(scheme_name, `number`) values('Cork','20437');
insert into bikes(scheme_name, `number`) values('Cork','20438');
insert into bikes(scheme_name, `number`) values('Cork','20439');
insert into bikes(scheme_name, `number`) values('Cork','20440');
insert into bikes(scheme_name, `number`) values('Cork','20443');
insert into bikes(scheme_name, `number`) values('Cork','20444');
insert into bikes(scheme_name, `number`) values('Cork','20446');
insert into bikes(scheme_name, `number`) values('Cork','20447');
insert into bikes(scheme_name, `number`) values('Cork','20448');
insert into bikes(scheme_name, `number`) values('Cork','20449');
insert into bikes(scheme_name, `number`) values('Cork','20450');
insert into bikes(scheme_name, `number`) values('Cork','20451');
insert into bikes(scheme_name, `number`) values('Cork','20452');
insert into bikes(scheme_name, `number`) values('Cork','20453');
insert into bikes(scheme_name, `number`) values('Cork','20454');
insert into bikes(scheme_name, `number`) values('Cork','20455');
insert into bikes(scheme_name, `number`) values('Cork','20456');
insert into bikes(scheme_name, `number`) values('Cork','20458');
insert into bikes(scheme_name, `number`) values('Cork','20459');
insert into bikes(scheme_name, `number`) values('Cork','20461');
insert into bikes(scheme_name, `number`) values('Cork','20462');
insert into bikes(scheme_name, `number`) values('Cork','20463');
insert into bikes(scheme_name, `number`) values('Cork','20464');
insert into bikes(scheme_name, `number`) values('Cork','20465');
insert into bikes(scheme_name, `number`) values('Cork','20466');
insert into bikes(scheme_name, `number`) values('Cork','20467');
insert into bikes(scheme_name, `number`) values('Cork','20468');
insert into bikes(scheme_name, `number`) values('Cork','20469');
insert into bikes(scheme_name, `number`) values('Cork','20470');
insert into bikes(scheme_name, `number`) values('Cork','20471');
insert into bikes(scheme_name, `number`) values('Cork','20472');
insert into bikes(scheme_name, `number`) values('Cork','20473');
insert into bikes(scheme_name, `number`) values('Cork','20474');
insert into bikes(scheme_name, `number`) values('Cork','20475');
insert into bikes(scheme_name, `number`) values('Cork','20476');
insert into bikes(scheme_name, `number`) values('Cork','20477');
insert into bikes(scheme_name, `number`) values('Cork','20478');
insert into bikes(scheme_name, `number`) values('Cork','20480');
insert into bikes(scheme_name, `number`) values('Cork','20481');
insert into bikes(scheme_name, `number`) values('Cork','20482');
insert into bikes(scheme_name, `number`) values('Cork','20483');
insert into bikes(scheme_name, `number`) values('Cork','20484');
insert into bikes(scheme_name, `number`) values('Cork','20485');
insert into bikes(scheme_name, `number`) values('Cork','20486');
insert into bikes(scheme_name, `number`) values('Cork','20487');
insert into bikes(scheme_name, `number`) values('Cork','20488');
insert into bikes(scheme_name, `number`) values('Cork','20489');
insert into bikes(scheme_name, `number`) values('Cork','20490');
insert into bikes(scheme_name, `number`) values('Cork','20491');
insert into bikes(scheme_name, `number`) values('Cork','20492');
insert into bikes(scheme_name, `number`) values('Cork','20493');
insert into bikes(scheme_name, `number`) values('Cork','20494');
insert into bikes(scheme_name, `number`) values('Cork','20495');
insert into bikes(scheme_name, `number`) values('Cork','20496');
insert into bikes(scheme_name, `number`) values('Cork','20497');
insert into bikes(scheme_name, `number`) values('Cork','20498');
insert into bikes(scheme_name, `number`) values('Cork','20499');
insert into bikes(scheme_name, `number`) values('Cork','20500');
insert into bikes(scheme_name, `number`) values('Cork','20501');
insert into bikes(scheme_name, `number`) values('Cork','20502');
insert into bikes(scheme_name, `number`) values('Cork','20503');
insert into bikes(scheme_name, `number`) values('Cork','20504');
insert into bikes(scheme_name, `number`) values('Cork','20505');
insert into bikes(scheme_name, `number`) values('Cork','20506');
insert into bikes(scheme_name, `number`) values('Cork','20507');
insert into bikes(scheme_name, `number`) values('Cork','20508');
insert into bikes(scheme_name, `number`) values('Cork','20509');
insert into bikes(scheme_name, `number`) values('Cork','20510');
insert into bikes(scheme_name, `number`) values('Cork','20511');
insert into bikes(scheme_name, `number`) values('Cork','20512');
insert into bikes(scheme_name, `number`) values('Cork','20513');
insert into bikes(scheme_name, `number`) values('Cork','20514');
insert into bikes(scheme_name, `number`) values('Cork','20515');
insert into bikes(scheme_name, `number`) values('Cork','20516');
insert into bikes(scheme_name, `number`) values('Cork','20517');
insert into bikes(scheme_name, `number`) values('Cork','20518');
insert into bikes(scheme_name, `number`) values('Cork','20519');
insert into bikes(scheme_name, `number`) values('Cork','20520');
insert into bikes(scheme_name, `number`) values('Cork','20522');
insert into bikes(scheme_name, `number`) values('Cork','20523');
insert into bikes(scheme_name, `number`) values('Cork','20524');
insert into bikes(scheme_name, `number`) values('Cork','20525');
insert into bikes(scheme_name, `number`) values('Cork','20526');
insert into bikes(scheme_name, `number`) values('Cork','20527');
insert into bikes(scheme_name, `number`) values('Cork','20528');
insert into bikes(scheme_name, `number`) values('Cork','20529');
insert into bikes(scheme_name, `number`) values('Cork','20530');
insert into bikes(scheme_name, `number`) values('Cork','20531');
insert into bikes(scheme_name, `number`) values('Cork','20533');
insert into bikes(scheme_name, `number`) values('Cork','20534');
insert into bikes(scheme_name, `number`) values('Cork','20535');
insert into bikes(scheme_name, `number`) values('Cork','20536');
insert into bikes(scheme_name, `number`) values('Cork','20537');
insert into bikes(scheme_name, `number`) values('Cork','20538');
insert into bikes(scheme_name, `number`) values('Cork','20539');
insert into bikes(scheme_name, `number`) values('Cork','20540');
insert into bikes(scheme_name, `number`) values('Cork','20541');
insert into bikes(scheme_name, `number`) values('Cork','20543');
insert into bikes(scheme_name, `number`) values('Cork','20544');
insert into bikes(scheme_name, `number`) values('Cork','20545');
insert into bikes(scheme_name, `number`) values('Cork','20546');
insert into bikes(scheme_name, `number`) values('Cork','20547');
insert into bikes(scheme_name, `number`) values('Cork','20548');
insert into bikes(scheme_name, `number`) values('Cork','20550');
insert into bikes(scheme_name, `number`) values('Cork','20552');
insert into bikes(scheme_name, `number`) values('Cork','20553');
insert into bikes(scheme_name, `number`) values('Cork','20554');
insert into bikes(scheme_name, `number`) values('Cork','20556');
insert into bikes(scheme_name, `number`) values('Cork','20558');
insert into bikes(scheme_name, `number`) values('Cork','20559');
insert into bikes(scheme_name, `number`) values('Cork','20560');
insert into bikes(scheme_name, `number`) values('Cork','20562');
insert into bikes(scheme_name, `number`) values('Cork','20565');
insert into bikes(scheme_name, `number`) values('Cork','20566');
insert into bikes(scheme_name, `number`) values('Cork','20567');
insert into bikes(scheme_name, `number`) values('Cork','20568');
insert into bikes(scheme_name, `number`) values('Cork','20569');
insert into bikes(scheme_name, `number`) values('Cork','20570');
insert into bikes(scheme_name, `number`) values('Cork','20571');
insert into bikes(scheme_name, `number`) values('Cork','20572');
insert into bikes(scheme_name, `number`) values('Cork','20573');
insert into bikes(scheme_name, `number`) values('Cork','20574');
insert into bikes(scheme_name, `number`) values('Cork','20576');
insert into bikes(scheme_name, `number`) values('Cork','20577');
insert into bikes(scheme_name, `number`) values('Cork','20581');
insert into bikes(scheme_name, `number`) values('Cork','20582');
insert into bikes(scheme_name, `number`) values('Cork','20584');
insert into bikes(scheme_name, `number`) values('Cork','20585');
insert into bikes(scheme_name, `number`) values('Cork','20587');
insert into bikes(scheme_name, `number`) values('Cork','20592');
insert into bikes(scheme_name, `number`) values('Cork','20593');
insert into bikes(scheme_name, `number`) values('Cork','20594');
insert into bikes(scheme_name, `number`) values('Cork','20595');
insert into bikes(scheme_name, `number`) values('Cork','20596');
insert into bikes(scheme_name, `number`) values('Cork','20597');
insert into bikes(scheme_name, `number`) values('Cork','20598');
insert into bikes(scheme_name, `number`) values('Cork','20599');
insert into bikes(scheme_name, `number`) values('Cork','20601');
insert into bikes(scheme_name, `number`) values('Cork','20602');
insert into bikes(scheme_name, `number`) values('Cork','20603');
insert into bikes(scheme_name, `number`) values('Cork','20605');
insert into bikes(scheme_name, `number`) values('Cork','20606');
insert into bikes(scheme_name, `number`) values('Cork','20607');
insert into bikes(scheme_name, `number`) values('Cork','20608');
insert into bikes(scheme_name, `number`) values('Cork','20609');
insert into bikes(scheme_name, `number`) values('Cork','20610');
insert into bikes(scheme_name, `number`) values('Cork','20611');
insert into bikes(scheme_name, `number`) values('Cork','20613');
insert into bikes(scheme_name, `number`) values('Cork','20616');
insert into bikes(scheme_name, `number`) values('Cork','20617');
insert into bikes(scheme_name, `number`) values('Cork','20623');
insert into bikes(scheme_name, `number`) values('Cork','20624');
insert into bikes(scheme_name, `number`) values('Cork','20625');
insert into bikes(scheme_name, `number`) values('Cork','20626');
insert into bikes(scheme_name, `number`) values('Cork','20627');
insert into bikes(scheme_name, `number`) values('Cork','20628');
insert into bikes(scheme_name, `number`) values('Cork','20629');
insert into bikes(scheme_name, `number`) values('Cork','20630');
insert into bikes(scheme_name, `number`) values('Cork','20631');
insert into bikes(scheme_name, `number`) values('Cork','20632');
insert into bikes(scheme_name, `number`) values('Cork','20633');
insert into bikes(scheme_name, `number`) values('Cork','20634');
insert into bikes(scheme_name, `number`) values('Cork','20635');
insert into bikes(scheme_name, `number`) values('Cork','20636');
insert into bikes(scheme_name, `number`) values('Cork','20637');
insert into bikes(scheme_name, `number`) values('Cork','20638');
insert into bikes(scheme_name, `number`) values('Cork','20639');
insert into bikes(scheme_name, `number`) values('Cork','20640');
insert into bikes(scheme_name, `number`) values('Cork','20641');
insert into bikes(scheme_name, `number`) values('Cork','20642');
insert into bikes(scheme_name, `number`) values('Cork','20643');
insert into bikes(scheme_name, `number`) values('Cork','20644');
insert into bikes(scheme_name, `number`) values('Cork','20645');
insert into bikes(scheme_name, `number`) values('Cork','20646');
insert into bikes(scheme_name, `number`) values('Cork','20648');
insert into bikes(scheme_name, `number`) values('Cork','20649');
insert into bikes(scheme_name, `number`) values('Cork','20651');
insert into bikes(scheme_name, `number`) values('Cork','20653');
insert into bikes(scheme_name, `number`) values('Cork','20655');
insert into bikes(scheme_name, `number`) values('Cork','20658');
insert into bikes(scheme_name, `number`) values('Cork','20659');
insert into bikes(scheme_name, `number`) values('Cork','20660');
insert into bikes(scheme_name, `number`) values('Cork','20661');
insert into bikes(scheme_name, `number`) values('Cork','20662');
insert into bikes(scheme_name, `number`) values('Cork','20663');
insert into bikes(scheme_name, `number`) values('Cork','20664');
insert into bikes(scheme_name, `number`) values('Cork','20665');
insert into bikes(scheme_name, `number`) values('Cork','20666');
insert into bikes(scheme_name, `number`) values('Cork','20667');
insert into bikes(scheme_name, `number`) values('Cork','20668');
insert into bikes(scheme_name, `number`) values('Cork','20669');
insert into bikes(scheme_name, `number`) values('Cork','20670');
insert into bikes(scheme_name, `number`) values('Cork','20671');
insert into bikes(scheme_name, `number`) values('Cork','20672');
insert into bikes(scheme_name, `number`) values('Cork','20673');
insert into bikes(scheme_name, `number`) values('Cork','20674');
insert into bikes(scheme_name, `number`) values('Cork','20675');
insert into bikes(scheme_name, `number`) values('Cork','20676');
insert into bikes(scheme_name, `number`) values('Cork','20677');
insert into bikes(scheme_name, `number`) values('Cork','20678');
insert into bikes(scheme_name, `number`) values('Cork','20679');
insert into bikes(scheme_name, `number`) values('Cork','20680');
insert into bikes(scheme_name, `number`) values('Cork','20681');
insert into bikes(scheme_name, `number`) values('Cork','20682');
insert into bikes(scheme_name, `number`) values('Cork','20683');
insert into bikes(scheme_name, `number`) values('Cork','20684');
insert into bikes(scheme_name, `number`) values('Cork','20685');
insert into bikes(scheme_name, `number`) values('Cork','20686');
insert into bikes(scheme_name, `number`) values('Cork','20688');
insert into bikes(scheme_name, `number`) values('Cork','20690');
insert into bikes(scheme_name, `number`) values('Cork','20691');
insert into bikes(scheme_name, `number`) values('Cork','20692');
insert into bikes(scheme_name, `number`) values('Cork','20694');
insert into bikes(scheme_name, `number`) values('Cork','20695');
insert into bikes(scheme_name, `number`) values('Cork','20696');
insert into bikes(scheme_name, `number`) values('Cork','20697');
insert into bikes(scheme_name, `number`) values('Cork','20698');
insert into bikes(scheme_name, `number`) values('Cork','20699');
insert into bikes(scheme_name, `number`) values('Cork','20700');
insert into bikes(scheme_name, `number`) values('Cork','20701');
insert into bikes(scheme_name, `number`) values('Cork','20702');
insert into bikes(scheme_name, `number`) values('Cork','20703');
insert into bikes(scheme_name, `number`) values('Cork','20704');
insert into bikes(scheme_name, `number`) values('Cork','20705');
insert into bikes(scheme_name, `number`) values('Cork','20706');
insert into bikes(scheme_name, `number`) values('Cork','20710');
insert into bikes(scheme_name, `number`) values('Cork','20715');
insert into bikes(scheme_name, `number`) values('Cork','20716');
insert into bikes(scheme_name, `number`) values('Cork','20717');
insert into bikes(scheme_name, `number`) values('Cork','20719');
insert into bikes(scheme_name, `number`) values('Cork','20720');
insert into bikes(scheme_name, `number`) values('Cork','20721');
insert into bikes(scheme_name, `number`) values('Cork','20722');
insert into bikes(scheme_name, `number`) values('Cork','20723');
insert into bikes(scheme_name, `number`) values('Cork','20724');
insert into bikes(scheme_name, `number`) values('Cork','20725');
insert into bikes(scheme_name, `number`) values('Cork','20726');
insert into bikes(scheme_name, `number`) values('Cork','20727');
insert into bikes(scheme_name, `number`) values('Cork','20728');
insert into bikes(scheme_name, `number`) values('Cork','20729');
insert into bikes(scheme_name, `number`) values('Cork','20731');
insert into bikes(scheme_name, `number`) values('Cork','20732');
insert into bikes(scheme_name, `number`) values('Cork','20733');
insert into bikes(scheme_name, `number`) values('Cork','20734');
insert into bikes(scheme_name, `number`) values('Cork','20735');
insert into bikes(scheme_name, `number`) values('Cork','20736');
insert into bikes(scheme_name, `number`) values('Cork','20738');
insert into bikes(scheme_name, `number`) values('Cork','20739');
insert into bikes(scheme_name, `number`) values('Cork','20741');
insert into bikes(scheme_name, `number`) values('Cork','20742');
insert into bikes(scheme_name, `number`) values('Cork','20743');
insert into bikes(scheme_name, `number`) values('Cork','20744');
insert into bikes(scheme_name, `number`) values('Cork','20745');
insert into bikes(scheme_name, `number`) values('Cork','20746');
insert into bikes(scheme_name, `number`) values('Cork','20747');
insert into bikes(scheme_name, `number`) values('Cork','20748');
insert into bikes(scheme_name, `number`) values('Cork','20749');
insert into bikes(scheme_name, `number`) values('Cork','20750');
insert into bikes(scheme_name, `number`) values('Cork','20751');
insert into bikes(scheme_name, `number`) values('Cork','20752');
insert into bikes(scheme_name, `number`) values('Cork','20753');
insert into bikes(scheme_name, `number`) values('Cork','20754');
insert into bikes(scheme_name, `number`) values('Cork','20755');
insert into bikes(scheme_name, `number`) values('Cork','20756');
insert into bikes(scheme_name, `number`) values('Cork','20757');
insert into bikes(scheme_name, `number`) values('Cork','20758');
insert into bikes(scheme_name, `number`) values('Cork','20759');
insert into bikes(scheme_name, `number`) values('Cork','20760');
insert into bikes(scheme_name, `number`) values('Cork','20761');
insert into bikes(scheme_name, `number`) values('Cork','20762');
insert into bikes(scheme_name, `number`) values('Cork','20763');
insert into bikes(scheme_name, `number`) values('Cork','20764');
insert into bikes(scheme_name, `number`) values('Cork','20765');
insert into bikes(scheme_name, `number`) values('Cork','20766');
insert into bikes(scheme_name, `number`) values('Cork','20769');
insert into bikes(scheme_name, `number`) values('Cork','20770');
insert into bikes(scheme_name, `number`) values('Cork','20771');
insert into bikes(scheme_name, `number`) values('Cork','20772');
insert into bikes(scheme_name, `number`) values('Cork','20773');
insert into bikes(scheme_name, `number`) values('Cork','20774');
insert into bikes(scheme_name, `number`) values('Cork','20775');
insert into bikes(scheme_name, `number`) values('Cork','20776');
insert into bikes(scheme_name, `number`) values('Cork','20777');
insert into bikes(scheme_name, `number`) values('Cork','20778');
insert into bikes(scheme_name, `number`) values('Cork','20779');
insert into bikes(scheme_name, `number`) values('Cork','20780');
insert into bikes(scheme_name, `number`) values('Cork','20781');
insert into bikes(scheme_name, `number`) values('Cork','20783');
insert into bikes(scheme_name, `number`) values('Cork','20784');
insert into bikes(scheme_name, `number`) values('Cork','20785');
insert into bikes(scheme_name, `number`) values('Cork','20786');
insert into bikes(scheme_name, `number`) values('Cork','20787');
insert into bikes(scheme_name, `number`) values('Cork','20788');
insert into bikes(scheme_name, `number`) values('Cork','20789');
insert into bikes(scheme_name, `number`) values('Cork','20790');
insert into bikes(scheme_name, `number`) values('Cork','20791');
insert into bikes(scheme_name, `number`) values('Cork','20792');
insert into bikes(scheme_name, `number`) values('Cork','20793');
insert into bikes(scheme_name, `number`) values('Cork','20794');
insert into bikes(scheme_name, `number`) values('Cork','20795');
insert into bikes(scheme_name, `number`) values('Cork','20796');
insert into bikes(scheme_name, `number`) values('Cork','20797');
insert into bikes(scheme_name, `number`) values('Cork','20798');
insert into bikes(scheme_name, `number`) values('Cork','20799');
insert into bikes(scheme_name, `number`) values('Cork','20800');
insert into bikes(scheme_name, `number`) values('Cork','20999');
insert into bikes(scheme_name, `number`) values('Limerick','30151');
insert into bikes(scheme_name, `number`) values('Limerick','30152');
insert into bikes(scheme_name, `number`) values('Limerick','30153');
insert into bikes(scheme_name, `number`) values('Limerick','30154');
insert into bikes(scheme_name, `number`) values('Limerick','30155');
insert into bikes(scheme_name, `number`) values('Limerick','30156');
insert into bikes(scheme_name, `number`) values('Limerick','30157');
insert into bikes(scheme_name, `number`) values('Limerick','30158');
insert into bikes(scheme_name, `number`) values('Limerick','30159');
insert into bikes(scheme_name, `number`) values('Limerick','30160');
insert into bikes(scheme_name, `number`) values('Limerick','30161');
insert into bikes(scheme_name, `number`) values('Limerick','30162');
insert into bikes(scheme_name, `number`) values('Limerick','30163');
insert into bikes(scheme_name, `number`) values('Limerick','30164');
insert into bikes(scheme_name, `number`) values('Limerick','30165');
insert into bikes(scheme_name, `number`) values('Limerick','30166');
insert into bikes(scheme_name, `number`) values('Limerick','30167');
insert into bikes(scheme_name, `number`) values('Limerick','30168');
insert into bikes(scheme_name, `number`) values('Limerick','30169');
insert into bikes(scheme_name, `number`) values('Limerick','30170');
insert into bikes(scheme_name, `number`) values('Limerick','30171');
insert into bikes(scheme_name, `number`) values('Limerick','30172');
insert into bikes(scheme_name, `number`) values('Limerick','30173');
insert into bikes(scheme_name, `number`) values('Limerick','30174');
insert into bikes(scheme_name, `number`) values('Limerick','30175');
insert into bikes(scheme_name, `number`) values('Limerick','30176');
insert into bikes(scheme_name, `number`) values('Limerick','30177');
insert into bikes(scheme_name, `number`) values('Limerick','30179');
insert into bikes(scheme_name, `number`) values('Limerick','30180');
insert into bikes(scheme_name, `number`) values('Limerick','30181');
insert into bikes(scheme_name, `number`) values('Limerick','30182');
insert into bikes(scheme_name, `number`) values('Limerick','30183');
insert into bikes(scheme_name, `number`) values('Limerick','30184');
insert into bikes(scheme_name, `number`) values('Limerick','30185');
insert into bikes(scheme_name, `number`) values('Limerick','30186');
insert into bikes(scheme_name, `number`) values('Limerick','30187');
insert into bikes(scheme_name, `number`) values('Limerick','30188');
insert into bikes(scheme_name, `number`) values('Limerick','30189');
insert into bikes(scheme_name, `number`) values('Limerick','30190');
insert into bikes(scheme_name, `number`) values('Limerick','30191');
insert into bikes(scheme_name, `number`) values('Limerick','30192');
insert into bikes(scheme_name, `number`) values('Limerick','30193');
insert into bikes(scheme_name, `number`) values('Limerick','30194');
insert into bikes(scheme_name, `number`) values('Limerick','30195');
insert into bikes(scheme_name, `number`) values('Limerick','30196');
insert into bikes(scheme_name, `number`) values('Limerick','30197');
insert into bikes(scheme_name, `number`) values('Limerick','30198');
insert into bikes(scheme_name, `number`) values('Limerick','30199');
insert into bikes(scheme_name, `number`) values('Limerick','30200');
insert into bikes(scheme_name, `number`) values('Limerick','30201');
insert into bikes(scheme_name, `number`) values('Limerick','30202');
insert into bikes(scheme_name, `number`) values('Limerick','30203');
insert into bikes(scheme_name, `number`) values('Limerick','30204');
insert into bikes(scheme_name, `number`) values('Limerick','30205');
insert into bikes(scheme_name, `number`) values('Limerick','30206');
insert into bikes(scheme_name, `number`) values('Limerick','30207');
insert into bikes(scheme_name, `number`) values('Limerick','30208');
insert into bikes(scheme_name, `number`) values('Limerick','30209');
insert into bikes(scheme_name, `number`) values('Limerick','30210');
insert into bikes(scheme_name, `number`) values('Limerick','30211');
insert into bikes(scheme_name, `number`) values('Limerick','30212');
insert into bikes(scheme_name, `number`) values('Limerick','30213');
insert into bikes(scheme_name, `number`) values('Limerick','30214');
insert into bikes(scheme_name, `number`) values('Limerick','30215');
insert into bikes(scheme_name, `number`) values('Limerick','30216');
insert into bikes(scheme_name, `number`) values('Limerick','30217');
insert into bikes(scheme_name, `number`) values('Limerick','30218');
insert into bikes(scheme_name, `number`) values('Limerick','30219');
insert into bikes(scheme_name, `number`) values('Limerick','30220');
insert into bikes(scheme_name, `number`) values('Limerick','30221');
insert into bikes(scheme_name, `number`) values('Limerick','30223');
insert into bikes(scheme_name, `number`) values('Limerick','30226');
insert into bikes(scheme_name, `number`) values('Limerick','30227');
insert into bikes(scheme_name, `number`) values('Limerick','30228');
insert into bikes(scheme_name, `number`) values('Limerick','30230');
insert into bikes(scheme_name, `number`) values('Limerick','30231');
insert into bikes(scheme_name, `number`) values('Limerick','30232');
insert into bikes(scheme_name, `number`) values('Limerick','30234');
insert into bikes(scheme_name, `number`) values('Limerick','30235');
insert into bikes(scheme_name, `number`) values('Limerick','30236');
insert into bikes(scheme_name, `number`) values('Limerick','30239');
insert into bikes(scheme_name, `number`) values('Limerick','30241');
insert into bikes(scheme_name, `number`) values('Limerick','30242');
insert into bikes(scheme_name, `number`) values('Limerick','30243');
insert into bikes(scheme_name, `number`) values('Limerick','30244');
insert into bikes(scheme_name, `number`) values('Limerick','30245');
insert into bikes(scheme_name, `number`) values('Limerick','30246');
insert into bikes(scheme_name, `number`) values('Limerick','30247');
insert into bikes(scheme_name, `number`) values('Limerick','30249');
insert into bikes(scheme_name, `number`) values('Limerick','30251');
insert into bikes(scheme_name, `number`) values('Limerick','30252');
insert into bikes(scheme_name, `number`) values('Limerick','30253');
insert into bikes(scheme_name, `number`) values('Limerick','30255');
insert into bikes(scheme_name, `number`) values('Limerick','30257');
insert into bikes(scheme_name, `number`) values('Limerick','30258');
insert into bikes(scheme_name, `number`) values('Limerick','30259');
insert into bikes(scheme_name, `number`) values('Limerick','30260');
insert into bikes(scheme_name, `number`) values('Limerick','30261');
insert into bikes(scheme_name, `number`) values('Limerick','30262');
insert into bikes(scheme_name, `number`) values('Limerick','30263');
insert into bikes(scheme_name, `number`) values('Limerick','30264');
insert into bikes(scheme_name, `number`) values('Limerick','30265');
insert into bikes(scheme_name, `number`) values('Limerick','30267');
insert into bikes(scheme_name, `number`) values('Limerick','30268');
insert into bikes(scheme_name, `number`) values('Limerick','30269');
insert into bikes(scheme_name, `number`) values('Limerick','30270');
insert into bikes(scheme_name, `number`) values('Limerick','30271');
insert into bikes(scheme_name, `number`) values('Limerick','30272');
insert into bikes(scheme_name, `number`) values('Limerick','30273');
insert into bikes(scheme_name, `number`) values('Limerick','30274');
insert into bikes(scheme_name, `number`) values('Limerick','30275');
insert into bikes(scheme_name, `number`) values('Limerick','30276');
insert into bikes(scheme_name, `number`) values('Limerick','30277');
insert into bikes(scheme_name, `number`) values('Limerick','30278');
insert into bikes(scheme_name, `number`) values('Limerick','30279');
insert into bikes(scheme_name, `number`) values('Limerick','30280');
insert into bikes(scheme_name, `number`) values('Limerick','30282');
insert into bikes(scheme_name, `number`) values('Limerick','30284');
insert into bikes(scheme_name, `number`) values('Limerick','30285');
insert into bikes(scheme_name, `number`) values('Limerick','30286');
insert into bikes(scheme_name, `number`) values('Limerick','30287');
insert into bikes(scheme_name, `number`) values('Limerick','30288');
insert into bikes(scheme_name, `number`) values('Limerick','30289');
insert into bikes(scheme_name, `number`) values('Limerick','30290');
insert into bikes(scheme_name, `number`) values('Limerick','30291');
insert into bikes(scheme_name, `number`) values('Limerick','30292');
insert into bikes(scheme_name, `number`) values('Limerick','30293');
insert into bikes(scheme_name, `number`) values('Limerick','30295');
insert into bikes(scheme_name, `number`) values('Limerick','30296');
insert into bikes(scheme_name, `number`) values('Limerick','30297');
insert into bikes(scheme_name, `number`) values('Limerick','30298');
insert into bikes(scheme_name, `number`) values('Limerick','30299');
insert into bikes(scheme_name, `number`) values('Limerick','30301');
insert into bikes(scheme_name, `number`) values('Limerick','30302');
insert into bikes(scheme_name, `number`) values('Limerick','30303');
insert into bikes(scheme_name, `number`) values('Limerick','30304');
insert into bikes(scheme_name, `number`) values('Limerick','30305');
insert into bikes(scheme_name, `number`) values('Limerick','30307');
insert into bikes(scheme_name, `number`) values('Limerick','30308');
insert into bikes(scheme_name, `number`) values('Limerick','30310');
insert into bikes(scheme_name, `number`) values('Limerick','30311');
insert into bikes(scheme_name, `number`) values('Limerick','30313');
insert into bikes(scheme_name, `number`) values('Limerick','30315');
insert into bikes(scheme_name, `number`) values('Limerick','30316');
insert into bikes(scheme_name, `number`) values('Limerick','30319');
insert into bikes(scheme_name, `number`) values('Limerick','30321');
insert into bikes(scheme_name, `number`) values('Limerick','30322');
insert into bikes(scheme_name, `number`) values('Limerick','30323');
insert into bikes(scheme_name, `number`) values('Limerick','30324');
insert into bikes(scheme_name, `number`) values('Limerick','30325');
insert into bikes(scheme_name, `number`) values('Limerick','30326');
insert into bikes(scheme_name, `number`) values('Limerick','30327');
insert into bikes(scheme_name, `number`) values('Limerick','30328');
insert into bikes(scheme_name, `number`) values('Limerick','30330');
insert into bikes(scheme_name, `number`) values('Limerick','30331');
insert into bikes(scheme_name, `number`) values('Limerick','30332');
insert into bikes(scheme_name, `number`) values('Limerick','30333');
insert into bikes(scheme_name, `number`) values('Limerick','30334');
insert into bikes(scheme_name, `number`) values('Limerick','30335');
insert into bikes(scheme_name, `number`) values('Limerick','30336');
insert into bikes(scheme_name, `number`) values('Limerick','30337');
insert into bikes(scheme_name, `number`) values('Limerick','30338');
insert into bikes(scheme_name, `number`) values('Limerick','30340');
insert into bikes(scheme_name, `number`) values('Limerick','30341');
insert into bikes(scheme_name, `number`) values('Limerick','30342');
insert into bikes(scheme_name, `number`) values('Limerick','30343');
insert into bikes(scheme_name, `number`) values('Limerick','30344');
insert into bikes(scheme_name, `number`) values('Limerick','30346');
insert into bikes(scheme_name, `number`) values('Limerick','30347');
insert into bikes(scheme_name, `number`) values('Limerick','30348');
insert into bikes(scheme_name, `number`) values('Limerick','30349');
insert into bikes(scheme_name, `number`) values('Limerick','30350');
insert into bikes(scheme_name, `number`) values('Limerick','30351');
insert into bikes(scheme_name, `number`) values('Limerick','30352');
insert into bikes(scheme_name, `number`) values('Limerick','30353');
insert into bikes(scheme_name, `number`) values('Limerick','30354');
insert into bikes(scheme_name, `number`) values('Limerick','30356');
insert into bikes(scheme_name, `number`) values('Limerick','30357');
insert into bikes(scheme_name, `number`) values('Limerick','30361');
insert into bikes(scheme_name, `number`) values('Limerick','30362');
insert into bikes(scheme_name, `number`) values('Limerick','30363');
insert into bikes(scheme_name, `number`) values('Limerick','30364');
insert into bikes(scheme_name, `number`) values('Limerick','30365');
insert into bikes(scheme_name, `number`) values('Limerick','30367');
insert into bikes(scheme_name, `number`) values('Limerick','30368');
insert into bikes(scheme_name, `number`) values('Limerick','30370');
insert into bikes(scheme_name, `number`) values('Limerick','30372');
insert into bikes(scheme_name, `number`) values('Limerick','30373');
insert into bikes(scheme_name, `number`) values('Limerick','30374');
insert into bikes(scheme_name, `number`) values('Limerick','30377');
insert into bikes(scheme_name, `number`) values('Limerick','30378');
insert into bikes(scheme_name, `number`) values('Limerick','30379');
insert into bikes(scheme_name, `number`) values('Limerick','30380');
insert into bikes(scheme_name, `number`) values('Limerick','30384');
insert into bikes(scheme_name, `number`) values('Limerick','30385');
insert into bikes(scheme_name, `number`) values('Limerick','30386');
insert into bikes(scheme_name, `number`) values('Limerick','30387');
insert into bikes(scheme_name, `number`) values('Limerick','30388');
insert into bikes(scheme_name, `number`) values('Limerick','30389');
insert into bikes(scheme_name, `number`) values('Limerick','30390');
insert into bikes(scheme_name, `number`) values('Limerick','30391');
insert into bikes(scheme_name, `number`) values('Limerick','30392');
insert into bikes(scheme_name, `number`) values('Limerick','30393');
insert into bikes(scheme_name, `number`) values('Limerick','30394');
insert into bikes(scheme_name, `number`) values('Limerick','30395');
insert into bikes(scheme_name, `number`) values('Limerick','30396');
insert into bikes(scheme_name, `number`) values('Limerick','30397');
insert into bikes(scheme_name, `number`) values('Limerick','30398');
insert into bikes(scheme_name, `number`) values('Limerick','30399');
insert into bikes(scheme_name, `number`) values('Limerick','30400');
insert into bikes(scheme_name, `number`) values('Limerick','30403');
insert into bikes(scheme_name, `number`) values('Limerick','30404');
insert into bikes(scheme_name, `number`) values('Limerick','30405');
insert into bikes(scheme_name, `number`) values('Limerick','30406');
insert into bikes(scheme_name, `number`) values('Limerick','30407');
insert into bikes(scheme_name, `number`) values('Limerick','30408');
insert into bikes(scheme_name, `number`) values('Limerick','30409');
insert into bikes(scheme_name, `number`) values('Limerick','30410');
insert into bikes(scheme_name, `number`) values('Limerick','30411');
insert into bikes(scheme_name, `number`) values('Limerick','30412');
insert into bikes(scheme_name, `number`) values('Limerick','30413');
insert into bikes(scheme_name, `number`) values('Limerick','30416');
insert into bikes(scheme_name, `number`) values('Limerick','30417');
insert into bikes(scheme_name, `number`) values('Limerick','30419');
insert into bikes(scheme_name, `number`) values('Limerick','30420');
insert into bikes(scheme_name, `number`) values('Limerick','30422');
insert into bikes(scheme_name, `number`) values('Limerick','30423');
insert into bikes(scheme_name, `number`) values('Limerick','30425');
insert into bikes(scheme_name, `number`) values('Galway','40001');
insert into bikes(scheme_name, `number`) values('Galway','40002');
insert into bikes(scheme_name, `number`) values('Galway','40003');
insert into bikes(scheme_name, `number`) values('Galway','40004');
insert into bikes(scheme_name, `number`) values('Galway','40005');
insert into bikes(scheme_name, `number`) values('Galway','40006');
insert into bikes(scheme_name, `number`) values('Galway','40007');
insert into bikes(scheme_name, `number`) values('Galway','40008');
insert into bikes(scheme_name, `number`) values('Galway','40009');
insert into bikes(scheme_name, `number`) values('Galway','40010');
insert into bikes(scheme_name, `number`) values('Galway','40011');
insert into bikes(scheme_name, `number`) values('Galway','40012');
insert into bikes(scheme_name, `number`) values('Galway','40013');
insert into bikes(scheme_name, `number`) values('Galway','40014');
insert into bikes(scheme_name, `number`) values('Galway','40015');
insert into bikes(scheme_name, `number`) values('Galway','40016');
insert into bikes(scheme_name, `number`) values('Galway','40017');
insert into bikes(scheme_name, `number`) values('Galway','40018');
insert into bikes(scheme_name, `number`) values('Galway','40019');
insert into bikes(scheme_name, `number`) values('Galway','40020');
insert into bikes(scheme_name, `number`) values('Galway','40021');
insert into bikes(scheme_name, `number`) values('Galway','40022');
insert into bikes(scheme_name, `number`) values('Galway','40023');
insert into bikes(scheme_name, `number`) values('Galway','40024');
insert into bikes(scheme_name, `number`) values('Galway','40025');
insert into bikes(scheme_name, `number`) values('Galway','40026');
insert into bikes(scheme_name, `number`) values('Galway','40027');
insert into bikes(scheme_name, `number`) values('Galway','40028');
insert into bikes(scheme_name, `number`) values('Galway','40029');
insert into bikes(scheme_name, `number`) values('Galway','40030');
insert into bikes(scheme_name, `number`) values('Galway','40031');
insert into bikes(scheme_name, `number`) values('Galway','40032');
insert into bikes(scheme_name, `number`) values('Galway','40033');
insert into bikes(scheme_name, `number`) values('Galway','40034');
insert into bikes(scheme_name, `number`) values('Galway','40035');
insert into bikes(scheme_name, `number`) values('Galway','40036');
insert into bikes(scheme_name, `number`) values('Galway','40037');
insert into bikes(scheme_name, `number`) values('Galway','40038');
insert into bikes(scheme_name, `number`) values('Galway','40039');
insert into bikes(scheme_name, `number`) values('Galway','40040');
insert into bikes(scheme_name, `number`) values('Galway','40041');
insert into bikes(scheme_name, `number`) values('Galway','40042');
insert into bikes(scheme_name, `number`) values('Galway','40043');
insert into bikes(scheme_name, `number`) values('Galway','40044');
insert into bikes(scheme_name, `number`) values('Galway','40045');
insert into bikes(scheme_name, `number`) values('Galway','40046');
insert into bikes(scheme_name, `number`) values('Galway','40047');
insert into bikes(scheme_name, `number`) values('Galway','40048');
insert into bikes(scheme_name, `number`) values('Galway','40049');
insert into bikes(scheme_name, `number`) values('Galway','40050');
insert into bikes(scheme_name, `number`) values('Galway','40051');
insert into bikes(scheme_name, `number`) values('Galway','40052');
insert into bikes(scheme_name, `number`) values('Galway','40053');
insert into bikes(scheme_name, `number`) values('Galway','40054');
insert into bikes(scheme_name, `number`) values('Galway','40055');
insert into bikes(scheme_name, `number`) values('Galway','40056');
insert into bikes(scheme_name, `number`) values('Galway','40057');
insert into bikes(scheme_name, `number`) values('Galway','40058');
insert into bikes(scheme_name, `number`) values('Galway','40059');
insert into bikes(scheme_name, `number`) values('Galway','40060');
insert into bikes(scheme_name, `number`) values('Galway','40061');
insert into bikes(scheme_name, `number`) values('Galway','40062');
insert into bikes(scheme_name, `number`) values('Galway','40063');
insert into bikes(scheme_name, `number`) values('Galway','40064');
insert into bikes(scheme_name, `number`) values('Galway','40065');
insert into bikes(scheme_name, `number`) values('Galway','40066');
insert into bikes(scheme_name, `number`) values('Galway','40067');
insert into bikes(scheme_name, `number`) values('Galway','40068');
insert into bikes(scheme_name, `number`) values('Galway','40069');
insert into bikes(scheme_name, `number`) values('Galway','40070');
insert into bikes(scheme_name, `number`) values('Galway','40071');
insert into bikes(scheme_name, `number`) values('Galway','40072');
insert into bikes(scheme_name, `number`) values('Galway','40073');
insert into bikes(scheme_name, `number`) values('Galway','40074');
insert into bikes(scheme_name, `number`) values('Galway','40075');
insert into bikes(scheme_name, `number`) values('Galway','40076');
insert into bikes(scheme_name, `number`) values('Galway','40077');
insert into bikes(scheme_name, `number`) values('Galway','40078');
insert into bikes(scheme_name, `number`) values('Galway','40079');
insert into bikes(scheme_name, `number`) values('Galway','40080');
insert into bikes(scheme_name, `number`) values('Galway','40081');
insert into bikes(scheme_name, `number`) values('Galway','40082');
insert into bikes(scheme_name, `number`) values('Galway','40083');
insert into bikes(scheme_name, `number`) values('Galway','40084');
insert into bikes(scheme_name, `number`) values('Galway','40085');
insert into bikes(scheme_name, `number`) values('Galway','40086');
insert into bikes(scheme_name, `number`) values('Galway','40087');
insert into bikes(scheme_name, `number`) values('Galway','40088');
insert into bikes(scheme_name, `number`) values('Galway','40089');
insert into bikes(scheme_name, `number`) values('Galway','40090');
insert into bikes(scheme_name, `number`) values('Galway','40091');
insert into bikes(scheme_name, `number`) values('Galway','40092');
insert into bikes(scheme_name, `number`) values('Galway','40093');
insert into bikes(scheme_name, `number`) values('Galway','40094');
insert into bikes(scheme_name, `number`) values('Galway','40095');
insert into bikes(scheme_name, `number`) values('Galway','40096');
insert into bikes(scheme_name, `number`) values('Galway','40097');
insert into bikes(scheme_name, `number`) values('Galway','40098');
insert into bikes(scheme_name, `number`) values('Galway','40099');
insert into bikes(scheme_name, `number`) values('Galway','40100');
insert into bikes(scheme_name, `number`) values('Galway','40101');
insert into bikes(scheme_name, `number`) values('Galway','40102');
insert into bikes(scheme_name, `number`) values('Galway','40103');
insert into bikes(scheme_name, `number`) values('Galway','40104');
insert into bikes(scheme_name, `number`) values('Galway','40105');
insert into bikes(scheme_name, `number`) values('Galway','40106');
insert into bikes(scheme_name, `number`) values('Galway','40107');
insert into bikes(scheme_name, `number`) values('Galway','40108');
insert into bikes(scheme_name, `number`) values('Galway','40109');
insert into bikes(scheme_name, `number`) values('Galway','40110');
insert into bikes(scheme_name, `number`) values('Galway','40111');
insert into bikes(scheme_name, `number`) values('Galway','40112');
insert into bikes(scheme_name, `number`) values('Galway','40113');
insert into bikes(scheme_name, `number`) values('Galway','40114');
insert into bikes(scheme_name, `number`) values('Galway','40115');
insert into bikes(scheme_name, `number`) values('Galway','40116');
insert into bikes(scheme_name, `number`) values('Galway','40117');
insert into bikes(scheme_name, `number`) values('Galway','40118');
insert into bikes(scheme_name, `number`) values('Galway','40119');
insert into bikes(scheme_name, `number`) values('Galway','40120');
insert into bikes(scheme_name, `number`) values('Galway','40121');
insert into bikes(scheme_name, `number`) values('Galway','40122');
insert into bikes(scheme_name, `number`) values('Galway','40123');
insert into bikes(scheme_name, `number`) values('Galway','40124');
insert into bikes(scheme_name, `number`) values('Galway','40125');
insert into bikes(scheme_name, `number`) values('Galway','40126');
insert into bikes(scheme_name, `number`) values('Galway','40127');
insert into bikes(scheme_name, `number`) values('Galway','40128');
insert into bikes(scheme_name, `number`) values('Galway','40129');
insert into bikes(scheme_name, `number`) values('Galway','40130');
insert into bikes(scheme_name, `number`) values('Galway','40131');
insert into bikes(scheme_name, `number`) values('Galway','40132');
insert into bikes(scheme_name, `number`) values('Galway','40134');
insert into bikes(scheme_name, `number`) values('Galway','40135');
insert into bikes(scheme_name, `number`) values('Galway','40136');
insert into bikes(scheme_name, `number`) values('Galway','40137');
insert into bikes(scheme_name, `number`) values('Galway','40138');
insert into bikes(scheme_name, `number`) values('Galway','40139');
insert into bikes(scheme_name, `number`) values('Galway','40140');
insert into bikes(scheme_name, `number`) values('Galway','40141');
insert into bikes(scheme_name, `number`) values('Galway','40142');
insert into bikes(scheme_name, `number`) values('Galway','40143');
insert into bikes(scheme_name, `number`) values('Galway','40144');
insert into bikes(scheme_name, `number`) values('Galway','40145');
insert into bikes(scheme_name, `number`) values('Galway','40146');
insert into bikes(scheme_name, `number`) values('Galway','40147');
insert into bikes(scheme_name, `number`) values('Galway','40148');
insert into bikes(scheme_name, `number`) values('Galway','40149');
insert into bikes(scheme_name, `number`) values('Galway','40150');
insert into bikes(scheme_name, `number`) values('Galway','40155');
insert into bikes(scheme_name, `number`) values('Galway','40165');
insert into bikes(scheme_name, `number`) values('Galway','40169');
insert into bikes(scheme_name, `number`) values('Galway','40190');
insert into bikes(scheme_name, `number`) values('Galway','40192');
insert into bikes(scheme_name, `number`) values('Galway','40194');
insert into bikes(scheme_name, `number`) values('Galway','40203');
insert into bikes(scheme_name, `number`) values('Galway','40209');
insert into bikes(scheme_name, `number`) values('Galway','40217');
insert into bikes(scheme_name, `number`) values('Galway','40227');
insert into bikes(scheme_name, `number`) values('Galway','40228');
insert into bikes(scheme_name, `number`) values('Galway','40304');
insert into bikes(scheme_name, `number`) values('Galway','40307');
insert into bikes(scheme_name, `number`) values('Galway','40315');
insert into bikes(scheme_name, `number`) values('Galway','40324');
insert into bikes(scheme_name, `number`) values('Galway','40325');
insert into bikes(scheme_name, `number`) values('Galway','40331');
insert into bikes(scheme_name, `number`) values('Galway','40338');
insert into bikes(scheme_name, `number`) values('Galway','40343');
insert into bikes(scheme_name, `number`) values('Galway','40344');
insert into bikes(scheme_name, `number`) values('Galway','40346');
insert into bikes(scheme_name, `number`) values('Galway','40361');
insert into bikes(scheme_name, `number`) values('Galway','40389');
insert into bikes(scheme_name, `number`) values('Galway','40396');
insert into bikes(scheme_name, `number`) values('Galway','40400');
insert into bikes(scheme_name, `number`) values('Galway','40412');
insert into bikes(scheme_name, `number`) values('Galway','40445');
insert into bikes(scheme_name, `number`) values('Galway','40457');
insert into bikes(scheme_name, `number`) values('Galway','40460');
insert into bikes(scheme_name, `number`) values('Galway','40532');
insert into bikes(scheme_name, `number`) values('Galway','40555');
insert into bikes(scheme_name, `number`) values('Galway','40557');
insert into bikes(scheme_name, `number`) values('Galway','40564');
insert into bikes(scheme_name, `number`) values('Galway','40575');
insert into bikes(scheme_name, `number`) values('Galway','40589');
insert into bikes(scheme_name, `number`) values('Galway','40590');
insert into bikes(scheme_name, `number`) values('Galway','40604');
insert into bikes(scheme_name, `number`) values('Galway','40612');
insert into bikes(scheme_name, `number`) values('Galway','40614');
insert into bikes(scheme_name, `number`) values('Galway','40615');
insert into bikes(scheme_name, `number`) values('Galway','40619');
insert into bikes(scheme_name, `number`) values('Galway','40620');
insert into bikes(scheme_name, `number`) values('Galway','40650');
insert into bikes(scheme_name, `number`) values('Galway','40656');
insert into bikes(scheme_name, `number`) values('Galway','40657');
insert into bikes(scheme_name, `number`) values('Galway','40687');
insert into bikes(scheme_name, `number`) values('Galway','40689');
insert into bikes(scheme_name, `number`) values('Galway','40707');
insert into bikes(scheme_name, `number`) values('Galway','40708');
insert into bikes(scheme_name, `number`) values('Galway','40712');
insert into bikes(scheme_name, `number`) values('Galway','40737');
insert into bikes(scheme_name, `number`) values('Galway','40740');

create table if not exists report_reasons(
	id int auto_increment NOT NULL,
    reason varchar(255) NOT NULL,
    
    primary key(id)
);

insert into report_reasons (reason) values ("Wearout");
insert into report_reasons (reason) values ("Damage");
insert into report_reasons (reason) values ("Vandalism");

create table if not exists repair_reasons(
	id int auto_increment NOT NULL,
    reason varchar(255) NOT NULL,
    for_what int NOT NULL, -- 1 - station, 2 - bike
    
    primary key(id)
);


create table if not exists repair_reports(
	id int auto_increment NOT NULL,
    state_id int NOT NULL,
    location_id int NOT NULL,
    bike_id int NULL,
    report_reason_id int NOT NULL,
    station_itself tinyint(1),
    report_date datetime,
    repair_date datetime,
    on_street_operator varchar(100),
    on_depot_operator varchar(100),
    street_comments varchar(2000),
    depot_comments varchar(2000),
    routine_review bit,
    on_street_repair bit,
    geo_location varchar(500),
    bollard_numbers varchar(500) NULL,
    bollard_comments varchar(1000) NULL,
    collected_date datetime,
    
    primary key(id),
    
    foreign key (state_id)
    references states(id),
    
    foreign key(location_id)
    references bike_stations(id),
    
    foreign key(bike_id)
    references bikes(id),
    
    foreign key(report_reason_id)
    references report_reasons(id)
);

create table if not exists repair_reports_operators(
	id int not null auto_increment,
    report_id int not null,
    user_name varchar(200) not null,
    jobs_done varchar(5000),
    time_of_work_milis int not null,
    job_done_datetime timestamp,
    
    primary key (id),
    
    foreign key(report_id)
    references repair_reports(id)
);

create table if not exists report_repair_reasons(
	repair_report_id int NOT NULL,
    repair_reason_id int NOT NULL,
    
    primary key (repair_report_id, repair_reason_id),
    
    foreign key(repair_report_id)
    references repair_reports(id),
    
    foreign key(repair_reason_id)
    references repair_reasons(id)
);


create table if not exists repair_jobs(
	id int auto_increment NOT NULL,
    job varchar(1000) NOT NULL,
    for_what int NOT NULL, -- 1 - station, 2 - bike
    
    primary key (id)
);

create table if not exists report_used_assets(
	report_operator_id int NOT NULL,
    product_type_id int NOT NULL,
    amount int NOT NULL,
    
    primary key (report_operator_id, product_type_id),
    
    foreign key(report_operator_id)
    references repair_reports_operators(id),
    
    FOREIGN KEY (product_type_id)
    REFERENCES types_of_assets(`id`)
);


create table if not exists routine_review(
	id int not null auto_increment,
    creation_date datetime NOT NULL,
    operator varchar(255) NOT NULL,
    station_id int,
    reports varchar(2000),
    bollards_total int(11) NULL,
    bikes_at_station int(11) NULL,
    graffiti bit(1),
    weeds bit(1),
    bollards_inactive varchar(5000),
    
    
    primary key (id),
    
    foreign key (station_id)
    references bike_stations(id)
    
);


create table if not exists routine_review_bikes_checked(
	review_id int NOT NULL,
    bike_id int NOT NULL,
    
    primary key (review_id, bike_id),
    
    foreign key (review_id)
    references routine_review(id),
    
    foreign key (bike_id)
    references bikes(id)
);


create table if not exists system_parameters(
	parameter_name varchar(100) NOT NULL,
    parameter_value varchar(1500),
    
    primary key (parameter_name)
);

insert into system_parameters values ("email_host", "mail.blacknight.com");
insert into system_parameters values ("email_port", "587");
insert into system_parameters values ("email_username", "alerts@stock.telclic.net");
insert into system_parameters values ("email_password", "jc5td4c_Fz");
insert into system_parameters values ("email_starttls", "false");
insert into system_parameters values ("domain_link", "https://sandbox.telclic.net");

insert into system_parameters values ("pdf_text", "");


