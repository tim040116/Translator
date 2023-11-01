REPLACE PROCEDURE PMART.OLD0401_WDWH_5_1_PRD_QRY_FUNC
(
   IN I_ORG_LEVEL  VARCHAR(2),   
   IN I_ORG_ID    VARCHAR(9),     
   IN I_KND_ID  INTEGER
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE M_SQLSTR            VARCHAR(1000) DEFAULT '';
	DECLARE M_SQLSELECT      VARCHAR(500) DEFAULT '';
	DECLARE M_SQLWHERE     VARCHAR(100) DEFAULT '';
  CALL PMART.P_DROP_TABLE ('#VT_WDWH_5_1_PRD_QRY_FUNC'); 
IF I_KND_ID <> 0 THEN
	 SET M_SQLSELECT  = '  SELECT DISTINCT  1 AS FLAG, A.GRP_NO , B.GRP_NM AS GRP_NAME , A.GRP_ID  ';
ELSE 
     SET M_SQLSELECT  = '  SELECT DISTINCT  1 AS FLAG, A.KND_NO, B.KND_NM AS KND_NAME, A.KND_ID  ';
END IF;	
   IF  I_ORG_LEVEL = '1' THEN
  		SET M_SQLWHERE =  ' ' ;
  END IF;
    IF  I_ORG_LEVEL = '6' THEN
  		SET M_SQLWHERE =  ' WHERE A.DEPT_NO = ''' + I_ORG_ID +'''' ;
    END IF;
    IF  I_ORG_LEVEL = '7' THEN
  		SET M_SQLWHERE =  ' WHERE A.DEPT_NO =''' + I_ORG_ID +'''' ;
    END IF;
    IF  I_ORG_LEVEL = '8'THEN
  		SET M_SQLWHERE =  ' WHERE A.BRANCH_NO = ''' + I_ORG_ID +'''' ;
    END IF;
    IF  I_ORG_LEVEL = '9' THEN
  		SET M_SQLWHERE =  ' WHERE A.RESPON_NO =  ''' + I_ORG_ID +'''' ;	
    END IF; 
	IF I_KND_ID <> 0 THEN	
		SET M_SQLWHERE = M_SQLWHERE   + ' AND  A.KND_ID =  ' + I_KND_ID + '';
	END IF;		
	  SET M_SQLSTR  = ' CREATE MULTISET VOLATILE TABLE #VT_WDWH_5_1_PRD_QRY_FUNC AS(	'
                           + M_SQLSELECT
                           + '  FROM PMART.ORG_DIM_POSI1 AS A '
						   + '  INNER JOIN PMART.PRD_DIM AS B ON A.GRP_ID = B.GRP_ID ' ;
	SET M_SQLSTR  =  M_SQLSTR + M_SQLWHERE ;
	SET M_SQLSTR  =  M_SQLSTR +  '  ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE M_SQLSTR;   
	IF I_KND_ID = 0 THEN
	     INSERT INTO  #VT_WDWH_5_1_PRD_QRY_FUNC(FLAG, KND_NO, KND_NAME, KND_ID ) SELECT 0, '0', '
	ELSE 
		 INSERT INTO  #VT_WDWH_5_1_PRD_QRY_FUNC(FLAG,  GRP_NO, GRP_NAME , GRP_ID) SELECT 0,  '0', '
	END IF;
END SP;