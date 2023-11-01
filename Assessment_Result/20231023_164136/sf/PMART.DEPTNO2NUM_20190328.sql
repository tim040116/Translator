REPLACE FUNCTION PMART.DEPTNO2NUM_20190328(P_DEPT_NO VARCHAR(4), P_DEPT_LEVEL INTEGER)
   RETURNS INTEGER
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CASE WHEN (SUBSTRING(P_DEPT_NO,1,1)='S') THEN
   								CASE  WHEN P_DEPT_LEVEL = 1 THEN 1000000
                                           		WHEN P_DEPT_LEVEL = 2 THEN 2000000
                                           		WHEN P_DEPT_LEVEL = 4 THEN 30000000 
								           		WHEN P_DEPT_LEVEL = 5 THEN 999000000 
										   		ELSE NULL END
   							WHEN (SUBSTRING(P_DEPT_NO,1,1)='M' AND SUBSTRING(P_DEPT_NO,2,1) IN ('0','1','2','3','4','5','6','7','8','9')) THEN
						   			CASE  WHEN P_DEPT_LEVEL = 6 THEN 3000000
                                           			WHEN P_DEPT_LEVEL = 7 THEN 4000000                                         
										   			ELSE NULL END
						    WHEN (SUBSTRING(P_DEPT_NO,1,1)='Q' AND SUBSTRING(P_DEPT_NO,2,1) IN ('0','1','2','3','4','5','6','7','8','9')) THEN
						   			CASE  WHEN P_DEPT_LEVEL = 6 THEN 3100000
                                           			WHEN P_DEPT_LEVEL = 7 THEN 4100000                                           
										   			ELSE NULL END
							WHEN (SUBSTRING(P_DEPT_NO,1,1)='N' AND SUBSTRING(P_DEPT_NO,2,1) IN ('0','1','2','3','4','5','6','7','8','9')) THEN
						   			CASE  WHEN P_DEPT_LEVEL = 6 THEN 3200000
                                           			WHEN P_DEPT_LEVEL = 7 THEN 4200000                                           
										   			ELSE NULL END
							ELSE NULL END 
				       + 
					   CASE WHEN ASCII(SUBSTRING(P_DEPT_NO,2,1)) >= 49  AND ASCII(SUBSTRING(P_DEPT_NO,2,1)) <= 57 THEN     
					               10000 * CAST(SUBSTRING(P_DEPT_NO,2,1) AS INTEGER)
					               WHEN CAST(ASCII(SUBSTRING(P_DEPT_NO,2,1)) AS INTEGER) >= CAST( 65 AS INTEGER) THEN        
					               10000 * CAST(ASCII(SUBSTRING(P_DEPT_NO,2,1))-55 AS INTEGER)								   
					   END
				       +
					   CASE WHEN ASCII(SUBSTRING(P_DEPT_NO,3,1)) >= 49  AND ASCII(SUBSTRING(P_DEPT_NO,3,1)) <= 57 THEN     
					               100 * CAST(SUBSTRING(P_DEPT_NO,3,1) AS INTEGER)
					               WHEN ASCII(SUBSTRING(P_DEPT_NO,3,1)) >= 65 THEN                                                                                 
					               100 * CAST(ASCII(SUBSTRING(P_DEPT_NO,3,1))-55 AS INTEGER)								   
					   END					   
					   +
					   CASE WHEN ASCII(SUBSTRING(P_DEPT_NO,4,1)) >= 48  AND ASCII(SUBSTRING(P_DEPT_NO,4,1)) <= 57 THEN     
					               CAST(SUBSTRING(P_DEPT_NO,4,1) AS INTEGER)
					               WHEN ASCII(SUBSTRING(P_DEPT_NO,4,1)) >= 65 THEN                                                                                 
					               CAST(ASCII(SUBSTRING(P_DEPT_NO,4,1))-55 AS INTEGER)								   
					   END;