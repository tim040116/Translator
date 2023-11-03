REPLACE PROCEDURE PMART.FUNC_FACT_STMARKETING_PRD_DOWNLOAD
(
   IN P_ACTID INTEGER,
   IN P_CLASSID INTEGER,
   IN P_FMCODE VARCHAR(7), 
   IN P_ORGTYPE VARCHAR(10),   
   IN P_ORGID VARCHAR(2000),
   IN P_TIMEID VARCHAR(2000),
   IN P_TIMETYPE VARCHAR(10)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000);    
	DECLARE ORGSELECT VARCHAR(2000);
	DECLARE ORGWHERE VARCHAR(2000);  
	DECLARE PRDWHERE VARCHAR(2000);  
    DECLARE TIMENAME  VARCHAR(100);  
    DECLARE TIMEWHERE  VARCHAR(2000);  	
	SET ORGWHERE = '';
	SET PRDWHERE = '';
	SET TIMENAME = '';
	SET TIMEWHERE = '';		
	IF P_CLASSID = '-1' THEN 
	   SET PRDWHERE = ' AND T1.ACT_ID = '+ P_ACTID;
	ELSE
	    IF P_FMCODE = '' THEN 
	        SET PRDWHERE = ' AND T1.ACT_ID = '+ P_ACTID + ' AND T1.CLASS_ID=' + P_CLASSID;
		ELSE
			SET PRDWHERE = ' AND T1.ACT_ID = '+ P_ACTID + ' AND T1.CLASS_ID=' + P_CLASSID  + ' AND T1.PRD_ID= ' + P_FMCODE ;
		END IF;
	END IF;
	IF P_ORGTYPE = '0' THEN
		SET ORGWHERE = ORGWHERE + ' AND A.PDEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '1' THEN
		SET ORGWHERE = ORGWHERE + ' AND A.DEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '2' THEN
		SET ORGWHERE = ORGWHERE + ' AND B.BRANCH_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '3' THEN
		SET ORGWHERE = ORGWHERE + ' AND C.RESPON_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '4' THEN
		SET ORGWHERE = ORGWHERE + ' AND C.OSTORE_ID = ' + P_ORGID;
	END IF;   
    IF P_TIMETYPE = 'Y' THEN
	    SET TIMENAME = 'T3.L_YEAR_NAME L_TIME_NM ';
		SET TIMEWHERE = ' JOIN PMART.TIME_Y T3 ON T1.TIME_ID = T3.L_YEAR_ID AND T3.L_YEAR_ID IN ('+P_TIMEID+') ';
   ELSEIF P_TIMETYPE = 'M' THEN
        SET TIMENAME = 'T3.L_MONTH_NAME L_TIME_NM ';
	    SET TIMEWHERE = ' JOIN PMART.TIME_M T3 ON T1.TIME_ID = T3.L_MONTH_ID AND T3.L_MONTH_ID IN  ('+P_TIMEID+') ';
   ELSE
		SET TIMENAME = 'T3.L_DAY_NAME L_TIME_NM ';
	    SET TIMEWHERE = ' JOIN PMART.TIME_D T3 ON T1.TIME_ID = T3.L_DAY_ID AND T3.L_DAY_ID IN  ('+P_TIMEID+') ';
   END IF;
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_STMARKETING_PRD_DOWNLOAD');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_STMARKETING_PRD_DOWNLOAD AS('
	    			     + ' SELECT T1.ORG_ID,T1.TIME_ID,T1.ACT_ID,T1.CLASS_ID,T2.DEPT_NO,T2.BRANCH_NO,T2.STORE_NO,T2.STORE_NAME'
						 + '                ,T1.PRD_ID,PRD_M.CLASS_NM,PRD_D.FM_NAME,T1.ORD_CNT,T1.SALE_CNT  ,T1.SALE_AMT, ' + TIMENAME +', T2.PDEPT_NO '
						 + '    FROM PMART.FACT_STMARKETING_PRD T1 '
	                     +'     INNER JOIN PDATA.STORMKT_PRD_M PRD_M ON T1.ACT_ID = PRD_M.ACT_ID AND T1.CLASS_ID = PRD_M.CLASS_ID '
						 +'     INNER JOIN PDATA.STORMKT_PRD_D PRD_D ON T1.ACT_ID = PRD_D.ACT_ID AND T1.CLASS_ID = PRD_D.CLASS_ID AND T1.PRD_ID = CAST(PRD_D.FM_CODE AS VARCHAR(7)) ';
	SET SQLSTR = SQLSTR + '  	 INNER JOIN ( '
					     + ' 	 		SELECT P.PDEPT_NO, A.DEPT_NO, B.BRANCH_NO, C.STORE_NO, C.STORE_NAME, C.OSTORE_ID '
						 + ' 			   FROM PMART.ORG_DEPT A '
					     + ' 			    JOIN PMART.ORG_BRANCH B ON A.DEPT_ID = B.DEPT_ID '
						 + ' 			    JOIN PMART.ORG_PDEPT P ON A.PDEPT_ID = P.PDEPT_ID '
					     + ' 				JOIN PMART.LAST_ORG_STORE C ON C.BRANCH_ID =  B.BRANCH_ID ';
    IF P_ORGID<> '' THEN 
        SET SQLSTR = SQLSTR + '			' + ORGWHERE;
    END IF;
	SET SQLSTR = SQLSTR + '		      ) T2 ON T1.ORG_ID = T2.OSTORE_ID '	
                	                 +    TIMEWHERE
	    	  				         + ' WHERE T1.TIME_ID IN ('+P_TIMEID+') '
									 +  PRDWHERE
					    	         + '  ) WITH DATA PRIMARY INDEX(TIME_ID,ACT_ID,CLASS_ID,PRD_ID) ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;