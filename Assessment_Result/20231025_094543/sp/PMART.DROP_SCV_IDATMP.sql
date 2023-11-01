REPLACE PROCEDURE PMART.DROP_SCV_IDATMP( IN TIME_ID INTEGER)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(4000) DEFAULT '';
  DECLARE SQL_DROP  VARCHAR(4000) DEFAULT '';
  DECLARE TEMP_TABLE CHARACTER(100);
  DECLARE STORE_CS CURSOR FOR STORE_SQL;    
  SET SQLSTR =' SELECT  TEMP_FILE_NAME   FROM SCV_IDACFGVW.IDA_ONLINE_JOB_LOG 
            WHERE DatePart(year , ADD_TIME)*10000+ 
			   			  DatePart(month , ADD_TIME)*100+
						  DatePart(day , ADD_TIME)*1 = '+TIME_ID+
						  'AND  JOB_STATUS = ''PUMPED'''        
			  ;   
  PREPARE STORE_SQL FROM SQLSTR;
  OPEN STORE_CS;  
  L1:
  WHILE (SQLCODE =0) 
  DO    
     L1_1:
       BEGIN 
         FETCH STORE_CS INTO TEMP_TABLE;
         IF SQLSTATE <> '00000' THEN LEAVE L1; END IF; 	   
		 SET SQL_DROP = 'DROP TABLE ' + TEMP_TABLE + '' ;
		 EXECUTE IMMEDIATE SQL_DROP;
     END L1_1;
  END WHILE L1;      
  CLOSE STORE_CS;
END SP;