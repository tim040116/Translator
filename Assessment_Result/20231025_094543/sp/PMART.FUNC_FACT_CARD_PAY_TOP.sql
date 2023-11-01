REPLACE PROCEDURE PMART.FUNC_FACT_CARD_PAY_TOP
(
   IN P_TIMEID INTEGER,
   IN P_ORGID INTEGER,
   IN P_CARDTYPE VARCHAR(50),
   IN P_ORDER_COL VARCHAR(20)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(2000);    
   DECLARE V_CARDTYPE VARCHAR(2);
   DECLARE V_SPLIT_POS SMALLINT;
   DECLARE V_STR_LEN SMALLINT;
   SET V_SPLIT_POS = 1;
CALL PMART.P_DROP_TABLE ('#VT_FACT_CARD_PAY_TOP');
SET SQLSTR =  ' CREATE MULTISET VOLATILE TABLE #VT_FACT_CARD_PAY_TOP('
                        +' DAT_SEQ SMALLINT, '
						+' ORG_ID INTEGER, '
                        +' TIME_ID INTEGER, '
                        +' CARD_TYPE SMALLINT, '
                        +' PRD_ID VARCHAR(7), '
                        +' PRD_NAME VARCHAR(64), '
                        +' TRANS_CNT INTEGER, '
                        +' PAY_CNT DECIMAL(12,0), '
                        +' PAY_AMT DECIMAL(12,0), '
                        +' CARD_CNT INTEGER, '
						+' CARD_TYPE_NAME VARCHAR(100) '
						+')UNIQUE PRIMARY INDEX(ORG_ID ,TIME_ID ,CARD_TYPE ,PRD_ID) ON COMMIT PRESERVE ROWS;';
		EXECUTE IMMEDIATE SQLSTR;   
    SET V_CARDTYPE =  NVL(STRING_SPLIT (TRIM(P_CARDTYPE),',',V_SPLIT_POS),'N');
WHILE TRIM(V_CARDTYPE) <>'N'
DO    
            SET SQLSTR =  ' INSERT INTO #VT_FACT_CARD_PAY_TOP '						  
                                   +'  SELECT DAT.*, CARD.CODE_NAME CARD_TYPE_NAME FROM (  '
                                   +'                                    SELECT RANK() OVER(ORDER BY '+P_ORDER_COL+' DESC)  DAT_SEQ '
		   				           +'                                    ,ORG_ID, TIME_ID, CARD_TYPE, PRD_ID , PRD_NAME '
                                   +'                                    ,TRANS_CNT,PAY_CNT, PAY_AMT, CARD_CNT '
                                   +'                                        FROM PMART.FACT_CARD_PAY_TOP PAY100 '
						           +'                                    WHERE 1 = 1'
   						           +'                                            AND ORG_ID = '+P_ORGID
                                   +'                                            AND TIME_ID = '+P_TIMEID
                        	       +'                                            AND CARD_TYPE IN ('+V_CARDTYPE+') '
                                   +'                                    ) DAT LEFT JOIN  (SELECT * FROM PDATA.PBMCODE WHERE CODE_TYPE=''CARD_TYPE'') CARD ON DAT.CARD_TYPE = CARD.CODE_ID '
						           +'   WHERE DAT.DAT_SEQ <= ( '
								   +'                                                        SELECT ORDER_SEQ '
								   +'                                                            FROM ( '
								   +'                                                                            SELECT ROW_NUMBER() OVER(ORDER BY '+P_ORDER_COL+' DESC)  RANK_SEQ '
								   +'                                                                                             ,RANK() OVER(ORDER BY '+P_ORDER_COL+' DESC)  ORDER_SEQ '
     		      				   +'                                                                                FROM PMART.FACT_CARD_PAY_TOP PAY100 '
								   +'                                                                            WHERE 1 = 1 '
   						           +'                                                                                   AND ORG_ID = '+P_ORGID
                                   +'                                                                                   AND TIME_ID = '+P_TIMEID
                        	       +'                                                                                   AND CARD_TYPE IN ('+V_CARDTYPE+') '
								   +'                                                                         )  DAT_R WHERE RANK_SEQ = 100'
								   +'                                                      );';
	        EXECUTE IMMEDIATE SQLSTR;  
		 DELETE PMART.T1 WHERE F1 = 2;
	     INSERT INTO PMART.T1 VALUES(2,SQLSTR);
		    SET V_SPLIT_POS = V_SPLIT_POS + 1;
		    SET V_CARDTYPE =  NVL(STRING_SPLIT (TRIM(P_CARDTYPE),',',V_SPLIT_POS),'N');
END WHILE;		
END SP;