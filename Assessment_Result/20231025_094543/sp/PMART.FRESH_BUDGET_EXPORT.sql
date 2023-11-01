REPLACE PROCEDURE PMART.FRESH_BUDGET_EXPORT
(
   IN P_EMP_NO   VARCHAR(10),  
   IN P_ORG_TYPE VARCHAR(1),   
   IN P_ORG_CODE VARCHAR(400), 
   IN P_FM_TYPE  VARCHAR(1),   
   IN P_FM_CODE  VARCHAR(400), 
   IN P_SDATE    INTEGER,      
   IN P_EDATE    INTEGER       
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR      VARCHAR(4000) DEFAULT '';
  DECLARE SQLSTR_6    VARCHAR(500)  DEFAULT '';
  DECLARE ORGIDWHERE  VARCHAR(500); 
  DECLARE PRDIDWHERE  VARCHAR(500); 
  DECLARE SEDATWHERE1 VARCHAR(500); 
  DECLARE SEDATWHERE2 VARCHAR(500);
  DECLARE ORGIDFIELD1 VARCHAR(500); 
  DECLARE ORGIDFIELD2 VARCHAR(500); 
  DECLARE ORGIDFIELD3 VARCHAR(500);
  DECLARE P_SE_DATE     INTEGER;  
  DECLARE P_BUDGET_DATE INTEGER;  
  SET ORGIDWHERE = '' ;
  IF P_ORG_TYPE = 2 THEN 
     SET ORGIDWHERE = ' AND A.DEPT_NO = ''' + P_ORG_CODE + ''' ';		
  ELSEIF P_ORG_TYPE = 4 THEN 
     SET ORGIDWHERE = ' AND A.PDEPT_NO = ''' + P_ORG_CODE + ''' ';		
  ELSE
     SET ORGIDWHERE = ' ';
  END IF;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_STORE_ID');  
     SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_STORE_ID AS  ' +
                  '(SELECT A.OSTORE_ID                             ' +
                  '       ,A.STORE_ID                              ' +
                  '       ,A.STORE_NO                              ' +
                  '       ,A.STORE_NM                              ' +
                  '       ,A.OPNDT                                 ' +
                  '       ,A.ENDDT                                 ' +
                  '       ,A.BRANCH_ID                             ' +
                  '       ,A.BRANCH_NO                             ' +
                  '       ,A.BRANCH_NM                             ' +
                  '       ,A.DEPT_ID                               ' +
                  '       ,A.DEPT_NO                               ' +
                  '       ,A.DEPT_NM                               ' +
		         '       ,A.PDEPT_ID                               ' +
                  '       ,A.PDEPT_NO                               ' +
                  '       ,A.PDEPT_NM                               ' +
                  '       ,B.LOCAL0                                ' +
                  '       ,B.LOCAL_NAME                            ' +
                  '       ,B.LOCAL1                                ' +
                  '       ,B.LOCAL1_NAME                           ' +
                  '       ,'''+P_EMP_NO+''' AS EMP_NO            ' +
                  '  FROM PMART.LATEST_ORG_DIM  A                  ' +
                  '  LEFT OUTER  JOIN PDATA.LATEST_SHOPDIST B      ' +
                  '    ON A.STORE_ID = CAST(B.STORE_NO AS INTEGER) ' +
                  ' WHERE A.ENDDT >= '+P_EDATE+'                 ' +
                  '  '+ORGIDWHERE+'                              ' +
                  ')                                               ' +
                  'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR;
  SET PRDIDWHERE = '' ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_PRDID');
  IF P_FM_TYPE = 1 THEN 
     SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_PRDID AS ' +
                  '(SELECT FM_CODE,FM_NAME,'''+P_EMP_NO+''' AS EMP_NO     ' +
                  '   FROM PDATA.PBMCMDT                                    ' +
                  '  WHERE KGRP_KIND_CODE IN (                              ' +
                  ' SELECT B.KND_NO AS FM_CODE                              ' +
                  '   FROM PDATA.FRESH_PBMKGRP A INNER JOIN PMART.PRD_KND B ' +
                  '     ON A.KIND_CODE = B.KND_NO                           ' +
                  '  WHERE A.GRP_FG = ''Y''        )                        ' +
                  ' )                                                       ' +
                  'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  ELSEIF P_FM_TYPE = 2 THEN 
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_PRDID AS ' +
                  '(SELECT FM_CODE,FM_NAME,'''+P_EMP_NO+''' AS EMP_NO     ' +
                  '   FROM PDATA.PBMCMDT                                    ' +
                  '  WHERE GROUP_CODE IN (                         ' +
                  ' SELECT B.GRP_NO AS FM_CODE                              ' +
                  '   FROM PDATA.FRESH_PBMKGRP A INNER JOIN PMART.PRD_GRP B ' +
                  '     ON A.KIND_CODE+GROUP_CODE = B.GRP_NO               ' +
                  '  WHERE A.GRP_FG = ''Y''                                 ' +
                  '    AND B.KND_NO = '''+P_FM_CODE+''' )                 ' +
                  ' )                                                       ' +
                  'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  ELSEIF P_FM_TYPE = 3 THEN
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_PRDID AS ' +
                  '(SELECT C.FM_CODE,C.FM_NAME,'''+P_EMP_NO+''' AS EMP_NO ' +
                  '   FROM PDATA.FRESH_PBMKGRP A INNER JOIN PMART.PRD_GRP B ' +
                  '     ON A.KIND_CODE+GROUP_CODE = B.GRP_NO               ' +
                  '                              INNER JOIN PMART.PRD_PRD C ' +
                  '     ON B.GRP_ID =C.GRP_ID                               ' +
                  '  WHERE A.GRP_FG = ''Y''                                 ' +
                  '    AND B.GRP_ID = '''+P_FM_CODE+'''                   ' +
                  ' )                                                       ' +
                  'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';  
  ELSE
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_PRDID AS ' +
                  '(SELECT FM_CODE,FM_NAME,'''+P_EMP_NO+''' AS EMP_NO     ' +
                  '   FROM PDATA.PBMCMDT                                    ' +
                  '  WHERE FM_CODE = '''+P_FM_CODE+'''                    ' +
                  ' )                                                       ' +
                  'WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';  
  END IF;
  EXECUTE IMMEDIATE SQLSTR;  
  SET SEDATWHERE1 = '' ;
  SET SEDATWHERE2 = '' ;  
  IF P_SDATE/100 = P_EDATE/100   THEN 
     SET P_SE_DATE = 0;
  ELSE 
     SET P_SE_DATE = 1;
  END IF;
  SET P_BUDGET_DATE = P_SDATE/100 ;
  SET SEDATWHERE1 = ' WHERE A.TIME_ID  >= '+P_SDATE+' AND A.TIME_ID  <='+P_EDATE+' ' ;
  SET SEDATWHERE2 = ' WHERE A.L_DAY_ID >= '+P_SDATE+' AND A.L_DAY_ID <='+P_EDATE+' ' ;  
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_SPD');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_SPD AS      ' +
                  '(SELECT A.TIME_ID,A.ORG_ID,A.PRD_ID,A.SALES_CNT,A.SALES_AMT ' +
                  '  ,B.OSTORE_ID,B.STORE_ID,B.STORE_NM,B.OPNDT,B.ENDDT        ' +
                  '  ,B.BRANCH_ID,B.BRANCH_NO,B.BRANCH_NM  ' +
				  '  ,B.DEPT_ID,B.DEPT_NO,B.DEPT_NM  ' +
				  '  ,B.PDEPT_ID,B.PDEPT_NO,B.PDEPT_NM  ' +
                  '  ,B.LOCAL0,B.LOCAL_NAME,B.LOCAL1,B.LOCAL1_NAME   ' +
                  '  ,C.FM_CODE,C.FM_NAME,C.EMP_NO                             ' +
                  '  FROM PMART.BASIC_MFACT_DETAIL A                           ' +
                  '   INNER JOIN #VT_FRESH_BUDGET_STORE_ID B                   ' +
                  '      ON A.ORG_ID = B.OSTORE_ID                             ' +		  
                  '     AND A.TIME_ID >=B.OPNDT                                ' +
                  '     AND A.TIME_ID < B.ENDDT                                ' +
                  '   INNER JOIN #VT_FRESH_BUDGET_PRDID C                      ' +
                  '      ON A.PRD_ID = C.FM_CODE                               ' +
                  '     AND B.EMP_NO = C.EMP_NO                                ' +
                  '  '+SEDATWHERE1+'                                         ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
  EXECUTE IMMEDIATE SQLSTR;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_STNUM');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_STNUM AS    ' +
                  '(SELECT SUM(A.PLAN_STNUM) AS PLAN_STNUM                     ' +
                  '  ,B.OSTORE_ID,B.STORE_ID,B.STORE_NM,B.OPNDT,B.ENDDT        ' +
                  '  ,B.BRANCH_ID,B.BRANCH_NO,B.BRANCH_NM  ' +
                  '  ,B.DEPT_ID,B.DEPT_NO,B.DEPT_NM  ' +
				  '  ,B.PDEPT_ID,B.PDEPT_NO,B.PDEPT_NM  ' +
				  '  ,B.LOCAL0,B.LOCAL_NAME,B.LOCAL1,B.LOCAL1_NAME   ' +
                  '  ,B.EMP_NO                                                 ' +
                  '  FROM PMART.REMD_FACT A                                    ' +
                  '   INNER JOIN #VT_FRESH_BUDGET_STORE_ID B                   ' +
                  '      ON A.OSTORE_ID = B.OSTORE_ID                          ' +                  
                  '     AND (A.L_DAY_ID >=B.OPNDT AND A.L_DAY_ID < B.ENDDT)    ' +
                  '     AND B.EMP_NO = '''+P_EMP_NO+'''                      ' +
                  '  '+SEDATWHERE2+'                                         ' +
                  '  GROUP BY                                                  ' +
                  '   B.OSTORE_ID,B.STORE_ID,B.STORE_NM,B.OPNDT,B.ENDDT        ' +
                  '  ,B.BRANCH_ID,B.BRANCH_NO,B.BRANCH_NM,B.DEPT_ID,B.DEPT_NO  ' +
                  '  ,B.DEPT_NM,B.PDEPT_ID,B.PDEPT_NO,B.PDEPT_NM   ' +
                  '  ,B.LOCAL0,B.LOCAL_NAME,B.LOCAL1,B.LOCAL1_NAME,B.EMP_NO  ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR;     
  SET SQLSTR_6 = '' ;
  SET ORGIDFIELD1 = '' ;
  SET ORGIDFIELD2 = '' ;
  IF P_ORG_TYPE = 1 THEN 
     SET ORGIDFIELD1 = ' PDEPT_NO AS ORG_ID ,PDEPT_NM AS ORG_NM';
     SET ORGIDFIELD2 = ' PDEPT_NO ,PDEPT_NM ';
  ELSEIF P_ORG_TYPE = 4 THEN 
     SET ORGIDFIELD1 = ' DEPT_NO AS ORG_ID ,DEPT_NM AS ORG_NM';
     SET ORGIDFIELD2 = ' DEPT_NO ,DEPT_NM ';
  ELSEIF P_ORG_TYPE = 2 THEN 
     SET ORGIDFIELD1 = ' BRANCH_NO AS ORG_ID ,BRANCH_NM AS ORG_NM';
     SET ORGIDFIELD2 = ' BRANCH_NO ,BRANCH_NM ';
  ELSE
     SET ORGIDFIELD1 = ' LOCAL0 AS ORG_ID ,LOCAL_NAME AS ORG_NM';
     SET ORGIDFIELD2 = ' LOCAL0 ,LOCAL_NAME ';
     SET SQLSTR_6 =  '  LEFT OUTER  JOIN PDATA.LATEST_SHOPDIST B      ' ;
     SET SQLSTR_6 =  SQLSTR_6 + '    ON A.STORE_ID = CAST(B.STORE_NO AS INTEGER) ' ;
  END IF;  
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_SPD_1');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_SPD_1 AS                 ' +
                  '(SELECT T3.EMP_NO,T3.CSPID+10 AS CSPID                                   ' +
                  '  ,T3.CSPRG,T3.ORG_ID,T3.ORG_NM                                          ' +
	          '  ,CAST(COUNT(DISTINCT T4.OSTORE_ID) AS DECIMAL(12,2)  ) AS SCNT         ' +
                  ' FROM                                                                    ' +
                  ' (SELECT * FROM                                                          ' +
                  '  (SELECT * FROM PMART.FRESH_CSPRG                                       ' +
                  '    WHERE EMP_NO = '''+P_EMP_NO+''') T1,                               ' + 
                  '  (SELECT '+ORGIDFIELD1+'                                              ' +
                  '     FROM PMART.LATEST_ORG_DIM A                                         ' +
                  '  '+SQLSTR_6+'                                                         ' +
                  ' WHERE A.ENDDT >= '+P_EDATE+'                                          ' +
                  '  '+ORGIDWHERE+'                                                       ' +
                  '    GROUP BY  '+ORGIDFIELD2+' )  T2                                    ' +
                  ' ) T3 LEFT OUTER JOIN                                                    ' +
                  ' (                                                                       ' +  
                  ' SELECT (CSP1/CSP2) AS CSP,T41.OSTORE_ID,ORG_ID,ORG_NM,T41.EMP_NO        ' +
                  '   FROM                                                                  ' +
                  '   (                                                                     ' +
                  '    SELECT SUM(SALES_CNT) AS CSP1,OSTORE_ID,'+ORGIDFIELD1+',EMP_NO     ' +
                  '      FROM #VT_FRESH_BUDGET_SPD                                          ' +
                  '     GROUP BY '+ORGIDFIELD2+' ,OSTORE_ID,EMP_NO                        ' +
                  '   ) T41  INNER JOIN                                                     ' +
                  '   (                                                                     ' +
                  '    SELECT AVG(PLAN_STNUM) AS CSP2,OSTORE_ID,EMP_NO                      ' +
                  '      FROM #VT_FRESH_BUDGET_STNUM                                        ' +
                  '     GROUP BY OSTORE_ID,EMP_NO                                           ' +
                  '   ) T42                                                                 ' + 
                  '   ON T41.OSTORE_ID = T42.OSTORE_ID                                      ' +
                  '  AND T41.EMP_NO =T42.EMP_NO                                             ' +                 
                  ' ) T4                                                                    ' +
                  '  ON T3.EMP_NO = T4.EMP_NO                                               ' +
                  ' AND T3.ORG_ID = T4.ORG_ID                                               ' +
                  ' AND (T4.CSP >= T3.SCSP AND T4.CSP <= T3.ECSP)                           ' +
                  ' GROUP BY T3.EMP_NO,T3.CSPID,T3.CSPRG,T3.ORG_ID,T3.ORG_NM                ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
  EXECUTE IMMEDIATE SQLSTR;
  INSERT INTO  #VT_FRESH_BUDGET_SPD_1
  SELECT EMP_NO,CSPID,CSPRG,'-1' AS ORG_ID, '合計' AS ORG_NM,SUM(SCNT) AS TOTCNT
    FROM #VT_FRESH_BUDGET_SPD_1
   GROUP BY  EMP_NO,CSPID,CSPRG
  ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_SPD_2');  
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_SPD_2 AS         ' +
                  '(                                                                ' +
                  ' SELECT SUM(SALES_CNT) AS CSP,OSTORE_ID,'+ORGIDFIELD1+',EMP_NO ' +
                  '   FROM #VT_FRESH_BUDGET_SPD                                     ' +
                  '  GROUP BY '+ORGIDFIELD2+' ,OSTORE_ID,EMP_NO                   ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
  EXECUTE IMMEDIATE SQLSTR;
  INSERT INTO  #VT_FRESH_BUDGET_SPD_1
  SELECT  EMP_NO,61,'總銷售量',ORG_ID,ORG_NM,SUM(CSP)
    FROM #VT_FRESH_BUDGET_SPD_2 
   WHERE ORG_ID   <>  '-1'
   GROUP BY EMP_NO,ORG_ID,ORG_NM
  ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_SPD_3');     
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_SPD_3 AS ' +
                  '(SELECT EMP_NO,  '+ORGIDFIELD1+'                       ' +
                  '       ,SUM(PLAN_STNUM) AS SCNT                          ' +
                  '   FROM #VT_FRESH_BUDGET_STNUM                           ' +
                  '  GROUP BY EMP_NO, '+ORGIDFIELD2+'                     ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';  			
  EXECUTE IMMEDIATE SQLSTR; 
  INSERT INTO #VT_FRESH_BUDGET_SPD_1
  SELECT EMP_NO,62 , '稼動日總和', ORG_ID,ORG_NM,SCNT
  FROM #VT_FRESH_BUDGET_SPD_3
  ;
  INSERT INTO #VT_FRESH_BUDGET_SPD_1
  SELECT A.EMP_NO, 21 AS CSPID,'銷數PSD' AS CSPRG,A.ORG_ID,A.ORG_NM,(A.SCNT/B.SCNT) AS NSCNT
    FROM (SELECT * FROM #VT_FRESH_BUDGET_SPD_1 WHERE CSPID = 61 ) A
        ,(SELECT * FROM #VT_FRESH_BUDGET_SPD_1 WHERE CSPID = 62 ) B
    WHERE A.ORG_ID = B.ORG_ID
  ;
  INSERT INTO #VT_FRESH_BUDGET_SPD_1 (EMP_NO,CSPID,CSPRG,ORG_ID,ORG_NM) VALUES (P_EMP_NO ,21, '銷數PSD', '-1','合計' ) ; 	
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_1');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_1 AS ' +
                  '(SELECT                                              ' +
                  '  A.BUDGET_DATE                                      ' +
                  ' ,B.DEPT_NO,B.DEPT_NM                                ' +
				  ' ,B.PDEPT_NO,B.PDEPT_NM                                ' +
                  ' ,A.BRANCH_NO                                        ' +
                  ' ,A.BRANCH_NM                                        ' +
                  ' ,SUBSTRING(A.GROUP_CODE ,1,2) AS KIND_CODE             ' +
                  ' ,A.GROUP_CODE                                       ' +
                  ' ,A.GROUP_NAME                                       ' +
                  ' ,A.BUDGET_AMT                                       ' +
                  ' ,'''+P_EMP_NO+''' AS EMP_NO                       ' +
                  ' FROM PMART.FRESH_BUDGET A INNER JOIN (              ' +
                  ' SELECT BRANCH_NO,BRANCH_NM,DEPT_NO,DEPT_NM ,PDEPT_NO,PDEPT_NM         ' +
                  ' FROM PMART.LATEST_ORG_DIM                           ' +
                  ' GROUP BY BRANCH_NO,BRANCH_NM,DEPT_NO,DEPT_NM,PDEPT_NO,PDEPT_NM        ' +
                  ' ) B                                                 ' +
                  ' ON A.BRANCH_NO = B.BRANCH_NO                        ' +
                  ' WHERE A.BUDGET_DATE = '+P_BUDGET_DATE+'           ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR;
  SET ORGIDFIELD3 = '' ;
  IF P_FM_TYPE = 1 THEN     
     SET ORGIDFIELD3 = '' ;
  ELSEIF P_FM_TYPE = 2 THEN        
     SET ORGIDFIELD3 = ' AND KIND_CODE = '''+P_FM_CODE+''' ' ;
  ELSE                       
     SET ORGIDFIELD3 = ' AND GROUP_CODE =(SELECT  GRP_NO FROM PMART.PRD_GRP  WHERE  GRP_ID = '''+P_FM_CODE+''' )' ;
  END IF ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_2');    
  IF P_ORG_TYPE <> 3 THEN  
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_2 AS ' +
                  '(SELECT EMP_NO , '+ORGIDFIELD1+'                   ' +
                  '       ,SUM(BUDGET_AMT) AS BAMT                      ' +
                  '   FROM #VT_FRESH_BUDGET_1     A                     ' +
                  '  WHERE 1 = 1                                        ' +
                  '  '+ORGIDWHERE+'                                   ' +
                  '  '+ORGIDFIELD3+'                                  ' +
                  '  GROUP BY EMP_NO , '+ORGIDFIELD2+'                ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';  
  ELSE
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_2 AS ' +
                  '(SELECT EMP_NO ,  DEPT_NO AS ORG_ID                  ' +
                  '       ,DEPT_NM AS ORG_NM ,SUM(BUDGET_AMT) AS BAMT   ' +
                  '   FROM #VT_FRESH_BUDGET_1     A                     ' +
                  '  WHERE 1 <> 1                                       ' +
                  '  GROUP BY EMP_NO , DEPT_NO ,DEPT_NM                 ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  END IF ; 
  EXECUTE IMMEDIATE SQLSTR;  
    INSERT INTO #VT_FRESH_BUDGET_SPD_1
    SELECT A.EMP_NO, 22 AS CSPID,'預算' AS CSPRG,A.ORG_ID,A.ORG_NM, BAMT AS SCNT
      FROM (SELECT DISTINCT EMP_NO,ORG_ID,ORG_NM FROM #VT_FRESH_BUDGET_SPD_1 WHERE CSPID < 17)  A 
      LEFT OUTER JOIN (SELECT ORG_ID,EMP_NO,BAMT FROM #VT_FRESH_BUDGET_2 ) B
        ON A.ORG_ID = B.ORG_ID
       AND A.EMP_NO = B.EMP_NO
    ;    
    INSERT INTO #VT_FRESH_BUDGET_SPD_1
    SELECT A.EMP_NO, 23 AS CSPID,'達成率' AS CSPRG,A.ORG_ID,A.ORG_NM,(A.SCNT/B.SCNT) AS NSCNT
      FROM (SELECT * FROM #VT_FRESH_BUDGET_SPD_1  WHERE CSPID = 21 ) A
          ,(SELECT * FROM #VT_FRESH_BUDGET_SPD_1  WHERE CSPID = 22 ) B
     WHERE A.ORG_ID = B.ORG_ID
    ;
  IF P_SE_DATE = 1 OR  P_FM_TYPE = 4 OR P_ORG_TYPE = 3 THEN 
    INSERT INTO #VT_FRESH_BUDGET_SPD_1
    SELECT EMP_NO, 24 AS CSPID,'達成率排行榜' AS CSPRG,ORG_ID,ORG_NM,SCNT
      FROM #VT_FRESH_BUDGET_SPD_1 
     WHERE CSPID = 23
    ; 	
  ELSE
    INSERT INTO #VT_FRESH_BUDGET_SPD_1
    SELECT EMP_NO, 24 AS CSPID,'達成率排行榜' AS CSPRG,ORG_ID,ORG_NM
          ,RANK() OVER(PARTITION BY CSPID ORDER BY SCNT DESC) DENSE_RANKX
      FROM #VT_FRESH_BUDGET_SPD_1 
     WHERE CSPID = 23
	 AND ORG_ID <> '-1'
    ; 
    INSERT INTO #VT_FRESH_BUDGET_SPD_1 (EMP_NO,CSPID,CSPRG,ORG_ID,ORG_NM) VALUES (P_EMP_NO ,24, '達成率排行榜', '-1','合計' ) ; 	
  END IF ;
  CALL PMART.P_DROP_TABLE ('#VT_FRESH_BUDGET_EXPORT');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_FRESH_BUDGET_EXPORT AS   ' +
                  '(SELECT CSPID/10 AS XX,S.CSPRG AS  XXNM,S.*                 ' +
                  '   FROM #VT_FRESH_BUDGET_SPD_1 S                            ' +
                  '  WHERE CSPID < 25                                          ' +
                  '    AND S.EMP_NO = '''+P_EMP_NO+'''                       ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
  EXECUTE IMMEDIATE SQLSTR;
  UPDATE  #VT_FRESH_BUDGET_EXPORT 
  SET XXNM = '銷售數分佈'
  WHERE XX = 1
  ;
  UPDATE  #VT_FRESH_BUDGET_EXPORT 
  SET XXNM = '達成狀況'
  WHERE XX = 2
  ;
END SP;