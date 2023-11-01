REPLACE PROCEDURE PMART.REMD_SC_COMPACT_NCIG()
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(4000) DEFAULT '';
  DECLARE P_YYYYMM NUMBER;
  DECLARE STORE_CS CURSOR FOR STORE_SQL;    
  DECLARE CONTINUE HANDLER FOR SQLSTATE '52010'  
  CALL PMART.P_DROP_TABLE('#VT_REMD_SC_COMPACT_NCIG');
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_REMD_SC_COMPACT_NCIG  
    (    
       MONTH_ID NUMBER
    )
   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
  EXECUTE IMMEDIATE SQLSTR;  
  SET SQLSTR ='SELECT DISTINCT ROUND(L_DAY_ID/100) AS YYYYMM FROM ' +
              ' ( ' +
             ' SELECT (CAST(REMD_DATE AS INTEGER) + 19110000 ) AS L_DAY_ID FROM PSTAGE.AC_RMMMDDI ' +   
              ' ) AA ' ;
  PREPARE STORE_SQL FROM SQLSTR;
  OPEN STORE_CS;  
  L1:
  WHILE (SQLCODE =0) 
  DO    
     L1_1:
        BEGIN 
         FETCH STORE_CS INTO P_YYYYMM  ;
         IF SQLSTATE <> '00000' THEN LEAVE L1; END IF; 	   
         INSERT INTO  #VT_REMD_SC_COMPACT_NCIG  (MONTH_ID ) VALUES (P_YYYYMM);   
         CALL PMART.NEWFILLBLANK_NCIG(P_YYYYMM);
		 CALL PMART.FILLBLANK(P_YYYYMM);       
         CALL PMART.COMPACTMONTH_NCIG(P_YYYYMM);
         CALL PMART.REMD_EX_TRANSFER_NCIG(P_YYYYMM);
     END L1_1;
  END WHILE L1;      
  CLOSE STORE_CS;
END SP;