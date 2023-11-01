REPLACE PROCEDURE PMART.REMD_T4_NCIG_BRANCH_FUNC(P_DAY_ID NUMBER,P_DEPT_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_T4_NCIG_BRANCH_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_T4_NCIG_BRANCH_FUNC AS('+            
			      'SELECT '+
            'B.ORG_ID AS ORG_ID, '+
            'BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,B.MASK)) AS MAST_STORE_NUM '+
            'FROM PMART.BASIC_OST_FACT A, '+
            '(SELECT ORG_ID ,MASK FROM PMART.LAST_ORG_DIM_MASK WHERE P_ORG_ID='+P_DEPT_ID+') B '+
            'WHERE A.TIME_ID='+P_DAY_ID+
        ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;