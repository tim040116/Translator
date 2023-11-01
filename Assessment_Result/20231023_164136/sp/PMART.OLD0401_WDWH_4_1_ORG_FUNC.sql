REPLACE PROCEDURE PMART.OLD0401_WDWH_4_1_ORG_FUNC
(
   IN I_ORG_TYPE  VARCHAR(2),   
   IN I_ORG_ID    INTEGER,      
   IN I_PRD_ID   INTEGER       
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR     VARCHAR(4000) DEFAULT '';
  DECLARE SQLSTR_WHERE    VARCHAR(100) DEFAULT '';
  CALL PMART.P_DROP_TABLE ('#VT_WDWH_4_1_ORG_FUNC');
  IF (I_ORG_TYPE = '-1') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_4_1_ORG_FUNC AS  ' 
	  					  + '(SELECT * FROM (  ' 
						  + '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID     ' 
						  + '  				,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO     ' 
						  + '  				,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM    ' 
						  + '  FROM PMART.LAST_ORG_DIM       ' 
						  + '  UNION ALL         ' 
						  + '  SELECT DISTINCT 1 AS ORG_LEVEL, DEPT_ID AS ORG_ID      ' 
						  + '  				 ,DEPT_NO AS ORG_NO    ' 
						  + '   				,DEPT_NM AS ORG_NM    ' 
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1 '
						  + '       ) AA    ' 
						  +  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
     EXECUTE IMMEDIATE SQLSTR;
  END IF;
  IF (I_ORG_TYPE = '1') THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_4_1_ORG_FUNC AS  ' 
	  					  + '(SELECT * FROM (  ' 
						  + '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID     ' 
						  + '  				,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO     ' 
						  + '  				,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM    ' 
						  + '  FROM PMART.LAST_ORG_DIM       ' 						  
						  + '  UNION ALL         ' 
						  + '  SELECT DISTINCT 2 AS ORG_LEVEL, DEPT_ID AS ORG_ID      ' 
						  + '  				 ,DEPT_NO AS ORG_NO    ' 
						  + '   				,DEPT_NM AS ORG_NM    ' 
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1  AND TOT_ID = ' + I_ORG_ID + '    ' 
						  + '  UNION ALL '
						  + '  SELECT DISTINCT 2 AS ORG_LEVEL  , BRANCH_ID AS ORG_ID  ' 
						  + '  		      ,BRANCH_NO AS ORG_NO  '
						  + '     			  ,BRANCH_NM AS ORG_NM  '
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1  AND DEPT_ID = ' + I_ORG_ID + '    ' 
						  + '       ) AA    ' 
						  +  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';   
     EXECUTE IMMEDIATE SQLSTR;
  END IF;
  IF (I_ORG_TYPE = '2') THEN
        SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_4_1_ORG_FUNC AS  ' 
	  					  + '(SELECT * FROM (  ' 
						  + '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID     ' 
						  + '  				,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO     ' 
						  + '  				,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM    ' 
						  + '  FROM PMART.LAST_ORG_DIM       ' 
						  + '  UNION ALL '
						  + '  SELECT DISTINCT 2 AS ORG_LEVEL  , BRANCH_ID AS ORG_ID  ' 
						  + '  		      ,BRANCH_NO AS ORG_NO  '
						  + '     			  ,BRANCH_NM AS ORG_NM  '
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1  AND  DEPT_ID = ' + I_ORG_ID + '    ' 
						  + '  UNION ALL         ' 
						  + '  SELECT DISTINCT 3 AS ORG_LEVEL, RESPON_ID AS ORG_ID     ' 
						  + '  				 ,RESPON_NO AS ORG_NO    ' 
						  + '   				,RESPON_NM AS ORG_NM    ' 
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1  AND  BRANCH_ID = ' + I_ORG_ID + '    ' 						  
						  + '       ) AA    ' 
						  +  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';   
     EXECUTE IMMEDIATE SQLSTR;
  END IF;  
  IF (I_ORG_TYPE = '3') THEN
          SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_4_1_ORG_FUNC AS  ' 
	  					  + '(SELECT * FROM (  ' 
						  + '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID     ' 
						  + '  				,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO     ' 
						  + '  				,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM    ' 
						  + '  FROM PMART.LAST_ORG_DIM       ' 
						  + '  UNION ALL         ' 
						  + '  SELECT DISTINCT 3 AS ORG_LEVEL, RESPON_ID AS ORG_ID     ' 
						  + '  				 ,RESPON_NO AS ORG_NO    ' 
						  + '   				,RESPON_NM AS ORG_NM    ' 
						  + '    FROM PMART.ORG_DIM_POSI1   ' 
						  + '   WHERE SEQNO=1  AND  BRANCH_ID = ' + I_ORG_ID + '    ' 			
						  + '  UNION ALL '
						  + '  SELECT DISTINCT 4 AS ORG_LEVEL  , A.KND_ID AS ORG_ID  ' 
						  + '  		      ,A.KND_NO AS ORG_NO  '
						  + '     			  ,P.KND_NAME AS ORG_NM  '
						  + '    FROM PMART.ORG_DIM_POSI1  AS A ' 
						  + '   LEFT JOIN PMART.PRD_KND AS P ON P.KND_ID=A.KND_ID'
						  + '   WHERE SEQNO=1  AND  RESPON_ID = ' + I_ORG_ID + '    ' 						  
						  + '       ) AA    ' 
						  +  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';   
     EXECUTE IMMEDIATE SQLSTR;
  END IF;  
    IF (I_ORG_TYPE = '4') THEN
          SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_4_1_ORG_FUNC AS  ' 
	  					  + '(SELECT * FROM (  ' 
						  + '  SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID AS ORG_ID     ' 
						  + '  				,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO     ' 
						  + '  				,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM    ' 
						  + '  FROM PMART.LAST_ORG_DIM       ' 
						  + '  UNION ALL '
						  + '  SELECT DISTINCT 4 AS ORG_LEVEL  , A.KND_ID AS ORG_ID  ' 
						  + '  		      ,A.KND_NO AS ORG_NO  '
						  + '     			  ,P.KND_NAME AS ORG_NM  '
						  + '    FROM PMART.ORG_DIM_POSI1  AS A ' 
						  + '   LEFT JOIN PMART.PRD_KND AS P ON P.KND_ID=A.KND_ID'
						  + '   WHERE SEQNO=1  AND  RESPON_ID = ' + I_ORG_ID + '  AND  A.KND_ID = ' + I_PRD_ID + '   ' 						  
						  + '  UNION ALL         ' 
						  + '  SELECT DISTINCT 5 AS ORG_LEVEL, A.GRP_ID AS ORG_ID     ' 
						  + '  				 ,A.GRP_NO AS ORG_NO    ' 
						  + '   				 ,P.GRP_NAME AS ORG_NM    ' 
						  + '    FROM PMART.ORG_DIM_POSI1 AS A  ' 
						  + '  LEFT JOIN PMART.PRD_GRP AS P ON P.GRP_ID=A.GRP_ID ' 
						  + '   WHERE SEQNO=1   AND  RESPON_ID = ' + I_ORG_ID + '  AND  A.KND_ID = ' + I_PRD_ID + '    ' 								  
						  + '       ) AA    ' 
						  +  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';   
     EXECUTE IMMEDIATE SQLSTR;
  END IF;  
END SP;