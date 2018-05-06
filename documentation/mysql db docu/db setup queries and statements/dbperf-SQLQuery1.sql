/*CREATE TABLE category (
ca_id int NOT NULL,
ca_description varchar(50) default NULL,
ca_code varchar(50) default NULL,
PRIMARY KEY (ca_id)
) ;*/

/*
BULK INSERT category
FROM 'C:\category.csv'
WITH
(
    FIRSTROW = 2,
    FIELDTERMINATOR = ';',  --CSV field delimiter
    ROWTERMINATOR = '\n',   --Use to shift the control to next row
    TABLOCK
)
*/