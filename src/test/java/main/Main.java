package main;

import java.text.SimpleDateFormat;
import java.util.*;
import etec.common.utils.*;
import etec.main.ParamsFactory;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\familymart\\T1\\SQLAExport.txt";
	
	public static void main(String[] args) {
		try {
			ParamsFactory.init();
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			//讀檔
			String text = "REPLACE PROCEDURE PMART.BI_REMD_RESPON_FUNC(P_WEEK_ID INTEGER, P_BRANCH_ID INTEGER)\r\n" + 
					"SQL SECURITY INVOKER\r\n" + 
					"SP:BEGIN\r\n" + 
					"\r\n" + 
					"   DECLARE SQLSTR VARCHAR(1000);\r\n" + 
					"   DECLARE V_SQLA VARCHAR(3000);\r\n" + 
					"   DECLARE V_SQLB VARCHAR(3000);\r\n" + 
					"   DECLARE V_SQLC VARCHAR(3000);\r\n" + 
					"\r\n" + 
					"   DECLARE V_DAY_ID	INTEGER;\r\n" + 
					"   DECLARE V_ORG_ID	INTEGER;\r\n" + 
					"   DECLARE V_WEEK_ID INTEGER;\r\n" + 
					"   DECLARE V_UPLOAD_STNUM DECIMAL(18,6);\r\n" + 
					"   DECLARE V_AMT DECIMAL(18,6);\r\n" + 
					"   DECLARE V_CUST_NUM DECIMAL(18,6);\r\n" + 
					"   DECLARE P_ORG_ID	DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_UPLOAD_STNUM DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_AMT DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_CUST_NUM DECIMAL(18,6);  \r\n" + 
					"\r\n" + 
					"   DECLARE V_DAY_ID_C1 INTEGER;\r\n" + 
					"   DECLARE V_ORG_ID_C1 INTEGER;\r\n" + 
					"   DECLARE V_WEEK_ID_C1 INTEGER;\r\n" + 
					"   DECLARE V_AMT_C1 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_CUST_NUM_C1 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_CUST_AMT_C1 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_AMT_C1 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_CUST_NUM_C1 DECIMAL(18,6);\r\n" + 
					"\r\n" + 
					"   DECLARE V_DAY_ID_C2 INTEGER;\r\n" + 
					"   DECLARE V_ORG_ID_C2 INTEGER;\r\n" + 
					"   DECLARE V_WEEK_ID_C2 INTEGER;\r\n" + 
					"   DECLARE V_AMT_C2 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_CUST_NUM_C2 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_CUST_AMT_C2 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_AMT_C2 DECIMAL(18,6);\r\n" + 
					"   DECLARE V_P_CUST_NUM_C2 DECIMAL(18,6);\r\n" + 
					"\r\n" + 
					"   DECLARE REMD_CS CURSOR FOR REMD_SQL;\r\n" + 
					"\r\n" + 
					"   CALL PMART.P_DROP_TABLE ('#VT_BI_REMD_RESPON_FUNC'); \r\n" + 
					"   SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_BI_REMD_RESPON_FUNC  \r\n" + 
					"    (    \r\n" + 
					"      L_DAY_ID INTEGER, ORG_ID INTEGER, L_WEEK_ID INTEGER, AMT DECIMAL(18,6), CUST_NUM DECIMAL(18,6), CUST_AMT DECIMAL(18,6), P_AMT DECIMAL(18,6), P_CUST_NUM DECIMAL(18,6)\r\n" + 
					"    )\r\n" + 
					"   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';\r\n" + 
					"   EXECUTE IMMEDIATE SQLSTR;\r\n" + 
					"\r\n" + 
					"   SET SQLSTR = 'SELECT A.L_DAY_ID, A.ORG_ID AS ORG_ID, C.L_WEEK_ID AS L_WEEK_ID, '+\r\n" + 
					"       '  DECODE(A.UPLOAD_STNUM, NULL, 0, A.UPLOAD_STNUM) AS UPLOAD_STNUM, A.AMT, '+\r\n" + 
					"       '  A.CUST_NUM AS CUST_NUM, DECODE(D.UPLOAD_STNUM,NULL,0,D.UPLOAD_STNUM)AS P_UPLOAD_STNUM, '+\r\n" + 
					"       '   DECODE(D.AMT,NULL,0,D.AMT)AS P_AMT, '+\r\n" + 
					"	   '   DECODE(D.CUST_NUM,NULL,0,D.CUST_NUM)AS P_CUST_NUM  '+\r\n" + 
					"       '  FROM PMART.REMD_FACT_SUM A INNER JOIN (SELECT DISTINCT RESPON_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID = '+ P_BRANCH_ID + ') B '+\r\n" + 
					"       '  ON A.ORG_ID = B.RESPON_ID '+\r\n" + 
					"       '                             INNER JOIN  PMART.YMWD_TIME C '+\r\n" + 
					"       '  ON A.L_DAY_ID = C.L_DAY_ID '+\r\n" + 
					"       '                             LEFT OUTER JOIN  PMART.REMD_FACT_SUM D '+\r\n" + 
					"       '  ON D.L_DAY_ID = C.L_DAY_LAST_YEAR  '+\r\n" + 
					"       ' AND D.ORG_ID = A.ORG_ID  '+\r\n" + 
					"       '  WHERE C.L_WEEK_ID = '+ P_WEEK_ID + \r\n" + 
					"       '  ORDER BY A.ORG_ID, A.L_DAY_ID; ';\r\n" + 
					"\r\n" + 
					"   PREPARE REMD_SQL FROM SQLSTR;\r\n" + 
					"   OPEN REMD_CS; \r\n" + 
					"\r\n" + 
					"   SET P_ORG_ID = 0;\r\n" + 
					"   L1:\r\n" + 
					"   WHILE (SQLCODE=0)	\r\n" + 
					"   DO\r\n" + 
					"    BEGIN \r\n" + 
					"	FETCH REMD_CS INTO V_DAY_ID, V_ORG_ID, V_WEEK_ID, V_UPLOAD_STNUM, V_AMT, V_CUST_NUM, V_P_UPLOAD_STNUM, V_P_AMT, V_P_CUST_NUM;\r\n" + 
					"		IF SQLSTATE <> '00000' THEN LEAVE L1; END IF;\r\n" + 
					"		IF P_ORG_ID <>  V_ORG_ID THEN\r\n" + 
					"			IF P_ORG_ID <> 0 THEN \r\n" + 
					"				L2 :\r\n" + 
					"				FOR CUR2 AS BI_REMD_RESPON_FUNC_C2 CURSOR FOR 				\r\n" + 
					"				SELECT A.L_DAY_ID, A.ORG_ID, A.L_WEEK_ID, A.AMT, A.CUST_NUM, A.CUST_AMT, \r\n" + 
					"				 (A.AMT * 100 / B.AMT ) AS P_AMT\r\n" + 
					"				 , (A.CUST_NUM * 100 / B.CUST_NUM ) AS P_CUST_NUM \r\n" + 
					"				 FROM ( \r\n" + 
					"				 SELECT 99991231 AS L_DAY_ID, A.ORG_ID, B.L_WEEK_ID, \r\n" + 
					"				 CAST(SUM(A.AMT) AS DECIMAL(18,6)) / SUM(A.UPLOAD_STNUM) AS AMT,  \r\n" + 
					"				 CAST(SUM(A.CUST_NUM) AS DECIMAL(18,6))  / SUM(A.UPLOAD_STNUM) AS CUST_NUM,  \r\n" + 
					"				 CAST(SUM(A.AMT) AS DECIMAL(18,6)) / SUM(A.CUST_NUM) AS CUST_AMT  \r\n" + 
					"				 FROM PMART.REMD_FACT_SUM A, PMART.YMWD_TIME B \r\n" + 
					"				 WHERE A.ORG_ID = P_ORG_ID\r\n" + 
					"				 AND B.L_WEEK_ID = V_WEEK_ID\r\n" + 
					"				 AND A.L_DAY_ID = B.L_DAY_ID GROUP BY A.ORG_ID, B.L_WEEK_ID) A  \r\n" + 
					"				 LEFT JOIN (  \r\n" + 
					"				 	SELECT 99991231 AS L_DAY_ID, A.ORG_ID, \r\n" + 
					"						   CAST(SUM(A.AMT) AS DECIMAL(18,6)) / SUM(A.UPLOAD_STNUM) AS AMT,  \r\n" + 
					"						   CAST(SUM(A.CUST_NUM) AS DECIMAL(18,6)) / SUM(A.UPLOAD_STNUM) AS CUST_NUM  \r\n" + 
					"					  FROM PMART.REMD_FACT_SUM A, PMART.YMWD_TIME B  \r\n" + 
					"					 WHERE A.ORG_ID = P_ORG_ID\r\n" + 
					"					   AND B.L_WEEK_ID = V_WEEK_ID - 100\r\n" + 
					"					   AND A.L_DAY_ID = B.L_DAY_ID \r\n" + 
					"					 GROUP BY A.ORG_ID, B.L_WEEK_ID) B ON A.L_DAY_ID = B.L_DAY_ID AND A.ORG_ID = B.ORG_ID \r\n" + 
					"\r\n" + 
					"				DO\r\n" + 
					"					SET V_DAY_ID_C2 = CUR2.L_DAY_ID;\r\n" + 
					"					SET V_ORG_ID_C2 = CUR2.ORG_ID;\r\n" + 
					"					SET V_WEEK_ID_C2 = CUR2.L_WEEK_ID;\r\n" + 
					"					SET V_AMT_C2 = CUR2.AMT;\r\n" + 
					"					SET V_CUST_NUM_C2 = CUR2.CUST_NUM;\r\n" + 
					"					SET V_CUST_AMT_C2 = CUR2.CUST_AMT;	\r\n" + 
					"					SET V_P_AMT_C2 = DECODE(CUR2.P_AMT, NULL, 0, CUR2.P_AMT) ;\r\n" + 
					"					SET V_P_CUST_NUM_C2 = DECODE(CUR2.P_CUST_NUM, NULL, 0, CUR2.P_CUST_NUM) ;\r\n" + 
					"				INSERT INTO #VT_BI_REMD_RESPON_FUNC (L_DAY_ID, ORG_ID, L_WEEK_ID, AMT, CUST_NUM, CUST_AMT, P_AMT ,P_CUST_NUM) VALUES (V_DAY_ID_C2, V_ORG_ID_C2, V_WEEK_ID_C2, V_AMT_C2, V_CUST_NUM_C2, V_CUST_AMT_C2, V_P_AMT_C2, V_P_CUST_NUM_C2);\r\n" + 
					"				END FOR L2;\r\n" + 
					"			END IF;\r\n" + 
					"			SET P_ORG_ID = V_ORG_ID;\r\n" + 
					"		END IF;\r\n" + 
					"		SET V_DAY_ID_C1 = V_DAY_ID;\r\n" + 
					"		SET V_ORG_ID_C1 = V_ORG_ID;\r\n" + 
					"		SET V_WEEK_ID_C1= V_WEEK_ID;\r\n" + 
					"		IF V_UPLOAD_STNUM = 0 THEN \r\n" + 
					"		   SET V_AMT_C1 = 0 ;\r\n" + 
					"		   SET V_CUST_NUM_C1 = 0 ; \r\n" + 
					"		ELSE\r\n" + 
					"		   SET V_AMT_C1 = V_AMT/V_UPLOAD_STNUM;\r\n" + 
					"		   SET V_CUST_NUM_C1 = V_CUST_NUM/V_UPLOAD_STNUM;\r\n" + 
					"		END IF ; \r\n" + 
					"        IF V_CUST_NUM = 0 THEN\r\n" + 
					"		   SET V_CUST_AMT_C1 = 0 ;\r\n" + 
					"	    ELSE\r\n" + 
					"		   SET V_CUST_AMT_C1 = V_AMT/V_CUST_NUM;\r\n" + 
					"		END IF ; \r\n" + 
					"\r\n" + 
					"		IF V_P_UPLOAD_STNUM = 0 THEN \r\n" + 
					"		   SET V_P_AMT_C1 = 0 ;\r\n" + 
					"	    ELSE\r\n" + 
					"		   IF V_AMT_C1  = 0 THEN\r\n" + 
					"		       SET V_P_AMT_C1 = 0 ;\r\n" + 
					"		   ELSE\r\n" + 
					"		      SET V_P_AMT_C1 = (V_AMT/V_UPLOAD_STNUM)*100/(V_P_AMT/V_P_UPLOAD_STNUM);\r\n" + 
					"		   END IF;	  \r\n" + 
					"		END IF ;   \r\n" + 
					"\r\n" + 
					"		IF V_P_UPLOAD_STNUM = 0 THEN \r\n" + 
					"		   SET V_P_CUST_NUM_C1 = 0 ;\r\n" + 
					"	    ELSE\r\n" + 
					"		   IF V_CUST_NUM_C1  = 0 THEN\r\n" + 
					"		       SET V_P_CUST_NUM_C1 = 0 ;\r\n" + 
					"		   ELSE\r\n" + 
					"		      SET V_P_CUST_NUM_C1 = (V_CUST_NUM/V_UPLOAD_STNUM)*100/(V_P_CUST_NUM/V_P_UPLOAD_STNUM);\r\n" + 
					"		   END IF;	  \r\n" + 
					"		END IF ;   		\r\n" + 
					"\r\n" + 
					"		INSERT INTO #VT_BI_REMD_RESPON_FUNC (L_DAY_ID, ORG_ID, L_WEEK_ID, AMT, CUST_NUM, CUST_AMT, P_AMT ,P_CUST_NUM) VALUES (V_DAY_ID_C1, V_ORG_ID_C1, V_WEEK_ID_C1, V_AMT_C1, V_CUST_NUM_C1, V_CUST_AMT_C1, V_P_AMT_C1, V_P_CUST_NUM_C1);\r\n" + 
					"	END;\r\n" + 
					"\r\n" + 
					"   END WHILE L1;\r\n" + 
					"	L3:\r\n" + 
					"	FOR CUR3 AS BI_REMD_RESPON_FUNC_C3 CURSOR FOR 				\r\n" + 
					"	SELECT A.L_DAY_ID, A.ORG_ID, A.L_WEEK_ID, A.AMT, A.CUST_NUM, A.CUST_AMT, \r\n" + 
					"	 (A.AMT * 100 / B.AMT ) AS P_AMT, (A.CUST_NUM * 100 / B.CUST_NUM ) AS P_CUST_NUM \r\n" + 
					"	 FROM ( \r\n" + 
					"	 SELECT 99991231 AS L_DAY_ID, A.ORG_ID, B.L_WEEK_ID, \r\n" + 
					"	 CAST(SUM(A.AMT) AS DECIMAL(18,6))  / SUM(A.UPLOAD_STNUM) AS AMT,  \r\n" + 
					"	 CAST(SUM(A.CUST_NUM) AS DECIMAL(18,6))  / SUM(A.UPLOAD_STNUM) AS CUST_NUM,  \r\n" + 
					"	 CAST(SUM(A.AMT) AS DECIMAL(18,6))  / SUM(A.CUST_NUM) AS CUST_AMT  \r\n" + 
					"	 FROM PMART.REMD_FACT_SUM A, PMART.YMWD_TIME B \r\n" + 
					"	 WHERE A.ORG_ID = P_ORG_ID\r\n" + 
					"	 AND B.L_WEEK_ID = V_WEEK_ID\r\n" + 
					"	 AND A.L_DAY_ID = B.L_DAY_ID GROUP BY A.ORG_ID, B.L_WEEK_ID) A  \r\n" + 
					"	 LEFT JOIN (  \r\n" + 
					"		SELECT 99991231 AS L_DAY_ID, A.ORG_ID, \r\n" + 
					"			   CAST(SUM(A.AMT) AS DECIMAL(18,6))  / SUM(A.UPLOAD_STNUM) AS AMT,  \r\n" + 
					"			   CAST(SUM(A.CUST_NUM) AS DECIMAL(18,6))  / SUM(A.UPLOAD_STNUM) AS CUST_NUM  \r\n" + 
					"		  FROM PMART.REMD_FACT_SUM A, PMART.YMWD_TIME B  \r\n" + 
					"		 WHERE A.ORG_ID = P_ORG_ID\r\n" + 
					"		   AND B.L_WEEK_ID = V_WEEK_ID - 100\r\n" + 
					"		   AND A.L_DAY_ID = B.L_DAY_ID \r\n" + 
					"		 GROUP BY A.ORG_ID, B.L_WEEK_ID) B ON A.L_DAY_ID = B.L_DAY_ID AND A.ORG_ID = B.ORG_ID \r\n" + 
					"	DO\r\n" + 
					"		SET V_DAY_ID_C2 = CUR3.L_DAY_ID;\r\n" + 
					"		SET V_ORG_ID_C2 = CUR3.ORG_ID;\r\n" + 
					"		SET V_WEEK_ID_C2 = CUR3.L_WEEK_ID;\r\n" + 
					"		SET V_AMT_C2 = CUR3.AMT;\r\n" + 
					"		SET V_CUST_NUM_C2 = CUR3.CUST_NUM;\r\n" + 
					"		SET V_CUST_AMT_C2 = CUR3.CUST_AMT;\r\n" + 
					"		SET V_P_AMT_C2 = DECODE(CUR3.P_AMT, NULL, 0, CUR3.P_AMT) ;\r\n" + 
					"		SET V_P_CUST_NUM_C2 = DECODE(CUR3.P_CUST_NUM, NULL, 0, CUR3.P_CUST_NUM) ;\r\n" + 
					"	INSERT INTO #VT_BI_REMD_RESPON_FUNC (L_DAY_ID, ORG_ID, L_WEEK_ID, AMT, CUST_NUM, CUST_AMT, P_AMT ,P_CUST_NUM) VALUES (V_DAY_ID_C2, V_ORG_ID_C2, V_WEEK_ID_C2, V_AMT_C2, V_CUST_NUM_C2, V_CUST_AMT_C2, V_P_AMT_C2, V_P_CUST_NUM_C2);\r\n" + 
					"	END FOR L3;\r\n" + 
					"   CLOSE REMD_CS;\r\n" + 
					"END SP;";
			List<String> lst = RegexTool.getRegexTarget("(?<=DECLARE )\\S+", text);
			System.out.println("");
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
