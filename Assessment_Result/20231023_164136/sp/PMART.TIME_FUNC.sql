REPLACE PROCEDURE PMART.TIME_FUNC
(FP_TIME_TYPE CHAR CASESPECIFIC,FP_TIME_ID INTEGER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000);
DECLARE V_TIME_SERIAL INTEGER;
   CALL PMART.P_DROP_TABLE ('#VT_TIME_FUNC'); 
   CASE FP_TIME_TYPE
      WHEN 'D' THEN  
         SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_TIME_FUNC  AS('+ 
                      'SELECT DISTINCT '+
                      'L_DAY_ID AS TIME_ID, '+
                      'L_DAY_NAME AS TIME_NM '+
                      'FROM PMART.YMWD_TIME  '+
                      'WHERE L_WEEK_ID='+ FP_TIME_ID  +
                      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';         
      WHEN 'W' THEN  
           SELECT DISTINCT L_WEEK_SERIAL INTO V_TIME_SERIAL 
           FROM   PMART.YMWD_TIME 
           WHERE L_WEEK_ID=FP_TIME_ID;
           SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_TIME_FUNC  AS('+ 
                      'SELECT DISTINCT '+
                      'L_WEEK_ID AS TIME_ID,'+
                      'L_WEEK_SNAME AS TIME_NM '+
                      'FROM PMART.YMWD_TIME WHERE L_WEEK_SERIAL>'+V_TIME_SERIAL+' -6'+
                      'AND L_WEEK_SERIAL<='+V_TIME_SERIAL+'  '+
                      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';             
       WHEN 'M' THEN  
          SELECT DISTINCT L_MONTH_SERIAL INTO V_TIME_SERIAL 
          FROM PMART.YMWD_TIME 
          WHERE L_MONTH_ID=FP_TIME_ID;
          SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_TIME_FUNC  AS('+ 
                      'SELECT DISTINCT '+
                      'L_MONTH_ID AS TIME_ID, '+
                      'L_MONTH_NAME AS TIME_NM '+
                      'FROM PMART.YMWD_TIME WHERE L_MONTH_SERIAL> '+V_TIME_SERIAL+' -6 '+
                      'AND L_MONTH_SERIAL<='+V_TIME_SERIAL +'  '+
                      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';            
   END CASE;
   EXECUTE IMMEDIATE SQLSTR;
END SP;