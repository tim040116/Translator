REPLACE FUNCTION PMART.D_INT2DATE(P_INPUT_TYPE VARCHAR(1), P_INT INTEGER)
   RETURNS DATE
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CASE  WHEN P_INPUT_TYPE = 'A' THEN CAST(SUBSTRING(TRIM(P_INT),1,4)+'/'+SUBSTRING(TRIM(P_INT),5,2)+'/'+SUBSTRING(TRIM(P_INT),7,2) AS DATE FORMAT 'YYYY/MM/DD')
                                          WHEN P_INPUT_TYPE = 'C' THEN CAST(TO_NUMBER(SUBSTRING(LPAD(TRIM(P_INT), 7, '0'),1,3)) + 1911+'/'+SUBSTRING(LPAD(TRIM(P_INT), 7, '0'),4,2)+'/'+SUBSTRING(LPAD(TRIM(P_INT), 7, '0'),6,2) AS DATE FORMAT 'YYYY/MM/DD')
										  END;