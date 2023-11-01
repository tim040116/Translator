REPLACE PROCEDURE PMART.PICL1_FUNC
(P_YEAR_ID NUMBER,P_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_PICL1_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_PICL1_FUNC AS('+    
               'SELECT '+
               'A.L_MONTH_ID AS L_MONTH_ID, '+
               'B.INAMT8 AS INAMT8, '+
               'B.SALAMT8 AS SALAMT8, '+
               'B.INSALD8 AS INSALD8, '+
               'B.ACCTAMT8 AS ACCTAMT8, '+
               'B.INAMT11 AS INAMT11, '+
               'B.SALAMT11 AS SALAMT11, '+
               'B.INSALD11 AS INSALD11, '+
               'B.ACCTAMT11 AS ACCTAMT11, '+
               'B.INAMT12 AS INAMT12, '+
               'B.SALAMT12 AS SALAMT12, '+
               'B.INSALD12 AS INSALD12, '+
               'B.ACCTAMT12 AS ACCTAMT12, '+
               'B.ACCTAMT90 AS ACCTAMT90 '+
               'FROM '+
               '( '+
               'SELECT DISTINCT L_MONTH_ID FROM PMART.YMWD_TIME WHERE L_YEAR_ID='+P_YEAR_ID+
               ') A '+
               'LEFT JOIN '+
               '( '+
               'SELECT * '+
               'FROM PMART.PICL3090M_SUM '+
               'WHERE  ORG_ID='+P_ORG_ID+
               ')B ON (A.L_MONTH_ID=B.L_MONTH_ID) '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;