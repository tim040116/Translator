REPLACE PROCEDURE PMART.MT_CTFACT_FUNC (
FP_TIME_ID NUMBER,
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER,
FP_MMA_LIST VARCHAR(1000))
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000);
DECLARE V_TABLE_NAME VARCHAR(30);
DECLARE V_MMA_ID NUMBER;
DECLARE V_TR00 NUMBER;
DECLARE V_TR01 NUMBER;
DECLARE V_TR02 NUMBER;
DECLARE V_TR03 NUMBER;
DECLARE V_TR04 NUMBER;
DECLARE V_TR05 NUMBER;
DECLARE V_TR06 NUMBER;
DECLARE V_TR07 NUMBER;
DECLARE V_TR08 NUMBER;
DECLARE V_TR09 NUMBER;
DECLARE V_TR10 NUMBER;
DECLARE V_TR11 NUMBER;
DECLARE V_TR12 NUMBER;
DECLARE V_TR13 NUMBER;
DECLARE V_TR14 NUMBER;
DECLARE V_TR15 NUMBER;
DECLARE V_TR16 NUMBER;
DECLARE V_TR17 NUMBER;
DECLARE V_TR18 NUMBER;
DECLARE V_TR19 NUMBER;
DECLARE V_TR20 NUMBER;
DECLARE V_TR21 NUMBER;
DECLARE V_TR22 NUMBER;
DECLARE V_TR23 NUMBER;
DECLARE V_PARAM  VARCHAR(200); 
   CALL PMART.P_DROP_TABLE ('#VT_MT_CTFACT_FUNC'); 
   CALL PMART.P_DROP_TABLE ('#VT_MT_CTFACT_FUNC_T1');  
   SET SQLSTR =  'CREATE MULTISET VOLATILE TABLE #VT_MT_CTFACT_FUNC  
   (
     MMA_ID		NUMBER,
     TR_ID		NUMBER,
     CUST_NUM	NUMBER)
   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
   EXECUTE IMMEDIATE SQLSTR;
   IF FP_ORG_LEVEL=0 THEN
      SET  V_PARAM = 'TOT_ID='+FP_ORG_ID +'  AND MMA_ID IN ('+ FP_MMA_LIST +') ';
   ELSEIF FP_ORG_LEVEL=1 THEN
      SET V_PARAM = 'DEPT_ID='+FP_ORG_ID +'  AND MMA_ID IN ('+ FP_MMA_LIST +') ';
   ELSEIF FP_ORG_LEVEL=2 THEN
      SET V_PARAM = 'BRANCH_ID='+FP_ORG_ID +' AND MMA_ID IN ('+ FP_MMA_LIST +') ';
   END IF;
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_MT_CTFACT_FUNC_T1  AS('+
      'SELECT '+
      'B.MMA_ID,'+
      'SUM(A.TR00) AS TR00,SUM(A.TR01) AS TR01,'+
      'SUM(A.TR02) AS TR02,SUM(A.TR03) AS TR03,'+
      'SUM(A.TR04) AS TR04,SUM(A.TR05) AS TR05,'+
      'SUM(A.TR06) AS TR06,SUM(A.TR07) AS TR07,'+
      'SUM(A.TR08) AS TR08,SUM(A.TR09) AS TR09,'+
      'SUM(A.TR10) AS TR10,SUM(A.TR11) AS TR11,'+
      'SUM(A.TR12) AS TR12,SUM(A.TR13) AS TR13,'+
      'SUM(A.TR14) AS TR14,SUM(A.TR15) AS TR15,'+
      'SUM(A.TR16) AS TR16,SUM(A.TR17) AS TR17,'+
      'SUM(A.TR18) AS TR18,SUM(A.TR19) AS TR19,'+
      'SUM(A.TR20) AS TR20,SUM(A.TR21) AS TR21,'+
      'SUM(A.TR22) AS TR22,SUM(A.TR23) AS TR23 '+
      'FROM '+
      '( '+
      'SELECT  *  '+
      'FROM PMART.SALES_CTFACT '+
      'WHERE TIME_ID='+ FP_TIME_ID +' '+
      'AND ORG_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_MMA_DIM '+
      'WHERE '+ V_PARAM + ') '+
      ') A,PMART.LAST_ORG_MMA_DIM B '+
      'WHERE A.ORG_ID=B.OSTORE_ID '+
      'GROUP BY B.MMA_ID '+ 
      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
	L1:
	FOR CUR1  AS VT_MT_CTFACT_FUNC_C1 CURSOR  FOR 
	    SELECT MMA_ID,
             TR00,TR01,
             TR02,TR03,
             TR04,TR05,
             TR06,TR07,
             TR08,TR09,
             TR10,TR11,
             TR12,TR13,
             TR14,TR15,
             TR16,TR17,
             TR18,TR19,
             TR20,TR21,
             TR22,TR23              
      FROM 	#VT_MT_CTFACT_FUNC_T1
   DO
  	  SET  V_MMA_ID = CUR1.MMA_ID;
      SET V_TR00 = CUR1.TR00;SET V_TR01 = CUR1.TR01;
      SET V_TR02 = CUR1.TR02;SET V_TR03 = CUR1.TR03;
      SET V_TR04 = CUR1.TR04;SET V_TR05 = CUR1.TR05;
      SET V_TR06 = CUR1.TR06;SET V_TR07 = CUR1.TR07;
      SET V_TR08 = CUR1.TR08;SET V_TR09 = CUR1.TR09;
      SET V_TR10 = CUR1.TR10;SET V_TR11 = CUR1.TR11;
      SET V_TR12 = CUR1.TR12;SET V_TR13 = CUR1.TR13;
      SET V_TR14 = CUR1.TR14;SET V_TR15 = CUR1.TR15;
      SET V_TR16 = CUR1.TR16;SET V_TR17 = CUR1.TR17;
      SET V_TR18 = CUR1.TR18;SET V_TR19 = CUR1.TR19;
      SET V_TR20 = CUR1.TR20;SET V_TR21 = CUR1.TR21;
      SET V_TR22 = CUR1.TR22;SET V_TR23 = CUR1.TR23;
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,0,V_TR00);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,1,V_TR01);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,2,V_TR02);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,3,V_TR03);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,4,V_TR04);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,5,V_TR05);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,6,V_TR06);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,7,V_TR07);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,8,V_TR08);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,9,V_TR09);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,10,V_TR10);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,11,V_TR11);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,12,V_TR12);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,13,V_TR13);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,14,V_TR14);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,15,V_TR15);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,16,V_TR16);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,17,V_TR17);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,18,V_TR18);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,19,V_TR19); 
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,20,V_TR20);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,21,V_TR21);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,22,V_TR22);
     INSERT INTO #VT_MT_CTFACT_FUNC (MMA_ID,TR_ID,CUST_NUM) VALUES (V_MMA_ID,23,V_TR23);      
   END FOR L1;
END SP;