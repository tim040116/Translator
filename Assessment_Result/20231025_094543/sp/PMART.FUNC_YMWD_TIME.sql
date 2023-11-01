REPLACE PROCEDURE PMART.FUNC_YMWD_TIME
(
   IN P_INPUT_TYPE INTEGER,
   IN P_INPUT_VALUE INTEGER,
   IN P_RETURN_TYPE INTEGER,
   IN P_PERIOD INTEGER
)
SP:BEGIN
	DECLARE V_DATE_ID INTEGER;
	DECLARE V_PERIOD INTEGER;
   DECLARE SQLSTR  VARCHAR(2000);    
	IF P_PERIOD > 0 THEN
		SET V_PERIOD = P_PERIOD - 1;
	ELSE
	    SET V_PERIOD = 0;
	END IF;	
	IF P_INPUT_TYPE = 1 THEN
		SET V_DATE_ID = P_INPUT_VALUE  - V_PERIOD;
	ELSEIF P_INPUT_TYPE = 2 THEN
		SELECT L_MONTH_ID INTO V_DATE_ID
		    FROM TIME_M
		WHERE L_MONTH_SERIAL IN (
		SELECT L_MONTH_SERIAL - V_PERIOD  FROM TIME_M WHERE L_MONTH_ID = P_INPUT_VALUE);	
	ELSEIF P_INPUT_TYPE = 3 THEN
		SELECT DISTINCT L_WEEK_ID INTO V_DATE_ID
		    FROM TIME_W
		WHERE L_WEEK_SERIAL IN (
		SELECT  DISTINCT L_WEEK_SERIAL - V_PERIOD  FROM TIME_W WHERE L_WEEK_ID = P_INPUT_VALUE);			
	ELSEIF P_INPUT_TYPE = 4 THEN	    
        SELECT L_DAY_ID INTO V_DATE_ID
		    FROM TIME_D
		WHERE L_DAY_SERIAL IN (
		SELECT L_DAY_SERIAL - V_PERIOD  FROM TIME_D WHERE L_DAY_ID = P_INPUT_VALUE);	
	END IF;
    CALL PMART.P_DROP_TABLE ('#VT_TIME');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_TIME AS('
                          +' SELECT DISTINCT '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'L_YEAR_ID  AS TIME_ID,L_YEAR_NAME AS TIME_NAME'
                                                                       WHEN 2 THEN 'L_MONTH_ID AS TIME_ID,L_MONTH_NAME AS TIME_NAME'
                                                                       WHEN 3 THEN 'L_WEEK_ID AS TIME_ID,L_WEEK_NAME AS TIME_NAME'
                                                                       WHEN 4 THEN 'L_DAY_ID AS TIME_ID,L_DAY_NAME AS TIME_NAME' ELSE '' END
                          +' FROM PMART.'
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'TIME_Y'
                                                                       WHEN 2 THEN 'TIME_M'
                                                                       WHEN 3 THEN 'TIME_W'
                                                                       WHEN 4 THEN 'TIME_D' ELSE '' END
                          +' AS S1 WHERE EXISTS(SELECT * FROM PMART.TIME_D AS S2 WHERE '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'S1.L_YEAR_ID=S2.L_YEAR_ID'
                                                                       WHEN 2 THEN 'S1.L_MONTH_ID=S2.L_MONTH_ID'
                                                                       WHEN 3 THEN 'S1.L_WEEK_ID=S2.L_WEEK_ID'                                                                       
                                                                       WHEN 4 THEN 'S1.L_DAY_ID=S2.L_DAY_ID'   ELSE '' END
                          +' AND '
                    + CASE P_INPUT_TYPE WHEN 1 THEN ' S2.L_YEAR_ID '
                                                              WHEN 2 THEN ' S2.L_MONTH_ID'
                                                              WHEN 3 THEN ' S2.L_WEEK_ID'
                                                              WHEN 4 THEN ' S2.L_DAY_ID' ELSE '' END
                    + ' BETWEEN '+TO_CHAR(V_DATE_ID,'99999999')+' AND '+TO_CHAR(P_INPUT_VALUE,'99999999')         
                          + ' )) WITH DATA PRIMARY INDEX(TIME_NAME) ON COMMIT PRESERVE ROWS;';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;