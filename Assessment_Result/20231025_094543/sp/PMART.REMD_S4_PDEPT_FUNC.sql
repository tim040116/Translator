REPLACE PROCEDURE PMART.REMD_S4_PDEPT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_S4_PDEPT_FUNC'); 
  LOCKING PMART.BASIC_OST_FACT FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM_MASK FOR ACCESS;
  LOCKING PMART.PLAN_STNUM FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM FOR ACCESS;  
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_S4_PDEPT_FUNC AS('+            
          'SELECT '+
          'B.ORG_ID AS ORG_ID, '+
          'BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,B.MASK)) AS MAST_STORE_NUM, '+
          ' COUNT(DISTINCT C.OSTORE_ID) AS STOP_STNUM '+              
          'FROM PMART.BASIC_OST_FACT A '+
          ' INNER JOIN '+            
          '(SELECT ORG_ID ,MASK FROM PMART.LAST_ORG_DIM_MASK WHERE P_ORG_ID=-1) B '+
          ' ON 1 = 1 '+
          ' LEFT OUTER JOIN '+
          ' (SELECT C1.*,C2.DEPT_ID AS ORG_ID '+
          ' FROM PMART.PLAN_STNUM  C1 INNER JOIN PMART.LAST_ORG_DIM C2 '+
          ' ON C1.STORE_ID = C2.STORE_ID '+
          ' WHERE C1.OSTORE_ID NOT IN (SELECT OSTORE_ID FROM PMART.REMD_FACT '+
          ' WHERE L_DAY_ID='+P_DAY_ID+
          '   AND UPLOAD_STNUM=1)) C  '+           
          ' ON A.TIME_ID = C.L_DAY_ID '+
          ' AND B.ORG_ID = C.ORG_ID '+          
          ' WHERE A.TIME_ID='+P_DAY_ID+
          ' GROUP BY B.ORG_ID,A.MAST_STORE_NUM,B.MASK '+          
          ' ) WITH DATA ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;