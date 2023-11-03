REPLACE PROCEDURE PMART.FUNC_FACT_CARD_ADD
(
   IN P_GROUP VARCHAR(500),
   IN P_TIMEID VARCHAR(2000),
   IN P_ORGID VARCHAR(2000),
   IN P_CARDTYPE VARCHAR(500),
   IN P_FGADDTYPE VARCHAR(10),
   IN P_FGADDRANGE VARCHAR(100)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(2000);    
CALL PMART.P_DROP_TABLE ('#VT_FACT_CARD_ADD');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_CARD_ADD AS('
                          +' SELECT '
                          + CASE WHEN P_GROUP LIKE '%RESPON_ID%' THEN Replace(P_GROUP,'RESPON_ID','RESPON_ID AS ORG_ID') ELSE P_GROUP END +',SUM(ADD_AMT) ADD_AMT,SUM(ADD_CNT) ADD_CNT'
                          + ' FROM PMART.FACT_CARD_ADD'
        + CASE WHEN P_GROUP LIKE '%RESPON_ID%'  THEN  ' JOIN PMART.ORG_STORE ON ORG_ID=OSTORE_ID ' ELSE '' END
                          + ' WHERE ' 
                          + CASE WHEN TRIM(P_ORGID)='' AND POSITION('ORG_ID' IN P_GROUP)=0 THEN 'ORG_ID=-1 ' WHEN TRIM(P_ORGID)='' THEN '1=1'  ELSE 'ORG_ID IN('+P_ORGID+') ' END
                          + ' AND TIME_ID IN ('+P_TIMEID+')' 
                          + CASE WHEN TRIM(P_CARDTYPE)='' AND POSITION('CARD_TYPE' IN P_GROUP)=0 THEN ' AND CARD_TYPE=-1 ' WHEN TRIM(P_CARDTYPE)='' THEN ''  ELSE ' AND CARD_TYPE IN('+P_CARDTYPE+') ' END
                          + CASE WHEN TRIM(P_FGADDTYPE)='' THEN ''  ELSE ' AND FG_ADD_TYPE IN('+P_FGADDTYPE+') ' END
                          + CASE WHEN TRIM(P_FGADDRANGE)='' THEN ''  ELSE ' AND FG_ADD_RANGE IN('+P_FGADDRANGE+') ' END
                          + ' GROUP BY '+  P_GROUP                     
                          + ' ) WITH DATA PRIMARY CHARINDEX('RESPON_ID','+CASE WHEN P_GROUP LIKE '%RESPON_ID%' THEN Replace(P_GROUP,'ORG_ID') ELSE P_GROUP END+') ON COMMIT PRESERVE ROWS;';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;