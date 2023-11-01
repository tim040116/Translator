REPLACE PROCEDURE PMART.BASIC_TIME_USELF
(FP_TIME_TYPE CHAR(1)CASESPECIFIC,FP_TIME_IDS NUMBER,FP_TIME_IDE NUMBER,FP_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLCREATETB  VARCHAR(8000); 
DECLARE SQLSTR  VARCHAR(8000); 
  CALL PMART.P_DROP_TABLE ('#VT_BASIC_TIME_USELF'); 
  SET SQLCREATETB = 
  'CREATE MULTISET VOLATILE TABLE #VT_BASIC_TIME_USELF 
  (
  TIME_ID        INTEGER,
  TIME_NM        VARCHAR(24) ,
  LTIME_ID       INTEGER,
  LTIME_NM       VARCHAR(24) ,
  MAST_STORE_NUM INTEGER,
  WEATHER        INTEGER,
  LWEATHER       INTEGER,
  WEATH_NAME     VARCHAR(24)  ,
  LWEATH_NAME    VARCHAR(24) 
  )
   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
EXECUTE IMMEDIATE SQLCREATETB;
  CASE FP_TIME_TYPE
      WHEN 'I' THEN
           SET SQLSTR = 
                'INSERT INTO #VT_BASIC_TIME_USELF(TIME_ID,TIME_NM,LTIME_ID,LTIME_NM,MAST_STORE_NUM,WEATHER,LWEATHER,WEATH_NAME,LWEATH_NAME) '+
                       'SELECT '+
                       '   A.L_DAY_ID AS TIME_ID, '+
                       '   A.L_DAY_NAME AS TIME_NM, '+
                       '   B.L_DAY_ID AS LTIME_ID, '+
                       '   B.L_DAY_NAME AS LTIME_NM, '+
                       '   BIT_EXTRACT(BIT_AND(C.MAST_STORE_NUM,D.MASK)) AS MAST_STORE_NUM, '+
                       '   PMART.BASIC_TIME_GET_MAXWEATHER(E.WEATHER1,E.WEATHER2,E.WEATHER3,E.WEATHER4,E.WEATHER5,E.WEATHER6,E.WEATHER7,E.WEATHER8,E.WEATHER9) AS WEATHER, '+
                       '   PMART.BASIC_TIME_GET_MAXWEATHER(F.WEATHER1,F.WEATHER2,F.WEATHER3,F.WEATHER4,F.WEATHER5,F.WEATHER6,F.WEATHER7,F.WEATHER8,F.WEATHER9) AS LWEATHER, '+
                       '   PMART.BASIC_TIME_GET_WEATHER_NM(E.WEATHER1,E.WEATHER2,E.WEATHER3,E.WEATHER4,E.WEATHER5,E.WEATHER6,E.WEATHER7,E.WEATHER8,E.WEATHER9) AS WEATH_NAME, '+
                       '   PMART.BASIC_TIME_GET_WEATHER_NM(F.WEATHER1,F.WEATHER2,F.WEATHER3,F.WEATHER4,F.WEATHER5,F.WEATHER6,F.WEATHER7,F.WEATHER8,F.WEATHER9) AS LWEATH_NAME '+
                       '   FROM PMART.YMWD_TIME A LEFT JOIN PMART.YMWD_TIME B '+
                       '   ON (A.L_DAY_LAST_YEAR=B.L_DAY_ID) LEFT JOIN PMART.BASIC_MAST_FACT C '+
                       '   ON (A.L_DAY_ID=C.TIME_ID) LEFT JOIN PMART.BASIC_SFACT E '+
                       '   ON (A.L_DAY_ID=E.TIME_ID AND E.ORG_ID='+FP_ORG_ID+') LEFT JOIN PMART.BASIC_SFACT F '+
                       '   ON (A.L_DAY_LAST_YEAR=F.TIME_ID AND F.ORG_ID='+FP_ORG_ID+'), '+
                       '   (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+FP_ORG_ID+') D '+
                       '   WHERE A.L_DAY_ID >='+FP_TIME_IDS+' AND A.L_DAY_ID<='+FP_TIME_IDE+';';
              EXECUTE IMMEDIATE SQLSTR;
   END CASE;  
END SP;