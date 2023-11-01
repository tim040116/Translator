REPLACE FUNCTION PMART.CONVERT_STRING_LIST(P_ITEM_LIST VARCHAR(2000))
   RETURNS VARCHAR(2000)
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CASE 
                           WHEN ( CAST( INSTR(P_ITEM_LIST,',',1)  AS INTEGER )>0   AND CAST(INSTR(LOWER(P_ITEM_LIST),'SELECT')  AS INTEGER)=0 ) THEN
                           '''' + Replace(  Replace( Replace(  Replace(P_ITEM_LIST,'''', ''),  ',', ''','''), ''' ', ''''), ' ''', '''') + ''''
						   WHEN ( CAST( INSTR(P_ITEM_LIST,',',1)  AS INTEGER )=0  AND CAST(INSTR(LOWER(P_ITEM_LIST),'SELECT')  AS INTEGER)=0 ) THEN
						   '''' + Replace(TRIM(P_ITEM_LIST),'''', '') + ''''
							ELSE 
							P_ITEM_LIST
						    END;