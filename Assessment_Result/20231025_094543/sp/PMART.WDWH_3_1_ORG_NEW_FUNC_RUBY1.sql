REPLACE PROCEDURE PMART.WDWH_3_1_ORG_NEW_FUNC_RUBY1
(
   IN I_ORG_TYPE  VARCHAR(2),   
   IN I_ORG_ID    INTEGER,      
   IN I_DAY_ID    INTEGER       
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR     VARCHAR(4000) DEFAULT '';
  CALL PMART.P_DROP_TABLE ('#VT_WDWH_3_1_ORG_NEW_FUNC');
  IF (I_ORG_TYPE = '-1') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_ORG_NEW_FUNC AS  ' +
                  '(SELECT * FROM (                                         ' +
                  '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID      ' +
                  '        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO           ' +
                  '        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM           ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 0 AS ORG_LEVEL, PDEPT_ID AS ORG_ID      ' +
                  '        ,PDEPT_NO AS ORG_NO                               ' +
                  '        ,PDEPT_NM AS ORG_NM                               ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE TOT_ID = ' + I_ORG_ID + '                     ' +
                  '       ) AA                                              ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;
 INSERT INTO PMART.T1(F1,F2)
 VALUES  (20210217, SQLSTR);
    IF (I_ORG_TYPE = '0') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_ORG_NEW_FUNC AS  ' +
                  '(SELECT * FROM (                                         ' +
                  '  SELECT DISTINCT -1 AS ORG_LEVEL , TOT_ID AS ORG_ID     ' +
                  '        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO           ' +
                  '        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM           ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 0 AS ORG_LEVEL  , PDEPT_ID AS ORG_ID    ' +
                  '        ,CAST(PDEPT_NO AS VARCHAR(10)) AS ORG_NO          ' +
                  '        ,CAST(PDEPT_NM AS VARCHAR(50)) AS ORG_NM          ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE PDEPT_ID = ' + I_ORG_ID + '                    ' +              
				  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 1 AS ORG_LEVEL  , DEPT_ID AS ORG_ID    ' +
                  '        ,CAST(DEPT_NO AS VARCHAR(10)) AS ORG_NO          ' +
                  '        ,CAST(DEPT_NM AS VARCHAR(50)) AS ORG_NM          ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE PDEPT_ID = ' + I_ORG_ID + '                    ' +              
                  '     ) AA                                                ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;
  IF (I_ORG_TYPE = '1') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_ORG_NEW_FUNC AS  ' +
                  '(SELECT * FROM (                                         ' +
                  '  SELECT DISTINCT -1 AS ORG_LEVEL , TOT_ID AS ORG_ID     ' +
                  '        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO           ' +
                  '        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM           ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
				  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 0 AS ORG_LEVEL  , PDEPT_ID AS ORG_ID    ' +
                  '        ,CAST(PDEPT_NO AS VARCHAR(10)) AS ORG_NO          ' +
                  '        ,CAST(PDEPT_NM AS VARCHAR(50)) AS ORG_NM          ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE DEPT_ID = ' + I_ORG_ID + '                    ' +
                  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 1 AS ORG_LEVEL  , DEPT_ID AS ORG_ID    ' +
                  '        ,CAST(DEPT_NO AS VARCHAR(10)) AS ORG_NO          ' +
                  '        ,CAST(DEPT_NM AS VARCHAR(50)) AS ORG_NM          ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE DEPT_ID = ' + I_ORG_ID + '                    ' +
                  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 2 AS ORG_LEVEL  , BRANCH_ID AS ORG_ID  ' +
                  '        ,BRANCH_NO AS ORG_NO                             ' +
                  '        ,BRANCH_NM AS ORG_NM                             ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE DEPT_ID = ' + I_ORG_ID + '                    ' +
                  '     ) AA                                                ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;
  IF (I_ORG_TYPE = '2') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_ORG_NEW_FUNC AS  ' +
                  '(SELECT * FROM (                                         ' +
                  '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID      ' +
                  '        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO           ' +
                  '        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM           ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
			      '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 2 AS ORG_LEVEL, BRANCH_ID AS ORG_ID    ' +
                  '        ,CAST(BRANCH_NO AS VARCHAR(10)) AS ORG_NO        ' +
                  '        ,CAST(BRANCH_NM AS VARCHAR(50)) AS ORG_NM        ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE BRANCH_ID=' + I_ORG_ID + '                    ' +
                  '   UNION ALL                                             ' +
                  '  SELECT DISTINCT 3 AS ORG_LEVEL, RESPON_ID AS ORG_ID    ' +
                  '        ,RESPON_NO AS ORG_NO                             ' +
                  '        ,RESPON_NM AS ORG_NM                             ' +
                  '    FROM PMART.LAST_ORG_DIM                              ' +
                  '   WHERE BRANCH_ID=' + I_ORG_ID + '                    ' +
                  '     ) AA                                                ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;  
  IF (I_ORG_TYPE = '3') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_ORG_NEW_FUNC AS         ' +
                  '(SELECT * FROM (                                                ' +
                  '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID             ' +
                  '        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO                 ' +
                  '        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM                 ' +
                  '    FROM PMART.LAST_ORG_DIM                                     ' +
                  '   UNION ALL                                                    ' +
                  '  SELECT DISTINCT 3 AS ORG_LEVEL, RESPON_ID AS ORG_ID           ' +
                  '        ,CAST(RESPON_NO AS VARCHAR(10)) AS ORG_NO              ' +
                  '        ,CAST(RESPON_NM AS VARCHAR(50)) AS ORG_NM              ' +
                  '    FROM PMART.LAST_ORG_DIM                                     ' +
                  '   WHERE RESPON_ID=' + I_ORG_ID + '                           ' +
                  '   UNION ALL                                                    ' +
                  '  SELECT DISTINCT 4 AS ORG_LEVEL, OSTORE_ID AS ORG_ID           ' +
                  '        ,STORE_NO AS ORG_NO                                     ' +
                  '        ,STORE_NM AS ORG_NM                                     ' +
                  '    FROM PMART.LATEST_ORG_DIM                                   ' +
                  '   WHERE RESPON_ID=' + I_ORG_ID + '                           ' +
                  '     AND ROUND(OPNDT/100)<=ROUND(' + I_DAY_ID + '/100)        ' +
                  '     AND ROUND(ENDDT/100)>=ROUND(' + I_DAY_ID + '/100)        ' +
                  '     AND ((ASTORE_ID IS NULL     AND OPNDT<=' + I_DAY_ID + ') ' +
                  '       OR (ASTORE_ID IS NOT NULL AND OPNDT<=' + I_DAY_ID + '  ' +
                  '      AND ' + I_DAY_ID + ' <ENDDT))                           ' +
                  '     ) AA                                                       ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;  
END SP;