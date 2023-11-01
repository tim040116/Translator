REPLACE PROCEDURE PMART.FUNC_FACT_MMK_ORDER_STATUS_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_ORDER_STATUS_TYPE VARCHAR(2)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000) ;
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_MMK_ORDER_STATUS_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_MMK_ORDER_STATUS_DIM ( '
						 +'ORDER_STATUS_ID VARCHAR(2), '
	                     +'ORDER_STATUS_NAME VARCHAR(20)   '
	                     +') UNIQUE PRIMARY INDEX(ORDER_STATUS_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    IF P_SHOWTYPE = 1 THEN 
	    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_MMK_ORDER_STATUS_DIM(ORDER_STATUS_ID,ORDER_STATUS_NAME) VALUES(CAST('''+ '-1' + ''' AS VARCHAR(2)),CAST('''+'
		EXECUTE IMMEDIATE SQLSTR;
    ELSE
       IF P_SHOWTYPE = 2  AND TRIM(P_ORDER_STATUS_TYPE)='-1' THEN 
   	        SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_MMK_ORDER_STATUS_DIM(ORDER_STATUS_ID,ORDER_STATUS_NAME) VALUES(CAST('''+ '-1' + ''' AS VARCHAR(2)),CAST('''+'合計'+'''  AS VARCHAR(20))); ';
			EXECUTE IMMEDIATE SQLSTR;
	   END IF;	
	END IF;			
	SET SQLSTR =' INSERT INTO #VT_FUNC_FACT_MMK_ORDER_STATUS_DIM(ORDER_STATUS_ID,ORDER_STATUS_NAME)  '
                         +'   SELECT CODE_ID AS ORDER_STATUS_ID,CODE_NAME AS ORDER_STATUS_NAME '
                         +'      FROM PMART.VW_PBMCODE  '
						 +'   WHERE CODE_TYPE= ''FPO_STATUS'' ';
    IF P_SHOWTYPE = 2  AND TRIM(P_ORDER_STATUS_TYPE)<>'-1' THEN 
	    SET SQLSTR = SQLSTR + ' AND CODE_ID =''' + TRIM(P_ORDER_STATUS_TYPE)+'''';
	END IF;
    EXECUTE IMMEDIATE SQLSTR;   
END SP;