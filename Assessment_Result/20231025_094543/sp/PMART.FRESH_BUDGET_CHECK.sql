REPLACE PROCEDURE PMART.FRESH_BUDGET_CHECK
(
   IN P_BUDGET_DATE INTEGER
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR     VARCHAR(4000) DEFAULT '';
  DECLARE P_ERR_CODE INTEGER;  	
  UPDATE A
    FROM PMART.FRESH_BUDGET A , (SELECT DISTINCT ORG_NO,ORG_SNAME
                                   FROM PMART.VW_ORG_DIM_NEW
                                  WHERE ORG_LEVEL = 2)  B
     SET BRANCH_NO = B.ORG_NO
   WHERE A.BRANCH_NM = ORG_SNAME
     AND A.BUDGET_DATE = P_BUDGET_DATE
   ; 
  UPDATE A
    FROM PMART.FRESH_BUDGET A , (SELECT DISTINCT KIND_CODE+GROUP_CODE AS NGROUP_CODE
                                                ,KIND_NAME+GROUP_NAME AS NGROUP_NAME
                                   FROM PDATA.FRESH_PBMKGRP
                                  WHERE GRP_FG = 'Y')  B
   SET GROUP_NAME = B.NGROUP_NAME
 WHERE A.GROUP_CODE = B.NGROUP_CODE
   AND A.BUDGET_DATE = P_BUDGET_DATE
   ;     
  SET P_ERR_CODE = 0 ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_TEMP');
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_CHECK');  
  SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_TEMP AS  ' +
               '(                                                        ' +
               ' SELECT BUDGET_DATE                                      ' +
               '       ,BUDGET_ID                                        ' +
               '       ,BRANCH_NO                                        ' +
               '       ,BRANCH_NM                                        ' +
               '       ,GROUP_CODE                                       ' +
               '       ,GROUP_NAME                                       ' +
               '       ,BUDGET_AMT                                       ' +
               '       ,ERROR_CODE                                       ' +
               '       ,ERROR_TYPE                                       ' +
               '       ,UPDATE_TIME                                      ' +
               '  FROM PMART.FRESH_BUDGET                                ' +
               ' WHERE BUDGET_DATE = '+P_BUDGET_DATE+'                 ' +
               ')                                                        ' +
               'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR;
  UPDATE #VT_FRESH_BUDGET_TEMP SET ERROR_CODE ='' , ERROR_TYPE = '0' ; 
  UPDATE #VT_FRESH_BUDGET_TEMP A
  SET ERROR_CODE = '課別代號不存在' ,ERROR_TYPE = '1' 
  WHERE  A.BRANCH_NO NOT IN 
  (SELECT DISTINCT ORG_NO
     FROM PMART.VW_ORG_DIM_NEW
    WHERE ORG_LEVEL = 2
  )  ;
  UPDATE #VT_FRESH_BUDGET_TEMP A
  SET ERROR_CODE = '品群番不存在' ,ERROR_TYPE = '2' 
  WHERE  A.GROUP_CODE NOT IN 
  (SELECT DISTINCT KIND_CODE+GROUP_CODE
     FROM PDATA.FRESH_PBMKGRP
    WHERE GRP_FG = 'Y'
  )  ;
  UPDATE #VT_FRESH_BUDGET_TEMP A
  SET ERROR_CODE = '課別+品群番重覆' ,ERROR_TYPE = '3' 
  WHERE A.BRANCH_NO+A.GROUP_CODE IN 
  (SELECT DISTINCT BRANCH_NO+GROUP_CODE
     FROM #VT_FRESH_BUDGET_TEMP
    GROUP BY BRANCH_NO+GROUP_CODE 
   HAVING COUNT(*) > 1
  )  ;
  SELECT COUNT(*) AS CNT INTO P_ERR_CODE
    FROM #VT_FRESH_BUDGET_TEMP
   WHERE ERROR_TYPE >= '1'
  ;
  UPDATE A
  FROM PMART.FRESH_BUDGET A , #VT_FRESH_BUDGET_TEMP B
   SET ERROR_CODE = B.ERROR_CODE
      ,ERROR_TYPE = B.ERROR_TYPE
 WHERE A.BUDGET_DATE = B.BUDGET_DATE
   AND A.BUDGET_ID = B.BUDGET_ID 
   ;
  IF (P_ERR_CODE = 0) THEN
      SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_CHECK AS('
                 +' SELECT 0 AS RETURN_VAL, ''SUCCESS'' AS RETURN_DESCR'						  
		 +' ) WITH DATA UNIQUE PRIMARY INDEX(RETURN_VAL) ON COMMIT PRESERVE ROWS;';
  ELSE
      SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_CHECK AS('
                 +' SELECT 1 AS RETURN_VAL, ''FAIL'' AS RETURN_DESCR'						  
		 +' ) WITH DATA UNIQUE PRIMARY INDEX(RETURN_VAL) ON COMMIT PRESERVE ROWS;';
  END IF;    
  EXECUTE IMMEDIATE SQLSTR;   
END SP;