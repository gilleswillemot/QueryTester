#create database dbperf
#use dbperf

#CREATE TABLE category (
#ca_id int NOT NULL,
#ca_description varchar(50) default NULL,
#ca_code varchar(50) default NULL,
#PRIMARY KEY (ca_id)
#) ;*/

#Directory waar je je .csv file bestand moet toevoegen: SHOW VARIABLES LIKE "secure_file_priv";

#LOAD DATA LOCAL INFILE 'C:/category.csv' 
#INTO TABLE category 
#FIELDS TERMINATED BY ',' 
#ENCLOSED BY '"'
#LINES TERMINATED BY '\n'
#IGNORE 1 ROWS


SET profiling = 1;
#explain extended select * from dbperf.category;
SHOW PROFILES;
#FLUSH STATUS;
#SELECT * from dbperf.category;
#SHOW SESSION STATUS LIKE 'Handler%';