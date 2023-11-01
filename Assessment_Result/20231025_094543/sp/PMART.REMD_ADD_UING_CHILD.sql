REPLACE PROCEDURE PMART.REMD_ADD_UING_CHILD(P_FUNCION VARCHAR(50)  CASESPECIFIC,P_YEAR_ID  NUMBER, P_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(4000) DEFAULT '';
  DECLARE V_TOT_AMT NUMBER;
  DECLARE V_TOT_CUST_NUM NUMBER;
  DECLARE V_CHILD_ID NUMBER;
  DECLARE V_YM CHAR(1);
  DECLARE P_MONTH_ID NUMBER;
  DECLARE P_MONTH_LAST_MONTH NUMBER;
  DECLARE STORE_CS CURSOR FOR STORE_SQL;    
  CALL PMART.P_DROP_TABLE ('#VT_REMD_ADD_UING_CHILD');  
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_REMD_ADD_UING_CHILD  
    (    
      ORG_ID NUMBER, MONTH_ID NUMBER, YM CHAR(1), TOT_AMT NUMBER, TOT_CUST_NUM NUMBER
    )
   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
  EXECUTE IMMEDIATE SQLSTR;    
      SET SQLSTR ='  INSERT INTO #VT_REMD_ADD_UING_CHILD                             ' +
                  'SELECT B.ORG_ID,A.L_MONTH_ID,''Y'' AS YM,0,0                      ' +
                  '  FROM (                                                          ' +
                  'SELECT DISTINCT L_MONTH_ID                                        ' +
                  ',ROUND(L_DAY_FIRST_DAY_LAST_MONTH/100) AS L_MONTH_LAST_MONTH      ' +
                  '  FROM PMART.YMWD_TIME                                            ' + 
                  ' WHERE L_YEAR_ID= '+P_YEAR_ID+'                                 ' +
                  ')A,                                                               ' +
                  '(                                                                 ' +
                  'SELECT '+P_ORG_ID+'                                         ' +
                  '   AS ORG_ID                                                      ' +
                  ')B                                                                ' +
                  '; ' ; 
      EXECUTE IMMEDIATE SQLSTR;    
      SET SQLSTR ='  INSERT INTO #VT_REMD_ADD_UING_CHILD                             ' +
                  'SELECT B.ORG_ID,A.L_MONTH_ID,''M'' AS YM,0,0                      ' +
                  '  FROM (                                                          ' +
                  'SELECT DISTINCT L_MONTH_ID                                        ' +
                  ',ROUND(L_DAY_FIRST_DAY_LAST_MONTH/100) AS L_MONTH_LAST_MONTH      ' +
                  '  FROM PMART.YMWD_TIME                                            ' + 
                  ' WHERE L_YEAR_ID= '+P_YEAR_ID+'                                 ' +
                  ')A,                                                               ' +
                  '(                                                                 ' +
                  'SELECT '+P_ORG_ID+'                                         ' +
                  '   AS ORG_ID                                                      ' +
                  ')B                                                                ' +
                  '; ' ; 
      EXECUTE IMMEDIATE SQLSTR;    	  
END SP;