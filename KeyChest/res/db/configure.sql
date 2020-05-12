DROP DATABASE IF EXISTS `keychest`;
CREATE DATABASE `keychest`;
USE `keychest`;

CREATE USER `felipe` IDENTIFIED BY 'xxxx';
GRANT ALL PRIVILEGES ON keychest.* TO 'felipe'@'%';

CREATE TABLE `owner` (
  `owner_id_pk` int(11) NOT NULL AUTO_INCREMENT,
  `owner_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`owner_id_pk`),
  UNIQUE KEY `owner_name` (`owner_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `config` (
  `cfg_owner_id` int(11) NOT NULL,
  PRIMARY KEY (`cfg_owner_id`),
  KEY `cfg_owner_id_fk` (`cfg_owner_id`),
  CONSTRAINT `cfg_owner_id_fk` FOREIGN KEY (`cfg_owner_id`) REFERENCES `owner` (`owner_id_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `credentials` (
  `cred_id_pk` int(11) NOT NULL AUTO_INCREMENT,
  `cred_service` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cred_login` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cred_password` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cred_owner_id` int(11) NOT NULL,
  `cred_created_time` timestamp NULL DEFAULT current_timestamp(),
  `cred_last_updated` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`cred_id_pk`),
  KEY `cred_owner_id_fk` (`cred_owner_id`),
  CONSTRAINT `cred_owner_id_fk` FOREIGN KEY (`cred_owner_id`) REFERENCES `owner` (`owner_id_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
