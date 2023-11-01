REPLACE PROCEDURE PMART.MONTH_MAST_STNUM
(P_DAY_ID NUMBER,P_ORG_ID NUMBER,P_LEVEL NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_MONTH_MAST_STNUM'); 
    CASE P_LEVEL
      WHEN 0 THEN
         SET SQLSTR = 
            'CREATE MULTISET VOLATILE TABLE #VT_MONTH_MAST_STNUM AS('+            
                 'SELECT COUNT(*) AS CNT FROM '+
                 '( '+
                 'SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                 'WHERE OPNDT<'+P_DAY_ID+' AND ENDDT>=ROUND('+P_DAY_ID+'/100)*100+01 '+
                 'AND TOT_ID=-1 '+
                 ') T  '+           
             ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
         EXECUTE IMMEDIATE SQLSTR; 
      WHEN 1 THEN
         SET SQLSTR = 
            'CREATE MULTISET VOLATILE TABLE #VT_MONTH_MAST_STNUM AS('+            
                 'SELECT COUNT(*) AS CNT FROM '+
                 '( '+
                 'SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                 'WHERE OPNDT<'+P_DAY_ID+' AND ENDDT>=ROUND('+P_DAY_ID+'/100)*100+01 '+
                 'AND DEPT_ID='+P_ORG_ID+
                 ') T  '+           
             ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
         EXECUTE IMMEDIATE SQLSTR; 
      WHEN 2 THEN
         SET SQLSTR = 
            'CREATE MULTISET VOLATILE TABLE #VT_MONTH_MAST_STNUM AS('+            
                 'SELECT COUNT(*) AS CNT FROM '+
                 '( '+
                 'SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                 'WHERE OPNDT<'+P_DAY_ID+' AND ENDDT>=ROUND('+P_DAY_ID+'/100)*100+01 '+
                 'AND BRANCH_ID='+P_ORG_ID+
                 ') T '+           
             ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
         EXECUTE IMMEDIATE SQLSTR; 
      WHEN 3 THEN
         SET SQLSTR = 
            'CREATE MULTISET VOLATILE TABLE #VT_MONTH_MAST_STNUM AS('+            
                 'SELECT COUNT(*) AS CNT FROM '+
                 '( '+
                 'SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                 'WHERE OPNDT<'+P_DAY_ID+' AND ENDDT>=ROUND('+P_DAY_ID+'/100)*100+01 '+
                 'AND RESPON_ID='+P_ORG_ID+
                 ') T '+           
             ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
         EXECUTE IMMEDIATE SQLSTR; 
   END CASE;
END SP;