REPLACE PROCEDURE PMART.FUNC_FACT_STMARKETING_AGN
(
   IN P_ACTID INTEGER,
   IN P_AGNID VARCHAR(3),   
   IN P_ORGTYPE VARCHAR(10),   
   IN P_ORGID VARCHAR(2000),
   IN P_TIMEID VARCHAR(2000)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000);    
	DECLARE ORGSELECT VARCHAR(2000);
	DECLARE ORGWHERE VARCHAR(2000);  
	DECLARE AGNWHERE VARCHAR(2000);  
	DECLARE SELCOL VARCHAR(2000);  
	DECLARE GRPCOL VARCHAR(2000);  
	SET ORGWHERE = '';
	SET AGNWHERE = '';
	SET SELCOL = '';
	SET GRPCOL ='';
	IF P_AGNID = '-1' THEN 
	    SET SELCOL = 'T1.TIME_ID,T1.ACT_ID,AGN_DIM.PARENT_AGN_ID AGN_ID, ''Y'' ALLOW_DRILL_DOWN ';
		SET GRPCOL = 'T1.TIME_ID,T1.ACT_ID,AGN_DIM.PARENT_AGN_ID';
	ELSE
	    SET SELCOL = 'T1.TIME_ID,T1.ACT_ID,AGN_DIM.AGN_ID , ''N'' ALLOW_DRILL_DOWN ';
		SET GRPCOL = 'T1.TIME_ID,T1.ACT_ID,AGN_DIM.AGN_ID ';
	    SET AGNWHERE = ' AND AGN_DIM.PARENT_AGN_ID = '''+ P_AGNID +'''  ';
	END IF;
	IF P_ORGTYPE = '0' THEN
		SET ORGSELECT = 'DEPT_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND PDEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '1' THEN
		SET ORGSELECT = 'BRANCH_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND DEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '2' THEN
		SET ORGSELECT = 'RESPON_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND BRANCH_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '3' THEN
		SET ORGSELECT = 'OSTORE_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND RESPON_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '4' THEN
		SET ORGSELECT = 'OSTORE_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND OSTORE_ID = ' + P_ORGID;
	ELSE
		SET ORGSELECT = 'PDEPT_ID AS GROUP_ORG_ID, OSTORE_ID';
	END IF;   
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_STMARKETING_AGN');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_STMARKETING_AGN AS('
					     + '  SELECT  DAT.TIME_ID,DAT.ACT_ID,DAT.AGN_ID,VW_AGN_DIM.AGN_SNAME,AGN_CNT,DAT.ALLOW_DRILL_DOWN,  VW_AGN_DIM.AGN_LEVEL  FROM ( '
	    			     + ' SELECT  ' + SELCOL
						 + '                ,SUM(T1.AGN_CNT) AS AGN_CNT '
						 + '    FROM PMART.FACT_STMARKETING_AGN T1 INNER JOIN PMART.VW_AGN_DIM AGN_DIM ON T1.AGN_ID = AGN_DIM.AGN_ID '
						 + '  	 INNER JOIN ( '
						 + '	                     	SELECT ' + ORGSELECT 
  					     + '			                    FROM PMART.LAST_ORG_STORE '
						 + '                            WHERE 1=1 ';
    IF P_ORGID<> '' THEN 
        SET SQLSTR = SQLSTR + '			' + ORGWHERE;
    END IF;
	SET SQLSTR = SQLSTR + '		      ) T2 ON T1.ORG_ID = T2.OSTORE_ID '	
	    	  				         + ' WHERE T1.TIME_ID IN ('+P_TIMEID+')'				 
			    			         + '       AND T1.ACT_ID = '  + P_ACTID ;				
	IF AGNWHERE<> '' THEN 
        SET SQLSTR = SQLSTR + '			' + AGNWHERE;
    END IF;						
	SET SQLSTR = SQLSTR + ' GROUP BY  '  + GRPCOL + '     '
	                                 + ' ) DAT INNER JOIN PMART.VW_AGN_DIM  ON DAT.AGN_ID = VW_AGN_DIM.AGN_ID '
                                     + ' ) WITH DATA PRIMARY INDEX(TIME_ID,ACT_ID,AGN_ID) ON COMMIT PRESERVE ROWS;'; 
    EXECUTE IMMEDIATE SQLSTR;   
END SP;