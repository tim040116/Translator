REPLACE FUNCTION PMART.BASIC_TIME_GET_WEATHER_NM(W1 INTEGER ,W2 INTEGER,W3 INTEGER,W4 INTEGER,W5 INTEGER,W6 INTEGER,W7 INTEGER,W8 INTEGER,W9 INTEGER)
RETURNS VARCHAR(20)
LANGUAGE SQL 
DETERMINISTIC 
CONTAINS SQL 
CALLED ON NULL INPUT
SQL SECURITY DEFINER
COLLATION INVOKER
INLINE TYPE 1 
RETURN
CASE 
    WHEN (W1 IS NULL AND W2 IS NULL AND W3 IS NULL AND W4 IS NULL AND W5 IS NULL AND W6 IS NULL AND W7 IS NULL AND W8 IS NULL AND W9 IS NULL) THEN
	     NULL
     WHEN  (W1=0 AND W2=0 AND W3=0 AND W4=0 AND W5=0 AND W6=0 AND W7=0 AND W8=0 AND W9=0) THEN
         NULL
     WHEN W1= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN 
         ' '
     WHEN W2= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN 
         '    '         
     WHEN W3= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '    '
     WHEN W4= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN 
         '   '      
     WHEN W5= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '    '
     WHEN W6= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '  '           
     WHEN W7= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '    '
     WHEN W8= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '     '
     WHEN W9= GREATEST(NVL(W1,0),NVL(W2,0),NVL(W3,0),NVL(W4,0),NVL(W5,0),NVL(W6,0),NVL(W7,0),NVL(W8,0),NVL(W9,0)) THEN  
         '  '	 
  END;