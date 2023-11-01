REPLACE PROCEDURE PMART.PRD_ABC_LEVEL_PRO_STOCK_HIS(P_YEAR_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
DELETE FROM PTEMP.TP_PRD_SAL_STOCK;
     INSERT INTO PTEMP.TP_PRD_SAL_STOCK(YYYYMM,PRD_ID,GRP_NO,PRD_SLUNT,SALES_CNT,NET_PROFIT)
           SELECT S.TIME_ID, S.PRD_ID, C.GRP_NO,NVL(C.PRD_SLUNT,0) ,SUM(NVL(S.SALCNT,0))
                       ,SUM(NVL(CASE WHEN TO_NUMBER(DECODE(C.REAL_CODE ,NULL,'0' , C.REAL_CODE)) <> 10 THEN (C.PRD_SLUNT-C.PRD_CSUNT*1.05)*S.SALCNT       
                                                     WHEN TO_NUMBER(DECODE(C.REAL_CODE ,NULL,'0' , C.REAL_CODE)) =  10 THEN S.SALAMT - (S.SALAMT * (C.PRD_CSUNT/C.PRD_SLUNT)) 
                                           END,0))/SUM(STCNT) AS NET_PROFIT             
          FROM PTEMP.TP_PRD_STATUS_FOR_STOCK  C, (SELECT * FROM PTEMP.TP_BASIC_MFACT_STOCK_SAL WHERE TIME_ID=P_YEAR_ID)S
      WHERE C.PRD_ID = S.PRD_ID
GROUP BY  S.TIME_ID, S.PRD_ID, C.GRP_NO,NVL(C.PRD_SLUNT,0);
DELETE FROM PTEMP.TP_PRD_SAL_STOCK_RANK ;  
INSERT INTO PTEMP.TP_PRD_SAL_STOCK_RANK (YYYYMM,PRD_ID, PRD_NO, PRD_NM, PRD_CMBND, PRD_SPC, GRP_NO, PRD_SLUNT, SALES_CNT, NET_PROFIT, ACC_NET_PROFIT,ROW_NUM)
SELECT A.YYYYMM,A.PRD_ID,A.PRD_NO,A.PRD_NM,A.PRD_CMBND,A.PRD_SPC,A.GRP_NO,A.PRD_SLUNT,A.SALES_CNT,A.NET_PROFIT
                 ,SUM(NET_PROFIT) OVER(PARTITION BY GRP_NO ORDER BY ROW_NUM ROWS UNBOUNDED PRECEDING) ACC_NET_PROFIT, ROW_NUM
    FROM (SELECT S.YYYYMM,S.PRD_ID,P.PRD_NO,P.PRD_NM,P.PRD_CMBND,P.PRD_SPC,S.GRP_NO,S.PRD_SLUNT,S.SALES_CNT,S.NET_PROFIT
                                   ,ROW_NUMBER() OVER(PARTITION BY S.GRP_NO ORDER BY S.NET_PROFIT DESC) ROW_NUM
                      FROM PTEMP.TP_PRD_SAL_STOCK S, PMART.PRD_DIM P
                  WHERE S.PRD_ID = P.PRD_ID
                 ) A;
DELETE FROM PTEMP.TP_PRD_ABC_LEVL_STOCK;
INSERT INTO PTEMP.TP_PRD_ABC_LEVL_STOCK(YYYYMM,PRD_ID,PRD_NO,PRD_NM,PRD_CMBND,PRD_SPC,GRP_NO,PRD_SLUNT,SALES_CNT,NET_PROFIT,PRD_LEVL) 
      SELECT T2.YYYYMM, T2.PRD_ID, T2.PRD_NO, T2.PRD_NM, T2.PRD_CMBND, T2.PRD_SPC,T2.GRP_NO, T2.PRD_SLUNT, T2.SALES_CNT, T2.NET_PROFIT,   
                       CASE WHEN (T2.ACC_NET_PROFIT / T1.TOT_PROFIT <= 0.7 OR T2.ROW_NUM = 1) THEN 'A'
                                   WHEN ( (T2.ACC_NET_PROFIT/T1.TOT_PROFIT)>0.7 AND ((T2.ACC_NET_PROFIT/T1.TOT_PROFIT)-(T2.NET_PROFIT/T1.TOT_PROFIT))<0.7 ) THEN 'A'
                                   WHEN (T2.ACC_NET_PROFIT / T1.TOT_PROFIT > 0.7 AND T2.ACC_NET_PROFIT / T1.TOT_PROFIT <= 0.9 AND T2.ROW_NUM <> 1) THEN 'B'
                                   WHEN ( (T2.ACC_NET_PROFIT /T1.TOT_PROFIT )>0.9 AND ((T2.ACC_NET_PROFIT/T1.TOT_PROFIT )-(T2.NET_PROFIT/T1.TOT_PROFIT))<0.9 )  THEN 'B'
                                   WHEN (T2.ACC_NET_PROFIT / T1.TOT_PROFIT > 0.9 AND T2.ACC_NET_PROFIT / T1.TOT_PROFIT <= 1 AND T2.ROW_NUM <> 1) THEN 'C'
                      ELSE 'C' END AS PRD_LEVEL
          FROM (  
                        SELECT A.* FROM (
                                                                 SELECT GRP_NO,SUM(NET_PROFIT)TOT_PROFIT
                                                                     FROM PTEMP.TP_PRD_SAL_STOCK 
                                                            GROUP BY GRP_NO
                                                           )  A WHERE A.TOT_PROFIT > 0 
                       ) T1
                         , PTEMP.TP_PRD_SAL_STOCK_RANK  T2
       WHERE T1.GRP_NO = T2.GRP_NO;
UPDATE   ORI_STOCK
     FROM PTEMP.TP_PRD_ABC_LEVL_STOCK  ORI_STOCK,
                  (
                     SELECT GRP_NO,NET_PROFIT,CASE WHEN PRD_LEVL_RANK=3 THEN 'A'
                                                                                         WHEN PRD_LEVL_RANK=2 THEN 'B'
                                                                                         WHEN PRD_LEVL_RANK=1 THEN 'C'
                                                                               END NEW_PRD_LEVL 
                         FROM (                    
                                       SELECT A.GRP_NO,A.NET_PROFIT
                                                        ,MAX(CASE WHEN A.PRD_LEVL ='A' THEN 3 
                                                                              WHEN A.PRD_LEVL ='B' THEN 2
                                                                              WHEN A.PRD_LEVL ='C' THEN 1
                                                                  END) PRD_LEVL_RANK    
                                           FROM PTEMP.TP_PRD_ABC_LEVL_STOCK  AS A,
                                                        (
                                                                SELECT GRP_NO,ROUND(NET_PROFIT,2) NET_PROFIT_1
                                                                    FROM PTEMP.TP_PRD_SAL_STOCK_RANK T
                                                                WHERE NET_PROFIT > 0 
                                                           GROUP BY GRP_NO,ROUND(NET_PROFIT,2)
                                                                 HAVING COUNT(1)>1
                                                        )B
                                       WHERE A.GRP_NO=B.GRP_NO
                                              AND ROUND(A.NET_PROFIT,2)  =B.NET_PROFIT_1
                                  GROUP BY A.GRP_NO,A.NET_PROFIT
                                      )   Q
                   )NEW_ABC_RANK
         SET PRD_LEVL=NEW_ABC_RANK.NEW_PRD_LEVL
  WHERE ORI_STOCK.GRP_NO=NEW_ABC_RANK.GRP_NO
         AND ORI_STOCK.NET_PROFIT=NEW_ABC_RANK.NET_PROFIT;
END SP;