REPLACE PROCEDURE PMART.POSN_STFACT_1_FUNC
(
FP_TIME_TYPE CHAR,          
FP_TIME_LIST VARCHAR(400), 
FP_PRD_LEVEL VARCHAR(400), 
FP_PRD_ID    VARCHAR(1000), 
FP_PRD_SQL   NUMBER,        
FP_ORG_LEVEL NUMBER,        
FP_ORG_LIST  VARCHAR(400), 
FP_MMA_LEVEL NUMBER,        
FP_MMA_ID    VARCHAR(400), 
FP_PRD_TYPE  NUMBER         
)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(4000);
   DECLARE V_TABLE_NAME VARCHAR(100); 
   DECLARE V_TABLE_NAME2 VARCHAR(100); 
   DECLARE V_MMA VARCHAR(200); 
   DECLARE V_MMA_WHERE VARCHAR(200); 
   DECLARE V_MMA_GROUP VARCHAR(200); 
   DECLARE V_PRD_IN VARCHAR(1000);
   DECLARE V_GRP_ID VARCHAR(20);
   DECLARE I NUMBER;
   DECLARE V_PRD_INB NUMBER;
   CALL PMART.P_DROP_TABLE ('#VT_POSN_STFACT_1_FUNC'); 
   SET V_MMA = '0 AS MMA_ID, ';
   SET V_MMA_WHERE = ' ';
   SET V_MMA_GROUP = ' ';
   IF (FP_MMA_LEVEL = 0 OR FP_MMA_LEVEL = 1) THEN 
      IF FP_PRD_LEVEL='PRD' 
      THEN
          IF FP_PRD_TYPE = 1 THEN
          SET V_TABLE_NAME = 'PMART.BASIC_STLFACT_SHOP'; ELSE
             IF FP_TIME_TYPE = 'D' THEN
                 SET V_TABLE_NAME = 'PMART.BASIC_STFACT_DETAIL_SHOP';
             ELSE
               SET V_TABLE_NAME = 'PMART.BASIC_STFACT_SHOP';
             END IF;
          END IF;
      ELSE
          IF FP_PRD_TYPE = 1 THEN SET V_TABLE_NAME = 'PMART.BASIC_STLFACT_SHOP'; ELSE SET V_TABLE_NAME = 'PMART.BASIC_STFACT_SHOP'; END IF;
      END IF;
      SET V_MMA ='B.MMA_ID AS MMA_ID, ';
      SET V_MMA_WHERE =' AND B.MMA_ID IN ('+ FP_MMA_ID +')';
      SET V_MMA_GROUP =',B.MMA_ID';
   ELSE 
      IF FP_PRD_LEVEL='PRD' 
      THEN
          IF FP_PRD_TYPE = 1 THEN
               SET V_TABLE_NAME = 'PMART.BASIC_STLFACT'; ELSE
               IF FP_TIME_TYPE = 'D' THEN
               SET V_TABLE_NAME = 'PMART.BASIC_STFACT_DETAIL';
               ELSE
               SET V_TABLE_NAME = 'PMART.BASIC_STFACT';
               END IF;
          END IF;
      ELSE
          IF FP_PRD_TYPE = 1 THEN SET V_TABLE_NAME = 'PMART.BASIC_STLFACT'; ELSE SET V_TABLE_NAME = 'PMART.BASIC_STFACT'; END IF;
      END IF;
   END IF;
   CASE
      WHEN FP_PRD_LEVEL='ALL' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, TOT_ID AS G_PRD_ID FROM PMART.PRD_DIM';
      WHEN FP_PRD_LEVEL='KND' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, KND_ID AS G_PRD_ID FROM PMART.PRD_DIM';
	 WHEN FP_PRD_LEVEL='ALLPRD' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, ''-99'' AS G_PRD_ID FROM PMART.ORG_DIM_POSN1 WHERE SEQNO = 1 ';
      WHEN FP_PRD_LEVEL='TOT' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, TOT_ID AS G_PRD_ID FROM PMART.ORG_DIM_POSN1 WHERE SEQNO = 1 ';
      WHEN FP_PRD_LEVEL='DEPT'THEN
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, DEPT_ID AS G_PRD_ID FROM PMART.ORG_DIM_POSN1 WHERE SEQNO = 1 ';
      WHEN FP_PRD_LEVEL='BRANCH' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, BRANCH_ID AS G_PRD_ID FROM PMART.ORG_DIM_POSN1 WHERE SEQNO = 1 ';
      WHEN FP_PRD_LEVEL='RESPON' THEN 
          SET V_TABLE_NAME2 = 'SELECT DISTINCT GRP_ID AS I_PRD_ID, RESPON_ID AS G_PRD_ID FROM PMART.ORG_DIM_POSN1 WHERE SEQNO = 1 ';
      ELSE 
         SET V_TABLE_NAME2 = 'SELECT ''0'' AS I_PRD_ID, ''0'' AS G_PRD_ID FROM DUAL';
   END CASE;
	IF FP_PRD_SQL = 1 AND (FP_PRD_LEVEL = 'ALLPRD' OR FP_PRD_LEVEL = 'TOT') THEN 
		SET V_PRD_IN = FP_PRD_ID + ' AND SEQNO = 1 ';
	ELSE
		SET V_PRD_IN = FP_PRD_ID;
	END IF;
   IF(FP_PRD_LEVEL = 'GRP' OR FP_PRD_LEVEL = 'PRD') THEN 
    SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_STFACT_1_FUNC  AS('+
      'SELECT '+
      'B.TIME_ID AS TIME_ID, '+
      'B.PRD_ID, '+
      ' '+V_MMA+' '+
      'B.SALES_STORE_NUM, '+ 
      'B.ORDER_STORE_NUM, '+ 
      'B.THROW_STORE_NUM, '+ 
      'B.INPRD_STORE_NUM, '+ 
      'B.RETPRD_STORE_NUM, '+ 
      'B.TRANSPRD_STORE_NUM, '+ 
      'B.TRANSPRD_IN_STORE_NUM, '+ 
      'B.Y_ORDER_STORE_NUM '+ 
      'FROM '+ V_TABLE_NAME +' AS B '+
      ' WHERE B.TIME_ID IN ('+ FP_TIME_LIST +') '+
      ' AND B.PRD_ID IN ('+ V_PRD_IN +') '+ V_MMA_WHERE +' '+
      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
   ELSE
    SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_STFACT_1_FUNC  AS('+
      'SELECT '+
      'B.TIME_ID AS TIME_ID, '+
      'P.G_PRD_ID AS PRD_ID, '+
      ' '+V_MMA+' '+
      'BIT_OR_AGGT(B.SALES_STORE_NUM) AS SALES_STORE_NUM, '+
      'BIT_OR_AGGT(B.ORDER_STORE_NUM) AS ORDER_STORE_NUM, '+
      'BIT_OR_AGGT(B.THROW_STORE_NUM) AS THROW_STORE_NUM, '+
      'BIT_OR_AGGT(B.INPRD_STORE_NUM) AS INPRD_STORE_NUM, '+
      'BIT_OR_AGGT(B.RETPRD_STORE_NUM) AS RETPRD_STORE_NUM, '+
      'BIT_OR_AGGT(B.TRANSPRD_STORE_NUM) AS TRANSPRD_STORE_NUM, '+
      'BIT_OR_AGGT(B.TRANSPRD_IN_STORE_NUM) AS TRANSPRD_IN_STORE_NUM, '+
      'BIT_OR_AGGT(B.Y_ORDER_STORE_NUM) AS Y_ORDER_STORE_NUM '+
      'FROM '+ V_TABLE_NAME +' B , ('+ V_TABLE_NAME2 +') P '+
      ' WHERE B.TIME_ID IN ('+ FP_TIME_LIST +') '+
      ' AND B.PRD_ID = P.I_PRD_ID '+
      ' AND P.I_PRD_ID IN ('+ V_PRD_IN +') '+  V_MMA_WHERE +
      ' GROUP BY B.TIME_ID,P.G_PRD_ID '+V_MMA_GROUP +' '+
      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    END IF;
  DELETE FROM PMART.T1 WHERE F1 = 10;
  INSERT INTO PMART.T1(F1,F2) SELECT 10,SQLSTR;		  
  EXECUTE IMMEDIATE SQLSTR;
END SP;