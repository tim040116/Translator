REPLACE PROCEDURE PMART.POSO1_8A_FUNC3
(
FP_DAY_ID  NUMBER,     
FP_ORG_ID  VARCHAR(50),
FP_SER     NUMBER     
)            
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR     VARCHAR(8000);
   DECLARE P_DATA_1 VARCHAR(100) Collate Chinese_Taiwan_Stroke_CI_AS;   
   DECLARE V_DATA_1 VARCHAR(1000) Collate Chinese_Taiwan_Stroke_CI_AS;   
   DECLARE STORE_CS CURSOR FOR STORE_SQL;       
   CALL PMART.P_DROP_TABLE ('#VT_POSO1_8A_FUNC3_1'); 
   CALL PMART.P_DROP_TABLE ('#VT_POSO1_8A_FUNC3_2'); 
   CALL PMART.P_DROP_TABLE ('#VT_POSO1_8A_FUNC3_3');
   CALL PMART.P_DROP_TABLE ('#VT_POSO1_8A_FUNC3');   
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_POSO1_8A_FUNC3_1  
                (    
                  DEPT_ID INTEGER
                 ,DEPT_NM VARCHAR(200) Collate Chinese_Taiwan_Stroke_CI_AS
                )
                NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';   
  EXECUTE IMMEDIATE SQLSTR;
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_POSO1_8A_FUNC3_2  
                (    
                  V_ORG_ID INTEGER
				 ,V_ORG_NM VARCHAR(200) Collate Chinese_Taiwan_Stroke_CI_AS
				 ,V_PRD_NM VARCHAR(200) Collate Chinese_Taiwan_Stroke_CI_AS
                 ,V_INPRD_CNT NUMBER
				 ,V_SALES_CNT NUMBER
				 ,V_SALES_RATE NUMBER
				 ,V_D_CNT NUMBER
                )
                NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';   
  EXECUTE IMMEDIATE SQLSTR;
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_POSO1_8A_FUNC3_3  
                (    
                  ORG_ID INTEGER
                 ,DATA_1 VARCHAR(200) Collate Chinese_Taiwan_Stroke_CI_AS
                )
                NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';   
  EXECUTE IMMEDIATE SQLSTR;       
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_POSO1_8A_FUNC3  
                (    
                  MESSAGE VARCHAR(200) Collate Chinese_Taiwan_Stroke_CI_AS
                )
                NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';   
  EXECUTE IMMEDIATE SQLSTR;
   INSERT INTO #VT_POSO1_8A_FUNC3_1(DEPT_ID  ,DEPT_NM )
   SELECT DISTINCT DEPT_ID, Replace(Replace(DEPT_NM,'營業'),'區')DEPT_NM FROM PMART.LATEST_ORG_DIM 
    UNION 
   SELECT DEPT_ID , DEPT_NM FROM (SELECT -1 AS DEPT_ID , '' AS DEPT_NM) AA ;  
   SET SQLSTR =
      ' INSERT INTO #VT_POSO1_8A_FUNC3_2  '
      'SELECT O.DEPT_ID AS V_ORG_ID, O.DEPT_NM AS V_ORG_NM, P.PRD_NM AS V_PRD_NM, ' +
      '       DECODE(A.INPRD_CNT, NULL, 0, A.INPRD_CNT) AS V_INPRD_CNT,  '+
      '       DECODE(B.SALES_CNT, NULL, 0, B.SALES_CNT) AS V_SALES_CNT,  '+
      '       DECODE(A.INPRD_CNT, 0, 0, ROUND( (CAST(B.SALES_CNT AS DECIMAL(20,4))/CAST(NULLIFZERO(A.INPRD_CNT) AS DECIMAL(20,4))) * 100, 2)) AS V_SALES_RATE, '+
      '       DECODE(B.SALES_CNT, NULL, 0, B.SALES_CNT) - DECODE(BB.SALES_CNT, NULL, 0, BB.SALES_CNT) AS V_D_CNT '+
      '  FROM  #VT_POSO1_8A_FUNC3_1 O '+
      ' LEFT OUTER JOIN  ( '+
      '  SELECT ORG_ID, SUM(INPRD_CNT) INPRD_CNT '+
      '    FROM ( '+
      '          SELECT T.ORG_ID, T.TIME_ID, MAX(INPRD_CNT) INPRD_CNT '+
      '            FROM PMART.BASIC_MFACT T '+
      '           WHERE TIME_ID IN '+
      '                       (SELECT Y.L_DAY_ID '+
      '                          FROM PMART.YMWD_TIME Y, '+
      '                               (SELECT CASE WHEN L_DAY_OF_WEEK = 7 THEN CAST(CAST(L_DAY_ID AS DATE )- 2  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 1 THEN CAST(CAST(L_DAY_ID AS DATE )- 3  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 2 THEN CAST(CAST(L_DAY_ID AS DATE )- 4  AS INTEGER) '+
      '                                       ELSE CAST(CAST(L_DAY_ID AS DATE )- 1  AS INTEGER) '+
      '                                       END AS S_DATE, '+
      '                                       CAST(CAST(L_DAY_ID AS DATE )- 0  AS INTEGER) AS E_DATE '+
      '                                  FROM PMART.YMWD_TIME Y '+
      '                                 WHERE L_DAY_ID = ' + FP_DAY_ID + ') B  '+
      '                     WHERE Y.L_DAY_ID >= B.S_DATE AND Y.L_DAY_ID <= B.E_DATE) AND '+
      '                 (T.ORG_ID IN ( '+ FP_ORG_ID +' )) AND '+
      '                 T.PRD_ID IN( ' +
      '                             SELECT PRD_ID FROM PMART.POSO_PUBLISH_CHK2 T  ' +
      '                              WHERE L_DAY_ID = (SELECT MAX(L_DAY_ID) ' +
      '                               FROM PMART.POSO_PUBLISH_CHK2 ST             ' +
      '                              WHERE L_DAY_ID < ' + FP_DAY_ID + '  )' +
      '                                AND PRD_SER= ' + FP_SER      + ' )' +
      '           GROUP BY T.ORG_ID, T.TIME_ID '+
      '         ) A1 '+
      '   GROUP BY ORG_ID '+
      '  ) A  '+
      ' ON O.DEPT_ID = A.ORG_ID '+      
      ' LEFT OUTER JOIN  ( '+
      '  SELECT ORG_ID,SUM(SALES_CNT) SALES_CNT '+
      '    FROM ( '+
      '          SELECT T.ORG_ID, T.TIME_ID, MAX(T.SALES_CNT) SALES_CNT '+
      '            FROM PMART.BASIC_MFACT T '+
      '           WHERE TIME_ID IN '+
      '                       (SELECT Y.L_DAY_ID '+
      '                          FROM PMART.YMWD_TIME Y, '+
      '                               (SELECT CASE WHEN L_DAY_OF_WEEK = 7 THEN CAST(CAST(L_DAY_ID AS DATE )- 1  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 1 THEN CAST(CAST(L_DAY_ID AS DATE )- 2  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 2 THEN CAST(CAST(L_DAY_ID AS DATE )- 3  AS INTEGER) '+
      '                                         ELSE ''99999999'' '+
      '                                       END AS S_DATE, '+
      '                                       CAST(CAST(L_DAY_ID AS DATE )- 1  AS INTEGER) AS E_DATE '+      
      '                                  FROM PMART.YMWD_TIME Y '+
      '                                 WHERE L_DAY_ID = ' + FP_DAY_ID + ') B '+
      '                         WHERE Y.L_DAY_ID >= B.S_DATE AND Y.L_DAY_ID <= B.E_DATE) AND '+
      '                 (T.ORG_ID IN ( '+ FP_ORG_ID +' )) AND  '+
      '                 T.PRD_ID IN( ' +
      '                             SELECT PRD_ID FROM PMART.POSO_PUBLISH_CHK2 T  ' +
      '                              WHERE L_DAY_ID = (SELECT MAX(L_DAY_ID) ' +
      '                               FROM PMART.POSO_PUBLISH_CHK2 ST             ' +
      '                              WHERE L_DAY_ID < ' + FP_DAY_ID + '  )' +
      '                                AND PRD_SER= ' + FP_SER      + ' )' +
      '           GROUP BY T.ORG_ID, T.TIME_ID '+
      '          ) B1 '+
      '    GROUP BY ORG_ID       '+
      '  ) B '+
      ' ON O.DEPT_ID = B.ORG_ID '+     
      ' LEFT OUTER JOIN  ( '+      
      '  SELECT ORG_ID, SUM(SALES_CNT) SALES_CNT '+
      '    FROM ( '+
      '          SELECT T.ORG_ID, T.TIME_ID, MAX(T.SALES_CNT) SALES_CNT '+
      '            FROM PMART.BASIC_MFACT T '+
      '           WHERE TIME_ID IN '+
      '                       (SELECT Y.L_DAY_ID '+
      '                          FROM PMART.YMWD_TIME Y, '+      
      '                               (SELECT CASE WHEN L_DAY_OF_WEEK = 7 THEN CAST(CAST(L_DAY_ID AS DATE )- 8  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 1 THEN CAST(CAST(L_DAY_ID AS DATE )- 9  AS INTEGER) '+
      '                                            WHEN L_DAY_OF_WEEK = 2 THEN CAST(CAST(L_DAY_ID AS DATE )- 10 AS INTEGER) '+      
      '                                         ELSE ''99999999'' '+
      '                                       END AS S_DATE, '+
      '                                       CAST(CAST(L_DAY_ID AS DATE )- 8  AS INTEGER) AS E_DATE '+      
      '                                  FROM PMART.YMWD_TIME Y '+
      '                                 WHERE L_DAY_ID = ' + FP_DAY_ID + ') B '+
      '                         WHERE Y.L_DAY_ID >= B.S_DATE AND Y.L_DAY_ID <= B.E_DATE) AND '+
      '                 (T.ORG_ID IN ( '+ FP_ORG_ID +' )) AND  '+
      '                 T.PRD_ID IN( ' +
      '                             SELECT PRE_PRD_ID FROM PMART.POSO_PUBLISH_CHK2 T  ' +
      '                              WHERE L_DAY_ID = (SELECT MAX(L_DAY_ID) ' +
      '                               FROM PMART.POSO_PUBLISH_CHK2 ST             ' +
      '                              WHERE L_DAY_ID < ' + FP_DAY_ID + '  )' +
      '                                AND PRD_SER= ' + FP_SER      + ' )' +
      '           GROUP BY T.ORG_ID, T.TIME_ID '+
      '          ) BB1 '+
      '    GROUP BY ORG_ID          '+
      '  ) BB '+
      ' ON O.DEPT_ID = BB.ORG_ID '+
      ' INNER JOIN '
      '(SELECT PRD_NM FROM PMART.POSO_PUBLISH_CHK2 T  ' +
      '                              WHERE L_DAY_ID = (SELECT MAX(L_DAY_ID) ' +
      '                               FROM PMART.POSO_PUBLISH_CHK2 ST             ' +
      '                              WHERE L_DAY_ID < ' + FP_DAY_ID + '  )' +
      '                                AND PRD_SER= ' + FP_SER      + ' ) P ' +
      ' ON 1 = 1  '+  	  
      ' WHERE O.DEPT_ID IN ( '+ FP_ORG_ID +' ) '+
      ';';
  EXECUTE IMMEDIATE SQLSTR;
  INSERT INTO #VT_POSO1_8A_FUNC3_3(ORG_ID  ,DATA_1 )
  SELECT  V_ORG_ID
         ,CASE WHEN V_ORG_ID = -1 THEN
               V_PRD_NM + ' 進' + TRIM(TO_CHAR(V_INPRD_CNT, '9999990')) + 
                    ',銷' + TRIM(TO_CHAR(V_SALES_CNT, '9999990')) + ',比' +
                             TRIM(TO_CHAR(V_SALES_RATE, '9999990.00')) + '%'+ ',差' + TRIM(TO_CHAR(V_D_CNT, '9999990'))
          ELSE
               V_ORG_NM + ' ' + V_PRD_NM +  
                    ':銷' + TRIM(TO_CHAR(V_SALES_CNT, '9999990')) + ',比' +
                             TRIM(TO_CHAR(V_SALES_RATE, '9999990.00')) + '%'+ ',差' + TRIM(TO_CHAR(V_D_CNT, '9999990'))
          END AS DATA_1
  FROM #VT_POSO1_8A_FUNC3_2;
  SET SQLSTR = ' SELECT DATA_1 FROM #VT_POSO1_8A_FUNC3_3 ORDER BY ORG_ID ' ;						  
  PREPARE STORE_SQL FROM SQLSTR;
  OPEN STORE_CS;  
  SET V_DATA_1='';
  L1:
  WHILE (SQLCODE =0) 
  DO    
     L1_1:
        BEGIN 		
         FETCH STORE_CS INTO P_DATA_1 ;         
         IF SQLSTATE <> '00000' THEN 
            LEAVE L1; 
         END IF;  	   
		 IF V_DATA_1='' THEN
		  SET V_DATA_1 = P_DATA_1 ;
		 ELSE
         SET V_DATA_1 = V_DATA_1+','+P_DATA_1 ;
		 END IF;
     END L1_1;     
  END WHILE L1;
  INSERT INTO #VT_POSO1_8A_FUNC3 (MESSAGE) VALUES (V_DATA_1);	 
  CLOSE STORE_CS;     
END SP;