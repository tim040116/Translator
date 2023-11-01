REPLACE PROCEDURE PMART.FUNC_POMO_DIM
(
   IN P_INPUT_TYPE INTEGER,
   IN P_INPUT_VALUE BIGINT,
   IN P_RETURN_TYPE INTEGER
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(2000);    
    CALL PMART.P_DROP_TABLE ('#VT_POMO');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_POMO AS('
                          +' SELECT POMO_ID,POMO_NAME FROM PMART.VW_POMO_DIM'
                          +' AS S1 WHERE EXISTS(SELECT * FROM PMART.VW_HAPY_PRD AS S2 WHERE '
                          + CASE P_RETURN_TYPE WHEN 1 THEN 'S1.POMO_ID=S2.POMO_MAST_ID'
                                                                        WHEN 2 THEN 'S1.POMO_ID=S2.POMO_KND_ID'
                                                                        WHEN 3 THEN 'S1.POMO_ID=S2.POMO_GRP_ID'                                                                       
                                                                        WHEN 4 THEN 'S1.POMO_ID=S2.POMO_PRD_ID'   ELSE '' END
                          + CASE WHEN P_INPUT_VALUE>0 THEN ' AND '
                          + CASE P_INPUT_TYPE WHEN 1 THEN ' S2.POMO_MAST_ID '
                                                                        WHEN 2 THEN ' S2.POMO_KND_ID'
                                                                        WHEN 3 THEN ' S2.POMO_GRP_ID'
                                                                        WHEN 4 THEN ' S2.POMO_PRD_ID' ELSE '' END
                          + ' = '+ CASE WHEN P_INPUT_VALUE=0 THEN CASE P_INPUT_TYPE WHEN 1 THEN ' S2.POMO_MAST_ID '
                                                                                                                                                                WHEN 2 THEN ' S2.POMO_KND_ID'
                                                                                                                                                                WHEN 3 THEN ' S2.POMO_GRP_ID'
                                                                                                                                                                WHEN 4 THEN ' S2.POMO_PRD_ID' ELSE '' END
                                                                                 ELSE TO_CHAR(P_INPUT_VALUE,'9999999999999999999') END
                              ELSE '' END                        
                          + ' )) WITH DATA PRIMARY INDEX(POMO_ID) ON COMMIT PRESERVE ROWS;';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;