REPLACE PROCEDURE PTEMP.P_DROP_TABLE
(
  IN PTABLE_NAME VARCHAR (50)
)
SP:BEGIN
DECLARE VDSQL VARCHAR (100);
SET VDSQL = 'DROP TABLE ' + PTABLE_NAME + ';' ;
EXECUTE IMMEDIATE VDSQL ;
END SP;