REPLACE PROCEDURE PMART.FUNC_IMPPRD_EXPORT
(
   IN P_TIMEID VARCHAR(1000),  
   IN P_ORGTYPE VARCHAR(20),  
   IN P_ORGID VARCHAR(20),      
   IN P_VERSION VARCHAR(10)  
)
SP:
BEGIN
   DECLARE SQLSTR  VARCHAR(20000);
   DECLARE ORG_REMDFACT VARCHAR(2000);
   DECLARE ORG_BASICMFACT VARCHAR(2000);
   DECLARE ORG_BASICDETAIL VARCHAR(2000);   
   DECLARE TIMEID INTEGER;
   DECLARE TIMEID_LWEEK INTEGER;
   DECLARE TIMEID_MONTH_S INTEGER;
   DECLARE TIMEID_MONTH_E INTEGER;
   DECLARE TIMEID_LMONTH_S INTEGER;
   DECLARE TIMEID_LMONTH_E INTEGER;
   DECLARE TIMEID_LYEAR INTEGER;
   DECLARE TIMEID_LYEAR_S INTEGER;
   DECLARE TIMEID_LYEAR_E INTEGER;
   DECLARE TIMEID_BLYEAR INTEGER;
   DECLARE TIMEID_BLYEAR_S INTEGER;
   DECLARE TIMEID_BLYEAR_E INTEGER;
   SET TIMEID =  CAST(CAST(P_TIMEID AS DATE) AS INTEGER) + 19000000;
   SET TIMEID_LWEEK = (SELECT L_DAY_LAST_WEEK FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID) ;
   SET TIMEID_MONTH_S = (SELECT L_MONTH_START_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID) ;
   SET TIMEID_MONTH_E = (SELECT L_MONTH_END_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID) ;
   SET TIMEID_LMONTH_S = (SELECT L_DAY_FIRST_DAY_LAST_MONTH FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID) ;
   SET TIMEID_LMONTH_E = (SELECT L_DAY_LAST_DAY_LAST_MONTH FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID) ;
   SET TIMEID_LYEAR = (SELECT CAST((CAST(P_TIMEID AS DATE)- INTERVAL '12' MONTH) AS INTEGER) + 19000000) ;
   SET TIMEID_LYEAR_S = (SELECT L_MONTH_START_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID_LYEAR) ;
   SET TIMEID_LYEAR_E = (SELECT L_MONTH_END_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID_LYEAR) ;
   SET TIMEID_BLYEAR = (SELECT CAST((CAST(P_TIMEID AS DATE)- INTERVAL '24' MONTH) AS INTEGER) + 19000000) ;
   SET TIMEID_BLYEAR_S = (SELECT L_MONTH_START_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID_BLYEAR) ;
   SET TIMEID_BLYEAR_E = (SELECT L_MONTH_END_DAY FROM PMART.YMWD_TIME WHERE L_DAY_ID= TIMEID_BLYEAR) ;
   IF TRIM(P_ORGTYPE) ='-1' THEN
      SET ORG_REMDFACT = ' ';
      SET ORG_BASICMFACT =' AND M.ORG_ID =-1 ';
      SET ORG_BASICDETAIL = ' ';
   ELSEIF TRIM(P_ORGTYPE)='0' THEN
      SET ORG_REMDFACT = 'AND O.PDEPT_ID = '|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICMFACT =' AND M.ORG_ID ='|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICDETAIL =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE PDEPT_ID='''|| TRIM(P_ORGID) ||''') ';
   ELSEIF TRIM(P_ORGTYPE)='1' THEN
      SET ORG_REMDFACT = 'AND O.DEPT_ID = '|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICMFACT =' AND M.ORG_ID ='|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICDETAIL =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID='''|| TRIM(P_ORGID) ||''') ';
   ELSEIF TRIM(P_ORGTYPE)='2' THEN
      SET ORG_REMDFACT = 'AND O.BRANCH_ID = '|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICMFACT =' AND M.ORG_ID ='|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICDETAIL =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID='''|| TRIM(P_ORGID) ||''') ';
   ELSEIF TRIM(P_ORGTYPE)='3' THEN
      SET ORG_REMDFACT = 'AND O.RESPON_ID = '|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICMFACT =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE RESPON_ID='''|| TRIM(P_ORGID) ||''') ';
      SET ORG_BASICDETAIL =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE RESPON_ID='''|| TRIM(P_ORGID) ||''') ';
   ELSEIF TRIM(P_ORGTYPE)='4' THEN
      SET ORG_REMDFACT = 'AND O.OSTORE_ID = '''|| TRIM(P_ORGID) ||'''';
      SET ORG_BASICMFACT =' AND M.ORG_ID ='|| TRIM(P_ORGID) ||' ';
      SET ORG_BASICDETAIL =' AND M.ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE OSTORE_ID='''|| TRIM(P_ORGID) ||''') ';
   END IF;	
   CALL PMART.P_DROP_TABLE ('#VT_IMPPRD_EXPORT');
   SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_IMPPRD_EXPORT,NO FALLBACK,NO JOURNAL,NO LOG ( '
             || ' ITEM_PROJECT  VARCHAR(50) CHARACTER SET UNICODE, '
             || ' ITEM_NM  VARCHAR(50) CHARACTER SET UNICODE, '
             || ' ITEM_SEQ  INTEGER, '
             || ' CAL_TYPE  VARCHAR(50) CHARACTER SET UNICODE, '
             || ' PSD_D01 DECIMAL(10,1), '
             || ' PSD_D02 DECIMAL(10,1), '
             || ' PSD_D03 DECIMAL(10,1), '
             || ' PSD_D04 DECIMAL(10,1), '
             || ' PSD_D05 DECIMAL(10,1), '
             || ' PSD_D06 DECIMAL(10,1), '
             || ' PSD_D07 DECIMAL(10,1), '
             || ' PSD_D08 DECIMAL(10,1), '
             || ' PSD_MONTH DECIMAL(10,1), '
             || ' PSD_MONTH_LAST DECIMAL(10,1), '
             || ' PSD_MONTH_LYEAR DECIMAL(10,1), '
             || ' PSD_MONTH_BLYEAR DECIMAL(10,1), '
             || ' PSD_D08_DAYDIFF DECIMAL(10,1), '
             || ' PSD_D08_WEEKDIFF DECIMAL(10,1), '
             || ' PSD_MONTH_LASTDIFF DECIMAL(10,1), '
             || ' PSD_MONTH_LYEARDIFF DECIMAL(10,1), '
             || ' PSD_MONTH_BLYEARDIFF DECIMAL(10,1), '
             || ' REMARK INTEGER '           
             ||') PRIMARY INDEX(ITEM_PROJECT) ON COMMIT PRESERVE ROWS; ';  
   EXECUTE IMMEDIATE SQLSTR;   
  SET SQLSTR = 'INSERT INTO #VT_IMPPRD_EXPORT ( '
						 || ' ITEM_PROJECT, ITEM_NM, ITEM_SEQ, CAL_TYPE, '
						 || ' PSD_D01,PSD_D02,PSD_D03,PSD_D04,PSD_D05,PSD_D06,PSD_D07,PSD_D08, '
						 || ' PSD_MONTH, PSD_MONTH_LAST, PSD_MONTH_LYEAR, PSD_MONTH_BLYEAR, REMARK) '
						 || ' WITH ';
   SET SQLSTR =SQLSTR 
						 || ' T_TIME AS( ' 
						 || ' SELECT ''PSD_D0'' || TRIM(ROW_NUMBER() OVER (ORDER BY L_DAY_ID)) AS L_TYPE, L_DAY_ID ' 
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LWEEK||' AND '||TIMEID||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH'' AS L_TYPE, L_DAY_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_MONTH_S||' AND '||TIMEID_MONTH_E||' AND L_DAY_ID<='||TIMEID||' '
						 || '  UNION '  
						 || ' SELECT ''PSD_MONTH_LAST'' AS L_TYPE,L_DAY_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LMONTH_S||' AND '||TIMEID_LMONTH_E||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH_LYEAR'' AS L_TYPE,L_DAY_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LYEAR_S||' AND '||TIMEID_LYEAR_E||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH_BLYEAR'' AS L_TYPE,L_DAY_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_BLYEAR_S||' AND '||TIMEID_BLYEAR_E||' '
						 || '  ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_STOR AS ( '
						 || ' SELECT D.L_TYPE, SUM(F.UPLOAD_STNUM) STNUM '
						 || '   FROM T_TIME D  '
						 || '   JOIN PMART.REMD_FACT F '
						 || '     ON  D.L_DAY_ID=F.L_DAY_ID '
						 || '   JOIN LAST_ORG_DIM O '
						 || '     ON F.OSTORE_ID = O.OSTORE_ID '
						 || ORG_REMDFACT
						 || ' WHERE 1=1 '
						 || '  GROUP BY D.L_TYPE '
						 || ' ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_STOR_CNT AS ( '
						 || ' SELECT PRD_ID, L_TYPE, COUNT(0) STNUM_ORG '
						 || '   FROM PMART.BASIC_MFACT M '
						 || '        JOIN T_TIME S '
						 || '           ON M.TIME_ID = S.L_DAY_ID '
						 || '  WHERE (TIME_ID BETWEEN '||TIMEID_LMONTH_S||' AND '||TIMEID||' OR '
						 || '        TIME_ID BETWEEN '||TIMEID_LYEAR_S||' AND '||TIMEID_LYEAR_E||' OR '
						 || '        TIME_ID BETWEEN '||TIMEID_BLYEAR_S||' AND '||TIMEID_BLYEAR_E||') '
						 || '    AND (SALES_CNT)>0 '
						 || '    AND PRD_ID IN (SELECT R.PRD_ID  '
						 || '                     FROM PMART.PB_IMPORTANT_RPT_DTL R  '
						 || '                     JOIN PRD_KND A  '
						 || '                       ON A.KND_ID = R.PRD_ID  '
						 || '                    WHERE R.VERSION_NO = ''' || P_VERSION ||'''  '
						 || '                      AND A.KND_ID IN (''02'',''03'',''04'',''05'',''B2'')  '
						 || '                   UNION  '
						 || '                   SELECT R.PRD_ID  '
						 || '                     FROM PMART.PB_IMPORTANT_RPT_DTL R  '
						 || '                     JOIN PRD_GRP A  '
						 || '                       ON A.GRP_ID = R.PRD_ID  '
						 || '                    WHERE R.VERSION_NO = ''' || P_VERSION ||'''  '
						 || '                      AND A.KND_ID IN (''02'',''03'',''04'',''05'',''B2'') '
						 || '                       OR A.GRP_ID IN (''226''))  '
						 || ORG_BASICDETAIL
						 || '    AND ORG_ID BETWEEN 0 AND 999999 '
						 || '  GROUP BY PRD_ID, L_TYPE '
						 || '  UNION '
						 || ' SELECT PRD_ID, L_TYPE, COUNT(0) STNUM_ORG '
						 || '   FROM PMART.BASIC_MFACT_DETAIL M  '
						 || '        JOIN T_TIME S '
						 || '          ON M.TIME_ID = S.L_DAY_ID '
						 || '  WHERE (TIME_ID BETWEEN '||TIMEID_LMONTH_S||' AND '||TIMEID||' OR '
						 || '        TIME_ID BETWEEN '||TIMEID_LYEAR_S||' AND '||TIMEID_LYEAR_E||' OR '
						 || '        TIME_ID BETWEEN '||TIMEID_BLYEAR_S||' AND '||TIMEID_BLYEAR_E||') '
						 || '    AND (SALES_CNT)>0 '
						 || '    AND PRD_ID IN (''0007958'') '
						 || ORG_BASICDETAIL
						 || '  GROUP BY PRD_ID,L_TYPE '
						 || ' ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_DATE AS( ' 
						 || ' SELECT ''PSD_D0'' || TRIM(ROW_NUMBER() OVER (ORDER BY L_DAY_ID)) AS L_TYPE, L_DAY_ID TIME_ID ' 
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LWEEK||' AND '||TIMEID||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH'' AS L_TYPE, L_DAY_ID TIME_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_MONTH_S||' AND '||TIMEID_MONTH_E||' AND L_DAY_ID<='||TIMEID||' '
						 || '  UNION '  
						 || ' SELECT ''PSD_MONTH_LAST'' AS L_TYPE, L_MONTH_ID TIME_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LMONTH_S||' AND '||TIMEID_LMONTH_E||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH_LYEAR'' AS L_TYPE, L_MONTH_ID TIME_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_LYEAR_S||' AND '||TIMEID_LYEAR_E||' '
						 || '  UNION '
						 || ' SELECT ''PSD_MONTH_BLYEAR'' AS L_TYPE, L_MONTH_ID TIME_ID '
						 || '   FROM PMART.YMWD_TIME '
						 || '  WHERE L_DAY_ID BETWEEN '||TIMEID_BLYEAR_S||' AND '||TIMEID_BLYEAR_E||' '
						 || '  ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_PRD AS ( '
						 || '  SELECT R.ITEM_PROJECT, '
						 || '        		 R.ITEM_NM, '
						 || '        		 R.ITEM_SEQ, '
						 || '        		 R.PRD_ID, '
						 || '        		 R.CAL_TYPE, '
						 || '        		 S.L_TYPE, '  
						 || '        		 NULL SALES '
						 || '      FROM PMART.PB_IMPORTANT_RPT_DTL R '
						 || '        JOIN T_DATE S '
						 || '          ON  R.VERSION_NO = ''' || P_VERSION ||''' '
						 || '  ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_PRD_FACT AS ( '
						 || '  SELECT R.ITEM_PROJECT, '
						 || '        		 R.ITEM_NM, '
						 || '        		 R.ITEM_SEQ, '
						 || '        		 R.PRD_ID, '
						 || '        		 R.CAL_TYPE, '
						 || '        		 S.L_TYPE, ' 
						 || '        		 CASE WHEN R.CAL_TYPE = ''C'' THEN M.SALES_CNT ELSE (M.SALES_AMT-M.DIS_AMT-M.SUB_AMT) END SALES '
						 || '      FROM PMART.PB_IMPORTANT_RPT_DTL R '
						 || '        JOIN T_DATE S '
						 || '          ON  R.VERSION_NO = ''' || P_VERSION ||''' '
						 || '        JOIN PMART.BASIC_MFACT M '
						 || '           ON M.PRD_ID = R.PRD_ID '
						 || '    	  AND M.TIME_ID = S.TIME_ID '
						 || ORG_BASICMFACT
						 || '  ), ';
   SET SQLSTR =SQLSTR 
						 || ' T_PRD_DETAIL AS ( '
						 || '  SELECT R.ITEM_PROJECT, '
						 || '        		 R.ITEM_NM, '
						 || '        		 R.ITEM_SEQ, '
						 || '        		 R.PRD_ID, '
						 || '        		 R.CAL_TYPE, '
						 || '        		 S.L_TYPE, ' 
						 || '        		 CASE WHEN R.CAL_TYPE = ''C'' THEN M.SALES_CNT ELSE (M.SALES_AMT-M.DIS_AMT-M.SUB_AMT) END SALES '
						 || '      FROM PMART.PB_IMPORTANT_RPT_DTL R '
						 || '        JOIN T_DATE S '
						 || '          ON  R.VERSION_NO = ''' || P_VERSION ||''' '
						 || '        JOIN PMART.BASIC_MFACT_DETAIL M '
						 || '           ON M.PRD_ID = R.PRD_ID '
						 || '    	  AND M.TIME_ID = S.TIME_ID '
						 || ORG_BASICMFACT
						 || ' WHERE ' || TRIM(P_ORGTYPE) ||'  IN (3,4) ' 
						 || '  ), ';
   IF TRIM(P_ORGTYPE) NOT IN ('3','4') THEN
		   SET SQLSTR =SQLSTR 
								 || ' T_PRD_EXCLUDE AS ( '
								 || '  SELECT R.ITEM_PROJECT, '
								 || '        		 R.ITEM_NM, '
								 || '        		 R.ITEM_SEQ, '
								 || '        		 R.PRD_ID, '
								 || '        		 R.CAL_TYPE, '
								 || '        		 S.L_TYPE, ' 
								 || '                CASE WHEN R.CAL_TYPE = ''C'' THEN M.SALES_CNT*-1 ELSE (M.SALES_AMT-M.DIS_AMT-M.SUB_AMT)*-1 END SALES '
								 || '      FROM PMART.PB_IMPORTANT_RPT_DTL R '
								 || '        JOIN T_DATE S '
						 		 || '          ON  R.VERSION_NO = ''' || P_VERSION ||''' '
								 || '         AND R.PRD_ID IN (''14'',''141'') '
								 || '         AND R.CAL_TYPE = ''C'' '
								 || '        JOIN PMART.BASIC_MFACT M '
								 || '           ON M.PRD_ID IN (''1414708'', ''1414712'', ''1414726'', ''1414730'', ''1414758'', ''1414762'')  '
								 || '         AND M.TIME_ID = S.TIME_ID '
								 || ORG_BASICMFACT
								 || '  ), ';
   ELSE	
		   SET SQLSTR =SQLSTR 
								 || ' T_PRD_EXCLUDE AS ( '
								 || '  SELECT R.ITEM_PROJECT, '
								 || '        		 R.ITEM_NM, '
								 || '        		 R.ITEM_SEQ, '
								 || '        		 R.PRD_ID, '
								 || '        		 R.CAL_TYPE, '
								 || '        		 S.L_TYPE, ' 
								 || '                CASE WHEN R.CAL_TYPE = ''C'' THEN M.SALES_CNT*-1 ELSE (M.SALES_AMT-M.DIS_AMT-M.SUB_AMT)*-1 END SALES '
								 || '      FROM PMART.PB_IMPORTANT_RPT_DTL R '
								 || '        JOIN T_DATE S '
						 		 || '          ON  R.VERSION_NO = ''' || P_VERSION ||''' '
								 || '         AND R.PRD_ID IN (''14'',''141'') '
								 || '         AND R.CAL_TYPE = ''C'' '
								 || '        JOIN PMART.BASIC_MFACT_DETAIL M '
								 || '           ON M.PRD_ID IN (''1414708'', ''1414712'', ''1414726'', ''1414730'', ''1414758'', ''1414762'')  '
								 || '         AND M.TIME_ID = S.TIME_ID '
								 || ORG_BASICMFACT
								 || '  ), ';
   END IF;	
   SET SQLSTR =SQLSTR 
						 || ' T_PRD_SUMARY AS ( '
						 || ' SELECT ITEM_PROJECT, '
						 || '        ITEM_NM, '
						 || '        T.L_TYPE, '
						 || '        CAL_TYPE, '
						 || '        MIN(ITEM_SEQ) AS ITEM_SEQ, '
						 || '        SUM(SALES) AS SALES, '
						 || '        MAX(CASE WHEN SC.STNUM_ORG IS NOT NULL THEN STNUM_ORG ELSE STNUM END) STNUM '
						 || '   FROM (SELECT * FROM T_PRD '
						 || '   UNION SELECT * FROM T_PRD_FACT '
						 || '   UNION SELECT * FROM T_PRD_DETAIL '
						 || '   UNION SELECT * FROM T_PRD_EXCLUDE) T'
						 || '     LEFT JOIN T_STOR S '
						 || '       ON T.L_TYPE = S.L_TYPE '
						 || '     LEFT JOIN T_STOR_CNT SC '
						 || '       ON T.L_TYPE = SC.L_TYPE '
						 || '      AND T.PRD_ID = SC.PRD_ID '
						 || '      AND T.CAL_TYPE=''C''  '
						 || '    GROUP BY ITEM_PROJECT, ITEM_NM, T.L_TYPE, CAL_TYPE '
						 || ' ) ';
   SET SQLSTR =SQLSTR 
						|| ' SELECT ITEM_PROJECT, '
						|| ' 			 ITEM_NM, '
						|| ' 			 ITEM_SEQ, '
						|| '			 CAL_TYPE, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D01'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D01'' THEN STNUM ELSE NULL END ) PSD_D01, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D02'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D02'' THEN STNUM ELSE NULL END ) PSD_D02, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D03'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D03'' THEN STNUM ELSE NULL END ) PSD_D03, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D04'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D04'' THEN STNUM ELSE NULL END ) PSD_D04, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D05'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D05'' THEN STNUM ELSE NULL END ) PSD_D05, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D06'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D06'' THEN STNUM ELSE NULL END ) PSD_D06, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D07'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D07'' THEN STNUM ELSE NULL END ) PSD_D07, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D08'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_D08'' THEN STNUM ELSE NULL END ) PSD_D08, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_MONTH'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_MONTH'' THEN STNUM ELSE NULL END ) PSD_MONTH, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_MONTH_LAST'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_MONTH_LAST'' THEN STNUM ELSE NULL END ) PSD_MONTH_LAST, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_MONTH_LYEAR'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_MONTH_LYEAR'' THEN STNUM ELSE NULL END ) PSD_MONTH_LYEAR, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_MONTH_BLYEAR'' THEN SALES ELSE NULL END )*1.0 /SUM(CASE WHEN L_TYPE =''PSD_MONTH_BLYEAR'' THEN STNUM ELSE NULL END ) PSD_MONTH_BLYEAR, '
						|| ' 			 SUM(CASE WHEN L_TYPE =''PSD_D08'' THEN STNUM ELSE NULL END ) REMARK '
						|| '   FROM T_PRD_SUMARY T '
						|| '  GROUP BY ITEM_PROJECT, ITEM_NM, ITEM_SEQ, CAL_TYPE ';
	EXECUTE IMMEDIATE SQLSTR;  
	DELETE PMART.T1 WHERE F1=1902;
	INSERT INTO PMART.T1(F1,F2) SELECT 1902,SQLSTR;		
   SET SQLSTR = ' UPDATE #VT_IMPPRD_EXPORT '
						 || ' SET PSD_D08_DAYDIFF      = PSD_D08 - PSD_D07, '
						 || '     PSD_D08_WEEKDIFF     = PSD_D08 - PSD_D01, '
						 || '     PSD_MONTH_LASTDIFF   = PSD_MONTH - PSD_MONTH_LAST, '
						 || '     PSD_MONTH_LYEARDIFF  = PSD_MONTH - PSD_MONTH_LYEAR, '
						 || '     PSD_MONTH_BLYEARDIFF = PSD_MONTH - PSD_MONTH_BLYEAR ';
	EXECUTE IMMEDIATE SQLSTR;  
   SET SQLSTR = ' INSERT #VT_IMPPRD_EXPORT '
						 || ' SELECT ''TOTAL'' AS ITEM_PROJECT, '
						 || '        ''TOTAL'' AS ITEM_NM, '
						 || '        9999 ITEM_SEQ, '
						 || '        ''A'' CAL_TYPE, '
						 || '        SUM(PSD_D01) PSD_D01, '
						 || '        SUM(PSD_D02) PSD_D02, '
						 || '        SUM(PSD_D03) PSD_D03, '
						 || '        SUM(PSD_D04) PSD_D04, '
						 || '        SUM(PSD_D05) PSD_D05, '
						 || '        SUM(PSD_D06) PSD_D06, '
						 || '        SUM(PSD_D07) PSD_D07, '
						 || '        SUM(PSD_D08) PSD_D08, '
						 || '        SUM(PSD_MONTH) PSD_MONTH, '
						 || '        SUM(PSD_MONTH_LAST) PSD_MONTH_LAST, '
						 || '        SUM(PSD_MONTH_LYEAR) PSD_MONTH_LYEAR, '
						 || '        SUM(PSD_MONTH_BLYEAR) PSD_MONTH_BLYEAR, '
						 || '        SUM(PSD_D08_DAYDIFF) PSD_D08_DAYDIFF, '
						 || '        SUM(PSD_D08_WEEKDIFF) PSD_D08_WEEKDIFF, '
						 || '        SUM(PSD_MONTH_LASTDIFF) PSD_MONTH_LASTDIFF, '
						 || '        SUM(PSD_MONTH_LYEARDIFF) PSD_MONTH_LYEARDIFF, '
						 || '        SUM(PSD_MONTH_BLYEARDIFF) PSD_MONTH_BLYEARDIFF, '
						 || '        MAX(REMARK) REMARK '
						 || '   FROM #VT_IMPPRD_EXPORT '
						 || '  WHERE ITEM_PROJECT = ( '
						 || '        SELECT ITEM_PROJECT FROM #VT_IMPPRD_EXPORT  '
						 || '         WHERE ITEM_SEQ = (SELECT MAX(ITEM_SEQ) FROM #VT_IMPPRD_EXPORT WHERE CAL_TYPE=''A'') '
						 || '        ) ';
	EXECUTE IMMEDIATE SQLSTR;  
END SP;