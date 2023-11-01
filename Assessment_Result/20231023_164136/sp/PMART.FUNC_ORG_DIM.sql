REPLACE PROCEDURE PMART.FUNC_ORG_DIM
(
   IN P_INPUT_TYPE INTEGER,
   IN P_INPUT_VALUE INTEGER,
   IN P_RETURN_TYPE INTEGER 
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(2000);    
        CALL PMART.P_DROP_TABLE ('#VT_ORG');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_ORG AS('
                          +' SELECT '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'DEPT_NO AS ORG_NO,DEPT_ID  AS ORG_ID,DEPT_SNAME AS ORG_NAME'
                                                                       WHEN 2 THEN 'BRANCH_NO AS ORG_NO, BRANCH_ID AS ORG_ID,BRANCH_SNAME AS ORG_NAME'
                                                                       WHEN 3 THEN 'RESPON_NO AS ORG_NO, RESPON_ID AS ORG_ID,RESPON_NAME AS ORG_NAME'
                                                                       WHEN 4 THEN 'STORE_NO AS ORG_NO, OSTORE_ID AS ORG_ID,STORE_NAME AS ORG_NAME' ELSE '' END
                          +' FROM PMART.'
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'ORG_DEPT'
                                                                       WHEN 2 THEN 'ORG_BRANCH'
                                                                       WHEN 3 THEN 'ORG_RESPON'
                                                                       WHEN 4 THEN 'LAST_ORG_STORE' ELSE '' END
                          +' AS S1 WHERE EXISTS(SELECT * FROM PMART.LAST_ORG_STORE AS S2 WHERE '
                          +CASE P_RETURN_TYPE WHEN 1 THEN 'S1.DEPT_ID=S2.DEPT_ID'
                                                                       WHEN 2 THEN 'S1.BRANCH_ID=S2.BRANCH_ID'
                                                                       WHEN 3 THEN 'S1.RESPON_ID=S2.RESPON_ID'                                                                       
                                                                       WHEN 4 THEN 'S1.STORE_ID=S2.STORE_ID'   ELSE '' END
                          + CASE WHEN P_INPUT_VALUE>0 THEN ' AND '
                             + CASE P_INPUT_TYPE WHEN 1 THEN ' S2.DEPT_ID '
                                                                       WHEN 2 THEN ' S2.BRANCH_ID'
                                                                       WHEN 3 THEN ' S2.RESPON_ID'
                                                                       WHEN 4 THEN ' S2.STORE_ID' ELSE '' END
                             + ' = '+ CASE WHEN P_INPUT_VALUE=0 THEN CASE P_INPUT_TYPE WHEN 1 THEN ' S2.DEPT_ID '
                                                                                                                                                      WHEN 2 THEN ' S2.BRANCH_ID'
                                                                                                                                                      WHEN 3 THEN ' S2.RESPON_ID'
                                                                                                                                                      WHEN 4 THEN ' S2.STORE_ID' ELSE '' END
                                                                                 ELSE TO_CHAR(P_INPUT_VALUE,'9999999999') END
                              ELSE '' END                        
                          + ' )) WITH DATA PRIMARY INDEX(ORG_NAME) ON COMMIT PRESERVE ROWS;';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;