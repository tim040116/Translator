REPLACE PROCEDURE PMART.POSN_1_9_1_FUNC
(
FP_TIME_TYPE CHAR,         
FP_TIME_LIST VARCHAR(400), 
FP_MMA_TYPE  NUMBER,       
FP_MMA_LIST  VARCHAR(400), 
FP_ORG_LEVEL NUMBER,       
FP_ORG_LIST  VARCHAR(400), 
FP_PRD_TYPE  VARCHAR(400), 
FP_PRD_LEVEL VARCHAR(400), 
FP_PRD_ID    VARCHAR(1000), 
FP_AMT_TYPE  VARCHAR(400)  
)            
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR     VARCHAR(8000);
   DECLARE V_SQL      VARCHAR(8000);
   CALL PMART.P_DROP_TABLE ('#VT_POSN_1_9_1_FUNC'); 
   CALL PMART.POSN_MFACT_FUNC (FP_TIME_TYPE,FP_TIME_LIST,FP_PRD_LEVEL,FP_PRD_ID,1,FP_ORG_LEVEL,FP_ORG_LIST,FP_MMA_TYPE,FP_MMA_LIST,FP_PRD_TYPE,FP_AMT_TYPE);
   CALL PMART.POSN_STFACT_FUNC(FP_TIME_TYPE,FP_TIME_LIST,FP_PRD_LEVEL,FP_PRD_ID,1,FP_ORG_LEVEL,FP_ORG_LIST,FP_MMA_TYPE,FP_MMA_LIST,FP_PRD_TYPE);
   CALL PMART.POSN_SFACT_FUNC (FP_TIME_TYPE,FP_TIME_LIST,FP_ORG_LEVEL,FP_ORG_LIST,FP_MMA_TYPE,FP_MMA_LIST);
   SET V_SQL =
          'SUM(B.ORDER_STORE_NUM) AS ORDERSTORE_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(B.ORDER_STORE_NUM), SUM(C.MAST_STORE_NUM)) AS ORDERSTORE_RATIO, '+ 
          'SUM(B.INPRD_STORE_NUM) AS INPRDSTORE_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(B.INPRD_STORE_NUM), SUM(C.MAST_STORE_NUM)) AS INPRDSTORE_RATIO, '+ 
          'SUM(B.SALES_STORE_NUM) AS SALESSTORE_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(B.SALES_STORE_NUM), SUM(C.MAST_STORE_NUM)) AS SALESSTORE_RATIO, '+ 
          'SUM(B.THROW_STORE_NUM) AS THROWSTORE_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(B.THROW_STORE_NUM), SUM(C.MAST_STORE_NUM)) AS THROWSTORE_RATIO, '+ 
          'SUM(B.Y_ORDER_STORE_NUM) AS Y_ORDER_STORE_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(B.Y_ORDER_STORE_NUM), SUM(C.MAST_STORE_NUM)) AS Y_ORDER_STORE_RATIO, '+ 
          'SUM(A.ORDER_AMT) AS ORDER_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.ORDER_AMT), SUM(C.STNUM_STORE_NUM)) AS ORDER_AMT_PSD, '+       
          '(DIVIDE_BY_ZERO(SUM(A.ORDER_AMT), SUM(C.STNUM_STORE_NUM)) * 7) AS ORDER_AMT_PSW, '+ 
          'SUM(A.INPRD_AMT) AS INPRD_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.INPRD_AMT), SUM(C.STNUM_STORE_NUM)) AS INPRD_AMT_PSD, '+       
          '(DIVIDE_BY_ZERO(SUM(A.INPRD_AMT), SUM(C.STNUM_STORE_NUM)) * 7) AS INPRD_AMT_PSW, '+ 
          'SUM(A.SALES_AMT) AS SALES_AMT, '+	
          'DIVIDE_BY_ZERO(SUM(A.SALES_AMT), SUM(C.STNUM_STORE_NUM)) AS SALES_AMT_PSD, '+       
          '(DIVIDE_BY_ZERO(SUM(A.SALES_AMT), SUM(C.STNUM_STORE_NUM)) * 7) AS SALES_AMT_PSW, '+ 
          'SUM(A.DIS_AMT+A.SUB_AMT) AS SUB_AMT, '+	   
          'SUM(A.REAL_SALES_AMT) AS REALSALES_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(C.STNUM_STORE_NUM)) AS REALSALES_AMT_PSD, '+        
          '(DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(C.STNUM_STORE_NUM)) * 7) AS REALSALES_AMT_PSW, '+  
          '(SUM(A.SALES_UNTAX_REAL_AMT) - SUM(A.SALES_UNTAX_COST)) AS REALSALES_GROSSPROFIT, '+          
          'DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(A.ORDER_AMT))*100 AS REALSALES_AMT_ORDERRATIO, '+	  
          'DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(A.INPRD_AMT))*100 AS REALSALES_AMT_INPRDRATIO, '+   
          'SUM(A.Y_ORDER_SALES_AMT) AS Y_ORDER_REALSALES_AMT, '+  
          'DIVIDE_BY_ZERO(SUM(A.Y_ORDER_SALES_AMT), SUM(B.Y_ORDER_STNUM)) AS Y_ORDER_REALSALES_AMT_PSD, '+        
          '(DIVIDE_BY_ZERO(SUM(A.Y_ORDER_SALES_AMT), SUM(B.Y_ORDER_STNUM)) * 7) AS Y_ORDER_REALSALES_AMT_PSW, '+  
          'SUM(A.ORDER_SALES_AMT) AS ORDERSTORE_REALSALES_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.ORDER_SALES_AMT), SUM(B.ORDER_STNUM)) AS ORDERSTORE_REALSALES_AMT_PSD, '+         
          '(DIVIDE_BY_ZERO(SUM(A.ORDER_SALES_AMT), SUM(B.ORDER_STNUM)) * 7) AS ORDERSTORE_REALSALES_AMT_PSW, '+	 
          'SUM(A.INPRD_SALES_AMT) AS INPRDSTORE_REALSALES_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.INPRD_SALES_AMT), SUM(B.INPRD_STNUM)) AS INPRDSTORE_REALSALES_AMT_PSD, '+	       
          '(DIVIDE_BY_ZERO(SUM(A.INPRD_SALES_AMT), SUM(B.INPRD_STNUM)) * 7) AS INPRDSTORE_REALSALES_AMT_PSW, '+	 
          'DIVIDE_BY_ZERO(SUM(A.SALES_AMT), SUM(B.SALES_STNUM)) AS SALESSTORE_SALES_AMT_PSD, '+ 
          '(DIVIDE_BY_ZERO(SUM(A.SALES_AMT), SUM(B.SALES_STNUM)) * 7) AS SALESSTORE_SALES_AMT_PSW, '+	
          'DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(B.SALES_STNUM)) AS SALESSTORE_REALSALES_AMT_PSD, '+	
          '(DIVIDE_BY_ZERO(SUM(A.REAL_SALES_AMT), SUM(B.SALES_STNUM)) * 7) AS SALESSTORE_REALSALES_AMT_PSW, '+	
          'SUM(A.RETPRD_AMT) AS RETURNSTORE_RETURN_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.RETPRD_AMT), SUM(B.RETPRD_STNUM)) AS RETURNSTORE_RETURN_AMT_PSD, '+ 
          '(DIVIDE_BY_ZERO(SUM(A.RETPRD_AMT), SUM(B.RETPRD_STNUM)) * 7) AS RETURNSTORE_RETURN_AMT_PSW, '+ 
          'SUM(A.TRANSPRD_AMT) AS TRANSSTORE_TRANS_AMT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.TRANSPRD_AMT),SUM(B.TRANSPRD_STNUM)) AS TRANSSTORE_TRANS_AMT_PSD, '+ 
          '(DIVIDE_BY_ZERO(SUM(A.TRANSPRD_AMT),SUM(B.TRANSPRD_STNUM)) * 7) AS TRANSSTORE_TRANS_AMT_PSW, '+ 
          'SUM(A.THROW_AMT) AS THROWSTORE_THROW_AMT, '+	
          'DIVIDE_BY_ZERO(SUM(A.THROW_AMT), SUM(B.THROW_STNUM)) AS THROWSTORE_THROW_AMT_PSD, '+ 
          '(DIVIDE_BY_ZERO(SUM(A.THROW_AMT), SUM(B.THROW_STNUM)) * 7) AS THROWSTORE_THROW_AMT_PSW, '+ 
          'SUM(A.ORDER_CNT) AS ORDER_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.ORDER_CNT), SUM(C.STNUM_STORE_NUM)) AS ORDER_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.ORDER_CNT), SUM(C.STNUM_STORE_NUM)) * 7 AS ORDER_CNT_PSW, '+ 
          'SUM(A.INPRD_CNT) AS INPRD_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.INPRD_CNT), SUM(C.STNUM_STORE_NUM)) AS INPRD_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.INPRD_CNT), SUM(C.STNUM_STORE_NUM)) * 7 AS INPRD_CNT_PSW, '+ 
          'SUM(A.SALES_CNT) AS SALES_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(C.STNUM_STORE_NUM)) AS SALES_CNT_PSD, '+      
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(C.STNUM_STORE_NUM)) * 7 AS SALES_CNT_PSW, '+  
          'SUM(A.RETPRD_CNT) AS SALES_CNT_ORDERRATIO, '+ 
          'DIVIDE_BY_ZERO(SUM(A.RETPRD_CNT), SUM(C.STNUM_STORE_NUM)) AS SALES_CNT_INPRDRATIO, '+      
          'DIVIDE_BY_ZERO(SUM(A.RETPRD_CNT), SUM(C.STNUM_STORE_NUM)) * 7 AS RETPRD_CNT, '+  
          'SUM(A.TRANSPRD_CNT) AS RETPRD_CNT_PSD, '+ 
          'DIVIDE_BY_ZERO(SUM(A.TRANSPRD_CNT), SUM(C.STNUM_STORE_NUM)) AS RETPRD_CNT_PSW, '+      
          'DIVIDE_BY_ZERO(SUM(A.TRANSPRD_CNT), SUM(C.STNUM_STORE_NUM)) * 7 AS TRANSPRD_CNT, '+  
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(A.ORDER_CNT))*100 AS TRANSPRD_CNT_PSD, '+ 
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(A.INPRD_CNT))*100 AS TRANSPRD_CNT_PSW, '+ 
          'SUM(A.Y_ORDER_SALES_CNT) AS Y_ORDERSTORE_SALES_CNT, '+  
          'DIVIDE_BY_ZERO(SUM(A.Y_ORDER_SALES_CNT), SUM(B.Y_ORDER_STNUM)) AS Y_ORDERSTORE_SALES_CNT_PSD, '+      
          'DIVIDE_BY_ZERO(SUM(A.Y_ORDER_SALES_CNT), SUM(B.Y_ORDER_STNUM)) * 7 AS Y_ORDERSTORE_SALES_CNT_PSW, '+  
          'SUM(A.ORDER_SALES_CNT) AS ORDERSTORE_SALES_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.ORDER_SALES_CNT), SUM(B.ORDER_STNUM)) AS ORDERSTORE_SALES_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.ORDER_SALES_CNT), SUM(B.ORDER_STNUM)) * 7 AS ORDERSTORE_SALES_CNT_PSW, '+ 
          'SUM(A.INPRD_SALES_CNT) AS INPRDSTORE_SALES_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.INPRD_SALES_CNT), SUM(B.INPRD_STNUM)) AS INPRDSTORE_SALES_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.INPRD_SALES_CNT), SUM(B.INPRD_STNUM)) * 7 AS INPRDSTORE_SALES_CNT_PSW, '+ 
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(B.SALES_STNUM)) AS SALESSTORE_SALES_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.SALES_CNT), SUM(B.SALES_STNUM)) * 7 AS SALESSTORE_SALES_CNT_PSW, '+ 
          'SUM(A.RETPRD_CNT) AS RETURNSTORE_RETURN_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.RETPRD_CNT), SUM(B.RETPRD_STNUM)) AS RETURNSTORE_RETURN_CNT_PSD, '+       
          'DIVIDE_BY_ZERO(SUM(A.RETPRD_CNT), SUM(B.RETPRD_STNUM)) * 7 AS RETURNSTORE_RETURN_CNT_PSW, '+   
          'SUM(A.TRANSPRD_CNT) AS TRANSSTORE_TRANS_CNT, '+ 
          'DIVIDE_BY_ZERO(SUM(A.TRANSPRD_CNT), SUM(B.TRANSPRD_STNUM)) AS TRANSSTORE_TRANS_CNT_PSD, '+     
          'DIVIDE_BY_ZERO(SUM(A.TRANSPRD_CNT), SUM(B.TRANSPRD_STNUM)) * 7 AS TRANSSTORE_TRANS_CNT_PSW, '+ 
          'SUM(A.THROW_CNT) AS THROWSTORE_THROW_CNT, '+	
          'DIVIDE_BY_ZERO(SUM(A.THROW_CNT), SUM(B.THROW_STNUM)) AS THROWSTORE_THROW_CNT_PSD, '+	 
          'DIVIDE_BY_ZERO(SUM(A.THROW_CNT), SUM(B.THROW_STNUM)) * 7 AS THROWSTORE_THROW_CNT_PSW '; 
     SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_1_9_1_FUNC  AS('+
          'SELECT '+
          'A.PRD_ID, '+  
          'A.MMA_ID, '+  
          V_SQL +
          ' FROM #VT_POSN_MFACT_FUNC A '+
          ' ,#VT_POSN_STFACT_FUNC B '+
          ' ,#VT_POSN_SFACT_FUNC C  '+
          ' WHERE A.TIME_ID=B.TIME_ID AND A.ORG_ID=B.ORG_ID AND A.PRD_ID=B.PRD_ID AND A.MMA_ID=B.MMA_ID  '+
          ' AND A.TIME_ID=C.TIME_ID AND A.ORG_ID=C.ORG_ID AND A.MMA_ID=C.MMA_ID GROUP BY A.PRD_ID,A.MMA_ID'+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
  EXECUTE IMMEDIATE SQLSTR;
END SP;