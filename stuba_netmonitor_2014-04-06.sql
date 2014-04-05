# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 10.0.2.10 (MySQL 5.5.35-0+wheezy1-log)
# Database: stuba_netmonitor
# Generation Time: 2014-04-05 23:15:22 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table nm_device
# ------------------------------------------------------------

CREATE TABLE `nm_device` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `device_name` varchar(255) NOT NULL DEFAULT '',
  `device_description` text,
  `ipv4_address` varchar(15) NOT NULL,
  `modified_date` timestamp NULL DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_interface
# ------------------------------------------------------------

CREATE TABLE `nm_interface` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `probe_id` int(11) unsigned NOT NULL,
  `interface_index` int(11) NOT NULL,
  `interface_name` varchar(255) NOT NULL DEFAULT '',
  `interface_type` varchar(255) DEFAULT NULL,
  `last_update_date` timestamp NULL DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `probe_id` (`probe_id`),
  CONSTRAINT `nm_interface_ibfk_1` FOREIGN KEY (`probe_id`) REFERENCES `nm_probe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_interface_aggregated_stats
# ------------------------------------------------------------

CREATE TABLE `nm_interface_aggregated_stats` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `probe_id` int(11) unsigned NOT NULL,
  `interface_count` int(11) NOT NULL,
  `rx_bytes` bigint(22) NOT NULL,
  `tx_bytes` bigint(22) NOT NULL,
  `rx_packets` bigint(22) NOT NULL,
  `tx_packets` bigint(22) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `probe_id` (`probe_id`),
  CONSTRAINT `nm_interface_aggregated_stats_ibfk_1` FOREIGN KEY (`probe_id`) REFERENCES `nm_probe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_interface_stats
# ------------------------------------------------------------

CREATE TABLE `nm_interface_stats` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `interface_id` int(11) unsigned NOT NULL,
  `interface_admin_status` int(1) NOT NULL DEFAULT '0',
  `interface_operational_status` int(1) NOT NULL DEFAULT '0',
  `interface_rx_bytes` bigint(22) NOT NULL DEFAULT '0',
  `interface_tx_bytes` bigint(22) NOT NULL DEFAULT '0',
  `interface_rx_packets` bigint(22) NOT NULL DEFAULT '0',
  `interface_tx_packets` bigint(22) NOT NULL DEFAULT '0',
  `interface_rx_discards_drops` bigint(22) NOT NULL DEFAULT '0',
  `interface_tx_discards_drops` bigint(22) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `interface_id` (`interface_id`),
  CONSTRAINT `nm_interface_stats_ibfk_1` FOREIGN KEY (`interface_id`) REFERENCES `nm_interface` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_probe
# ------------------------------------------------------------

CREATE TABLE `nm_probe` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `device_id` int(11) unsigned NOT NULL,
  `probe_type_id` int(11) unsigned NOT NULL,
  `probe_status` int(11) NOT NULL DEFAULT '0',
  `last_update_date` timestamp NULL DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `device_id` (`device_id`),
  KEY `probe_type_id` (`probe_type_id`),
  CONSTRAINT `nm_probe_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `nm_device` (`id`),
  CONSTRAINT `nm_probe_ibfk_2` FOREIGN KEY (`probe_type_id`) REFERENCES `nm_probe_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_probe_stats
# ------------------------------------------------------------

CREATE TABLE `nm_probe_stats` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `probe_identity_id` int(11) unsigned NOT NULL,
  `attribute_name` varchar(255) NOT NULL DEFAULT '',
  `attribute_value` text,
  `last_update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `probe_identity_id` (`probe_identity_id`),
  CONSTRAINT `nm_probe_stats_ibfk_1` FOREIGN KEY (`probe_identity_id`) REFERENCES `nm_probe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nm_probe_type
# ------------------------------------------------------------

CREATE TABLE `nm_probe_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `probe_name` varchar(255) NOT NULL DEFAULT '',
  `probe_description` text,
  `probe_classpath` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
