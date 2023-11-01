REPLACE PROCEDURE PMART.PRD_RANK_FUNC (FP_L_WEEK_ID INTEGER,FP_ORG_ID INTEGER,FP_PRD_TYPE CHAR(1)  CASESPECIFIC,FP_PRD_LIST VARCHAR(5000))
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000) ;    
DECLARE V_PRD_SQL VARCHAR(12000) ;
DECLARE V_TEMP_PRD_SQL VARCHAR(12000) ;
DECLARE V_L_WEEK_SERIAL INTEGER;
DECLARE V_WEEK_ID1  INTEGER ;
DECLARE V_WEEK_ID2  INTEGER;
DECLARE V_WEEK_ID3  INTEGER;
DECLARE V_TABLE_NAME  VARCHAR(100);
DECLARE V_TABLE_NAME1 VARCHAR(100);
DECLARE V_TABLE_NAME2 VARCHAR(100);
DECLARE V_ORG_ID VARCHAR(20) DEFAULT'';
   CALL PMART.P_DROP_TABLE ('#VT_PRD_RANK_FUNC');      
   CALL PMART.P_DROP_TABLE('#VT_PRD_TEMP');
	CREATE VOLATILE TABLE #VT_PRD_TEMP  ,
,
,
,
     DEFAULT MERGEBLOCKRATIO
     (
		FM_CODE	VARCHAR(7)			
	 )
    NO PRIMARY INDEX  ON COMMIT PRESERVE ROWS;
      IF FP_PRD_TYPE='P' THEN
         SET V_TEMP_PRD_SQL =
	     'INSERT INTO #VT_PRD_TEMP '+
         'SELECT A1.PRD_ID '+
         'FROM PMART.PRD_DIM A1, PDATA.PBMCMDT A2 '+
         'WHERE A1.PRD_ID IN ('+ FP_PRD_LIST + ') AND A1.PRD_ID = TO_NUMBER(A2.FM_CODE) ';
   ELSE
         SET V_TEMP_PRD_SQL =
		 'INSERT INTO #VT_PRD_TEMP '+
         'SELECT DISTINCT A1.LINK_ID AS PRD_ID '+
         'FROM PMART.PRD_LINK_DIM A1, PDATA.PBMCMDT A2 '+
         'WHERE A1.LINK_ID IN ('+ FP_PRD_LIST + ') AND A1.PRD_ID = TO_NUMBER(A2.FM_CODE)';
   END IF;
   EXECUTE IMMEDIATE V_TEMP_PRD_SQL;
   SET V_ORG_ID = TRIM(TO_CHAR(FP_ORG_ID));   
   SET V_WEEK_ID1 = FP_L_WEEK_ID;
   SELECT DISTINCT L_WEEK_SERIAL INTO V_L_WEEK_SERIAL FROM PMART.YMWD_TIME
      WHERE L_WEEK_ID=FP_L_WEEK_ID;
   SELECT DISTINCT L_WEEK_ID INTO V_WEEK_ID2 FROM PMART.YMWD_TIME
      WHERE L_WEEK_SERIAL=V_L_WEEK_SERIAL-1;
   SELECT DISTINCT L_WEEK_ID INTO V_WEEK_ID3 FROM PMART.YMWD_TIME
      WHERE L_WEEK_SERIAL=V_L_WEEK_SERIAL-2;
    SET V_L_WEEK_SERIAL = NVL(TRIM(V_L_WEEK_SERIAL),'0');
    SET V_WEEK_ID2 = NVL(TRIM(V_WEEK_ID2),'0');
    SET V_WEEK_ID3 = NVL(TRIM(V_WEEK_ID3),'0');
   IF FP_PRD_TYPE='P' THEN
      SET V_PRD_SQL =
         'SELECT A1.PRD_ID,A1.PRD_SLUNT,A1.PRD_CSUNT, TO_NUMBER(CASE WHEN A2.REAL_CODE IS NULL THEN 0 ELSE A2.REAL_CODE END) AS RTNDYS '+  
         'FROM PMART.PRD_DIM A1, PDATA.PBMCMDT A2 '+
         'WHERE A1.PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) AND A1.PRD_ID = TO_NUMBER(A2.FM_CODE) ';
      SET V_TABLE_NAME = 'MFACT';
      SET V_TABLE_NAME1 = 'STFACT';
      SET V_TABLE_NAME2 = 'PMART.BASIC_STFACT_DETAIL';
   ELSE
            SET V_PRD_SQL =
         'SELECT DISTINCT A1.LINK_ID AS PRD_ID,A1.LINK_SLUNT AS PRD_SLUNT,'+
         'A1.LINK_CSUNT AS PRD_CSUNT, TO_NUMBER(CASE WHEN A2.REAL_CODE IS NULL THEN 0 ELSE A2.REAL_CODE END) AS RTNDYS '+
         'FROM PMART.PRD_LINK_DIM A1, PDATA.PBMCMDT A2 '+
         'WHERE A1.LINK_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) AND A1.PRD_ID = TO_NUMBER(A2.FM_CODE)';
      SET V_TABLE_NAME = 'MLFACT';
      SET V_TABLE_NAME1 = 'STLFACT';
      SET V_TABLE_NAME2 = 'PMART.BASIC_STLFACT';
   END IF;
      SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_PRD_RANK_FUNC AS('+
                   'SELECT '+
                       '-1 AS TOT_ID, '+
                       'A.PRD_ID AS PRD_ID, '+
                       'RANK() OVER(ORDER BY DECODE(H.SALES_CNT,NULL,0,H.SALES_CNT) DESC NULLS LAST) '+
                       'AS TOP_RANK,'+
                       'RANK() OVER(ORDER BY DECODE(B.SALES_CNT,NULL,0,B.SALES_CNT) DESC NULLS LAST) '+
                       'AS LEVEL_RANK, '+
                       'CASE '+
                       'WHEN A.RTNDYS <> 10 THEN (A.PRD_SLUNT-A.PRD_CSUNT*1.05)*B.SALES_CNT '+
                       'WHEN A.RTNDYS =  10 THEN B.SALES_AMT - (B.SALES_AMT * (A.PRD_CSUNT/A.PRD_SLUNT)) '+ 
                       'END AS NET_PROFIT, '+
                       'CAST(B.SALES_CNT AS DECIMAL(18,6))/CAST(DECODE(B.ORDER_CNT,0,NULL,B.ORDER_CNT) AS DECIMAL(18,6))*100 AS OS_RATIO,'+
                       'B.SALES_CNT AS M1_CNT, '+
                       'B.SALES_AMT AS M1_AMT, '+                      					                  
					   'CASE WHEN (CAST(B.SALES_CNT AS  DECIMAL(18,6))/CAST(DECODE(C.SALES_CNT,0,NULL,C.SALES_CNT) AS DECIMAL(18,6))*100)>=1000 THEN 999.99 '+
						'ELSE (CAST(B.SALES_CNT AS DECIMAL(18,6))/CAST(DECODE(C.SALES_CNT,0,NULL,C.SALES_CNT) AS DECIMAL(18,6))*100) END AS M2_CNT,  '+
						'CASE WHEN (CAST(B.SALES_AMT AS DECIMAL(18,6))/CAST(DECODE(C.SALES_AMT,0,NULL,C.SALES_AMT) AS DECIMAL(18,6))*100)>=1000 THEN 999.99  '+
						'ELSE (CAST(B.SALES_AMT AS DECIMAL(18,6))/CAST(DECODE(C.SALES_AMT,0,NULL,C.SALES_AMT) AS DECIMAL(18,6))*100) END AS M2_AMT,  '+
						'CASE WHEN (CAST(B.SALES_CNT AS DECIMAL(18,6))/CAST(DECODE(D.SALES_CNT,0,NULL,D.SALES_CNT) AS DECIMAL(18,6))*100)>=1000 THEN 999.99  '+
						'ELSE (CAST(B.SALES_CNT AS DECIMAL(18,6))/CAST(DECODE(D.SALES_CNT,0,NULL,D.SALES_CNT) AS DECIMAL(18,6))*100) END AS M3_CNT,  '+
						'CASE WHEN (CAST(B.SALES_AMT AS DECIMAL(18,6))/CAST(DECODE(D.SALES_AMT,0,NULL,D.SALES_AMT) AS DECIMAL(18,6))*100)>=1000 THEN 999.99  '+
						'ELSE (CAST(B.SALES_AMT AS DECIMAL(18,6))/CAST(DECODE(D.SALES_AMT,0,NULL,D.SALES_AMT) AS DECIMAL(18,6))*100) END AS M3_AMT,  '+
				      'B.SALES_CNT/DECODE(E.SALES_ORDER_STORE_NUM,0,NULL,E.SALES_ORDER_STORE_NUM) '+	   
                       'AS M4_CNT, '+
                       'B.SALES_AMT/DECODE(E.SALES_ORDER_STORE_NUM,0,NULL,E.SALES_ORDER_STORE_NUM) '+
                       'AS M4_AMT, '+
                       'B.SALES_CNT/'+
                       'DECODE(G.SALES_ORDER_STORE_NUM,0,NULL,G.SALES_ORDER_STORE_NUM) '+
                       'AS M5_CNT, '+
                       'B.SALES_AMT/'+
                       'DECODE(G.SALES_ORDER_STORE_NUM,0,NULL,G.SALES_ORDER_STORE_NUM) '+
                       'AS M5_AMT,'+
                       'E.SALES_STORE_NUM/'+
                       'DECODE(I.MAST_STORE_NUM,0,NULL,I.MAST_STORE_NUM)*100 '+
                       'AS M6, '+
                       'F.SALES_STORE_NUM/'+
                       'DECODE(I.MAST_STORE_NUM2,0,NULL,I.MAST_STORE_NUM2)*100 '+
                       'AS M7, '+
                       'E.SALES_STORE_NUM AS M8, '+
                       'E.UNSALES_STORE_NUM AS M9 '+
                       'FROM  '+
                       '('+ V_PRD_SQL + ') A '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT  '+
                       'PRD_ID,SALES_CNT,SALES_AMT,ORDER_CNT '+
                       'FROM  PMART.BASIC_'+ V_TABLE_NAME +' WHERE TIME_ID='+ V_WEEK_ID1 +' '+
                       'AND ORG_ID='+ FP_ORG_ID + ' '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ') B ON (A.PRD_ID=B.PRD_ID) '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT  '+
                       'PRD_ID,SALES_CNT,SALES_AMT '+
                       'FROM  PMART.BASIC_'+ V_TABLE_NAME +' WHERE TIME_ID='+ V_WEEK_ID2 + ' ' +
                       'AND ORG_ID='+ FP_ORG_ID + ' '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ') C ON (A.PRD_ID=C.PRD_ID) '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT  '+
                       'PRD_ID,SALES_CNT,SALES_AMT '+
                       'FROM  PMART.BASIC_'+ V_TABLE_NAME +' WHERE TIME_ID='+ V_WEEK_ID3 +' '+
                       'AND ORG_ID='+ FP_ORG_ID + ' '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ') D ON (A.PRD_ID=D.PRD_ID) '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT '+
                       'E1.PRD_ID AS PRD_ID, '+
                       'CAST(BIT_EXTRACT(BIT_AND(E1.SALES_STORE_NUM,E2.MASK)) AS DECIMAL(18,6)) AS SALES_STORE_NUM, '+
                       'CAST(BIT_EXTRACT(BIT_AND(BIT_AND(BIT_REVERSE(E1.SALES_STORE_NUM),E3.MAST_STORE_NUM),E2.MASK)) AS DECIMAL(18,6)) AS UNSALES_STORE_NUM, '+
                       'CAST(BIT_EXTRACT(BIT_AND(BIT_OR(E1.SALES_STORE_NUM,E1.ORDER_STORE_NUM),E2.MASK)) AS DECIMAL(18,6)) AS SALES_ORDER_STORE_NUM '+
                       'FROM '+
                       '( '+
                       'SELECT  '+
                       'PRD_ID,SALES_STORE_NUM,ORDER_STORE_NUM '+
                       'FROM PMART.BASIC_'+ V_TABLE_NAME1 +' WHERE TIME_ID='+ V_WEEK_ID1 + ' '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ')E1, '+
                       '(SELECT MASK FROM PMART.LAST_ORG_DIM_MASK '+
                       'WHERE ORG_ID='+ FP_ORG_ID +') E2, '+
                       '(SELECT MAST_STORE_NUM FROM PMART.BASIC_OST_FACT '+
                       'WHERE TIME_ID='+ V_WEEK_ID1 +') E3 '+
                       ') E ON (A.PRD_ID=E.PRD_ID) '+
                       'LEFT JOIN  '+
                       '( '+
                       'SELECT '+
                       'F1.PRD_ID AS PRD_ID, '+
                       'CAST(BIT_EXTRACT(BIT_AND(F1.SALES_STORE_NUM,F2.MASK)) AS DECIMAL(18,6)) AS SALES_STORE_NUM '+
                       'FROM  '+
                       '( '+
                       'SELECT  '+
                       'PRD_ID,SALES_STORE_NUM  '+
                       'FROM PMART.BASIC_'+ V_TABLE_NAME1 +' WHERE TIME_ID='+ V_WEEK_ID2 + ' '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ')F1, '+
                       '(SELECT MASK FROM PMART.LAST_ORG_DIM_MASK '+
                       'WHERE ORG_ID='+ FP_ORG_ID +') F2 '+
                       ') F ON (A.PRD_ID=F.PRD_ID) '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT '+
                       'PRD_ID, '+
                       'SUM(SALES_ORDER_STORE_NUM) AS SALES_ORDER_STORE_NUM '+
                       'FROM '+
                       '( '+
                       'SELECT '+
                       'G1.PRD_ID AS PRD_ID, '+
                       'CAST(BIT_EXTRACT(BIT_AND(BIT_OR(G1.SALES_STORE_NUM,G1.ORDER_STORE_NUM),G2.MASK)) AS DECIMAL(18,6)) AS SALES_ORDER_STORE_NUM '+
                       'FROM '+ V_TABLE_NAME2 +' G1, '+
                       '(SELECT MASK FROM PMART.LAST_ORG_DIM_MASK '+
                       'WHERE ORG_ID='+ FP_ORG_ID +') G2 '+
                       'WHERE '+
                       'G1.TIME_ID IN (SELECT L_DAY_ID FROM PMART.YMWD_TIME '+
                       'WHERE L_WEEK_ID='+ V_WEEK_ID1 +') '+
                       'AND G1.PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ') G3 '+
                       'GROUP BY PRD_ID '+
                       ') G ON (A.PRD_ID=G.PRD_ID) '+
                       'LEFT JOIN '+
                       '( '+
                       'SELECT  PRD_ID,SALES_CNT '+
                       'FROM  PMART.BASIC_'+ V_TABLE_NAME +' WHERE TIME_ID='+ V_WEEK_ID1 + ' '+
                       'AND ORG_ID=-1 '+
                       'AND PRD_ID IN (SELECT FM_CODE FROM #VT_PRD_TEMP) '+
                       ') H ON (A.PRD_ID=H.PRD_ID) '+
                       ', '+
                       '( '+
                       'SELECT '+
                       'CAST(BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,C.MASK)) AS DECIMAL(18,6)) AS MAST_STORE_NUM, '+
                       'CAST(BIT_EXTRACT(BIT_AND(B.MAST_STORE_NUM,C.MASK)) AS DECIMAL(18,6)) AS MAST_STORE_NUM2 '+
                       'FROM '+
                       '( '+
                       'SELECT 1 AS JK,MAST_STORE_NUM '+
                       'FROM PMART.BASIC_MAST_FACT '+
                       'WHERE TIME_ID='+ V_WEEK_ID1 + ' '+
                       ')A, '+
                       '( '+
                       'SELECT 1 AS JK,MAST_STORE_NUM '+
                       'FROM PMART.BASIC_MAST_FACT '+
                       'WHERE TIME_ID='+ V_WEEK_ID2 + ' '+
                       ')B, '+
                       '(SELECT MASK FROM PMART.LAST_ORG_DIM_MASK '+
                       'WHERE ORG_ID='+ FP_ORG_ID + ') C '+
                       'WHERE A.JK=B.JK '+
                       ') I '+    
                   ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      EXECUTE IMMEDIATE SQLSTR;
	  CALL PMART.P_DROP_TABLE('#VT_PRD_TEMP');
END SP;