REPLACE PROCEDURE PMART.FILLBLANK_0102 (IN P_MONTH_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(4000) DEFAULT '';
  DECLARE P_OSTORE_ID NUMBER;
  DECLARE P_OPNDT NUMBER;
  DECLARE P_ENDDT NUMBER;
  DECLARE STORE_CS CURSOR FOR STORE_SQL;    
  CALL PMART.P_DROP_TABLE ('#VT_FILLBLANK');
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FILLBLANK  
    (    
       OSTORE_ID NUMBER
      ,OPNDT NUMBER
      ,ENDDT NUMBER
    )
   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
  EXECUTE IMMEDIATE SQLSTR;    
  SET SQLSTR =' SELECT DISTINCT A.OSTORE_ID,A.OPNDT,A.ENDDT                                                ' +
              '   FROM (SELECT * FROM PMART.LATEST_ORG_DIM WHERE  ASTORE_ID IS NULL AND ROUND(ENDDT/100) = '+P_MONTH_ID+'  ) A  ' +
              '    ,(SELECT L_DAY_ID                                                                       ' +
              '        FROM PMART.YMWD_TIME                                                                ' +
              '       WHERE L_MONTH_ID= '+P_MONTH_ID+'                                                   ' +
              '     ) B                                                                                    ' +
              '  WHERE ((A.OPNDT <  B.L_DAY_ID  AND B.L_DAY_ID <= A.ENDDT AND B.L_DAY_ID  <  20131015 )    ' +
              '      OR (A.OPNDT <= B.L_DAY_ID  AND B.L_DAY_ID <  A.ENDDT AND B.L_DAY_ID  >= 20131015 )    ' +
              '        )                                                                                   ' +
              '  ' ;   
  PREPARE STORE_SQL FROM SQLSTR;
  OPEN STORE_CS;  
  L1:
  WHILE (SQLCODE =0) 
  DO    
     L1_1:
        BEGIN 
         FETCH STORE_CS INTO P_OSTORE_ID,P_OPNDT,P_ENDDT  ;
         IF SQLSTATE <> '00000' THEN LEAVE L1; END IF; 	   
         INSERT INTO  #VT_FILLBLANK  (OSTORE_ID,OPNDT,ENDDT ) VALUES (P_OSTORE_ID,P_OPNDT,P_ENDDT);   
         INSERT INTO PMART.REMD_FACT  (L_DAY_ID,OSTORE_ID,STORE_ID,BUDGET_ST_AVG_AMT,BUDGET_ST_TOT_AMT,BUDGET_DEPT_AVG_AMT,BUDGET_DEPT_TOT_AMT,STATUS,AC)
         SELECT 
          S.L_DAY_ID
         ,S.OSTORE_ID
         ,S.STORE_ID
         ,S.BUDGET_ST_AVG_AMT
         ,S.BUDGET_ST_TOT_AMT
         ,S.BUDGET_DEPT_AVG_AMT
         ,S.BUDGET_DEPT_TOT_AMT
         ,S.STATUS
         ,S.AC
         FROM 
         (
         SELECT 
          A.L_DAY_ID
         ,B.OSTORE_ID
         ,B.STORE_ID
         ,B.BUDGET_ST_AVG_AMT
         ,B.BUDGET_ST_TOT_AMT
         ,B.BUDGET_DEPT_AVG_AMT
         ,B.BUDGET_DEPT_TOT_AMT
         ,B.STATUS
         ,B.AC
         FROM (SELECT L_DAY_ID FROM PMART.YMWD_TIME WHERE L_MONTH_ID= P_MONTH_ID ) A 
         LEFT OUTER JOIN 
              (SELECT L_DAY_ID
                     ,OSTORE_ID
                     ,STORE_ID
                     ,BUDGET_ST_AVG_AMT
                     ,BUDGET_ST_TOT_AMT
                     ,BUDGET_DEPT_AVG_AMT
                     ,BUDGET_DEPT_TOT_AMT
                     ,0 AS STATUS
                     ,1 AS AC
                FROM PMART.REMD_FACT  
               WHERE ROUND(L_DAY_ID/100) = P_MONTH_ID AND OSTORE_ID = P_OSTORE_ID
                 AND L_DAY_ID+OSTORE_ID IN (SELECT MAX(L_DAY_ID)+OSTORE_ID 
                                               FROM PMART.REMD_FACT 
                                              WHERE ROUND(L_DAY_ID/100)= P_MONTH_ID 
                                                AND OSTORE_ID = P_OSTORE_ID
                                                AND AC = 1
                                              GROUP BY OSTORE_ID 
                                            )
              ) B
          ON 1 = 1  
         ) S LEFT OUTER  JOIN PMART.REMD_FACT  T
          ON (T.L_DAY_ID=S.L_DAY_ID 
         AND  T.OSTORE_ID=S.OSTORE_ID)
       WHERE T.L_DAY_ID IS NULL
         AND S.L_DAY_ID >= P_OPNDT
         ;
     END L1_1;
  END WHILE L1;      
  CLOSE STORE_CS;
END SP;