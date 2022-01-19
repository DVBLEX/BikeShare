use msc;

DROP TABLE IF EXISTS `bikes_location_on-street`;
CREATE TABLE `bikes_location_on-street`
(
    `id`                int          NOT NULL AUTO_INCREMENT,
    `scheme_name`       varchar(100) NOT NULL,
    `bike_id`           int          NOT NULL,
    `manually_selected` TINYINT(1)   NOT NULL,
    `reason`            varchar(100) NOT NULL,
    `last_in_storage`   datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `bike_id_uk` (`bike_id` ASC),
    INDEX `bike_id_ik` (`bike_id` ASC),
    INDEX `last_in_storage_ik` (`last_in_storage` ASC)

);


DROP TABLE IF EXISTS `bikes_location_in_depot`;
CREATE TABLE `bikes_location_in_depot`
(
    `id`                int          NOT NULL AUTO_INCREMENT,
    `scheme_name`       varchar(100) NOT NULL,
    `bike_id`           int          NOT NULL,
    `manually_selected` TINYINT(1)   NOT NULL,
    `reason`            varchar(100) NOT NULL,
    `in_storage_since`  datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `bike_id_uk` (`bike_id` ASC),
    INDEX `bike_id_ik` (`bike_id` ASC),
    INDEX `in_storage_since_ik` (`in_storage_since` ASC)
);

