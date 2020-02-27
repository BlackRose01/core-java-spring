USE `arrowhead`;

REVOKE ALL, GRANT OPTION FROM 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_measurement` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement` TO 'qos_monitor'@'localhost';
GRANT SELECT ON `arrowhead`.`system_` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement_log` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement_log_details` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_measurement` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement_log` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement_log_details` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`cloud` TO 'qos_monitor'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`cloud_gatekeeper_relay` TO 'qos_monitor'@'localhost';

REVOKE ALL, GRANT OPTION FROM 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_measurement` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement` TO 'qos_monitor'@'%';
GRANT SELECT ON `arrowhead`.`system_` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement_log` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_intra_ping_measurement_log_details` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_measurement` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement_log` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`qos_inter_ping_measurement_log_details` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`cloud` TO 'qos_monitor'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`cloud_gatekeeper_relay` TO 'qos_monitor'@'%';

FLUSH PRIVILEGES;