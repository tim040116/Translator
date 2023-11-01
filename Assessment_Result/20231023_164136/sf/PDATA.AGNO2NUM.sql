REPLACE FUNCTION PDATA.AGNO2NUM(P_AG_NO VARCHAR(3), P_LEVEL INTEGER)
   RETURNS INTEGER
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CAST (
	   		CASE 
				WHEN ASCII(SUBSTRING(P_AG_NO,1,1)) >= 48  AND ASCII(SUBSTRING(P_AG_NO,1,1)) <= 57 THEN
					'0' + SUBSTRING(P_AG_NO,1,1)
	   			WHEN ASCII(SUBSTRING(P_AG_NO,1,1)) >= 65 AND ASCII(SUBSTRING(P_AG_NO,1,1)) <= 90 THEN
					CAST(ASCII(SUBSTRING(P_AG_NO,1,1)) - 55 AS CHAR(2))
			END + 
	   		CASE 
				WHEN ASCII(SUBSTRING(P_AG_NO,2,1)) >= 48  AND ASCII(SUBSTRING(P_AG_NO,2,1)) <= 57 THEN
					'0' + SUBSTRING(P_AG_NO,2,1)
	   			WHEN ASCII(SUBSTRING(P_AG_NO,2,1)) >= 65 AND ASCII(SUBSTRING(P_AG_NO,2,1)) <= 90 THEN
					CAST(ASCII(SUBSTRING(P_AG_NO,2,1)) - 55 AS CHAR(2))
			END + 
			CASE 
				WHEN LENGTH(P_AG_NO) > 2 THEN
			   		CASE 
						WHEN ASCII(SUBSTRING(P_AG_NO,3,1)) >= 48  AND ASCII(SUBSTRING(P_AG_NO,3,1)) <= 57 THEN
							'0' + SUBSTRING(P_AG_NO,3,1)
			   			WHEN ASCII(SUBSTRING(P_AG_NO,3,1)) >= 65 AND ASCII(SUBSTRING(P_AG_NO,3,1)) <= 90 THEN
							CAST(ASCII(SUBSTRING(P_AG_NO,3,1)) - 55 AS CHAR(2))
					END
				ELSE
					''
			END AS INTEGER
		)+ 
		CASE P_LEVEL WHEN 1 THEN 0 WHEN 2 THEN 1000000 END;