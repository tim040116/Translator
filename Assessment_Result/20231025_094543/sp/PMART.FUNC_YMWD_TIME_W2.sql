REPLACE PROCEDURE PMART.FUNC_YMWD_TIME_W2
(
   IN P_INPUT_TYPE INTEGER,
   IN P_INPUT_VALUE INTEGER,
   IN P_RETURN_TYPE INTEGER,
   IN P_PERIOD INTEGER,
   IN P_SHOWTTYPE SMALLINT  
)
SP:BEGIN
	DECLARE V_DATE_ID INTEGER;
	DECLARE V_PERIOD INTEGER;
   DECLARE SQLSTR  VARCHAR(5000) ;
	IF P_PERIOD > 0 THEN
		SET V_PERIOD = P_PERIOD - 1;
	ELSE
	    SET V_PERIOD = 0;
	END IF;	
	IF P_INPUT_TYPE = 1 THEN
		SET V_DATE_ID = P_INPUT_VALUE  - V_PERIOD;
	ELSEIF P_INPUT_TYPE = 2 THEN
		SELECT L_MONTH_ID INTO V_DATE_ID
		    FROM PMART.TIME_M
		WHERE L_MONTH_SERIAL IN (
		SELECT L_MONTH_SERIAL - V_PERIOD  FROM PMART.TIME_M WHERE L_MONTH_ID = P_INPUT_VALUE);	
	ELSEIF P_INPUT_TYPE = 3 THEN
		SELECT DISTINCT L_WEEK_ID INTO V_DATE_ID
		    FROM PMART.YMWD_TIME_W2
		WHERE L_WEEK_SERIAL IN (
		SELECT  DISTINCT L_WEEK_SERIAL - V_PERIOD  FROM PMART.YMWD_TIME_W2 WHERE L_WEEK_ID = P_INPUT_VALUE);		
	ELSEIF P_INPUT_TYPE = 4 THEN	    
        SELECT L_DAY_ID INTO V_DATE_ID
		    FROM PMART.TIME_D
		WHERE L_DAY_SERIAL IN (
		SELECT L_DAY_SERIAL - V_PERIOD  FROM PMART.TIME_D WHERE L_DAY_ID = P_INPUT_VALUE);	
	END IF;
    CALL PMART.P_DROP_TABLE ('#VT_TIME');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_TIME AS(';
	IF P_SHOWTTYPE = 2 THEN 
        SET SQLSTR =  SQLSTR  +' SELECT DISTINCT CAST(-1 AS INTEGER) AS TIME_ID,CAST('''+'合計'+'''  AS VARCHAR(24)) TIME_NAME FROM PMART.TIME_Y UNION ALL';
	ELSE
	    SET SQLSTR =  SQLSTR  +' SELECT DISTINCT CAST(-1 AS INTEGER) AS TIME_ID,CAST('''+'
    END IF;
	SET SQLSTR =  SQLSTR  +' SELECT DISTINCT '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'L_YEAR_ID  AS TIME_ID,L_YEAR_NAME AS TIME_NAME'
                                                                       WHEN 2 THEN  CASE WHEN P_SHOWTTYPE = 1 THEN 'L_MONTH_ID AS TIME_ID,SUBSTRING(CAST(L_MONTH_ID AS VARCHAR(6)),5,2) AS TIME_NAME'
																	                                 ELSE 'L_MONTH_ID AS TIME_ID,L_MONTH_NAME AS TIME_NAME'
																									 END			 
                                                                       WHEN 3 THEN 'L_WEEK_ID AS TIME_ID,L_WEEK_NAME AS TIME_NAME'
                                                                       WHEN 4 THEN 'L_DAY_ID AS TIME_ID,L_DAY_NAME AS TIME_NAME' ELSE '' END
                          +' FROM PMART.'
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'TIME_Y'
                                                                       WHEN 2 THEN 'TIME_M'
                                                                       WHEN 3 THEN 'YMWD_TIME_W2'
                                                                       WHEN 4 THEN 'TIME_D' ELSE '' END
                          +' AS S1 WHERE EXISTS(SELECT * FROM PMART.YMWD_TIME_W2 AS S2 WHERE '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'S1.L_YEAR_ID=S2.L_YEAR_ID'
                                                                       WHEN 2 THEN 'S1.L_MONTH_ID=S2.L_MONTH_ID'
                                                                       WHEN 3 THEN 'S1.L_DAY_ID=S2.L_DAY_ID'
                                                                       WHEN 4 THEN 'S1.L_DAY_ID=S2.L_DAY_ID'   ELSE '' END
                          +' AND '
						  + CASE P_INPUT_TYPE WHEN 1 THEN ' S2.L_YEAR_ID'
                                                                    WHEN 2 THEN ' S2.L_MONTH_ID'
                                                                    WHEN 3 THEN ' S2.L_WEEK_ID'
                                                                    WHEN 4 THEN ' S2.L_DAY_ID' ELSE '' END
						  + ' BETWEEN '+TO_CHAR(V_DATE_ID,'99999999')+' AND '+TO_CHAR(P_INPUT_VALUE,'99999999')         
                          + ' )) WITH DATA PRIMARY INDEX(TIME_NAME) ON COMMIT PRESERVE ROWS;';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;