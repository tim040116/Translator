REPLACE FUNCTION PMART.TRANS_AMT_MASK_ADD(COUNT1 INTEGER)
   RETURNS  INTEGER
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CASE 
   WHEN  COUNT1=0  OR COUNT1=3  OR  COUNT1=4
      THEN   2
	    WHEN COUNT1=1  OR COUNT1=2  OR  COUNT1=5
            THEN 1
   	       			 ELSE 
                      0
			 END;