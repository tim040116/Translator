REPLACE PROCEDURE PMART.WDWH_3_1_NEW_FUNC
(
   IN I_ORG_TYPE  VARCHAR(2),   
   IN I_ORG_ID    INTEGER,      
   IN I_DAY_ID    INTEGER       
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR     VARCHAR(4000) DEFAULT '';  
  DECLARE V_UP_ORG_ID    INTEGER;
  SET V_UP_ORG_ID  = '' ;
  IF (I_ORG_TYPE = '1') THEN
     SELECT DISTINCT PDEPT_ID 
       INTO V_UP_ORG_ID
       FROM PMART.LAST_ORG_DIM
      WHERE DEPT_ID =  I_ORG_ID
   ;
  END IF ;
  IF (I_ORG_TYPE = '2') THEN
     SELECT DISTINCT DEPT_ID 
       INTO V_UP_ORG_ID
       FROM PMART.LAST_ORG_DIM
      WHERE BRANCH_ID =  I_ORG_ID
   ;
  END IF ;
  IF (I_ORG_TYPE = '3') THEN
     SELECT DISTINCT BRANCH_ID 
       INTO V_UP_ORG_ID
       FROM PMART.LAST_ORG_DIM
      WHERE RESPON_ID =  I_ORG_ID
   ;
  END IF ;  
  CALL PMART.REMD_SCAL_TOT_FUNC(I_DAY_ID);
  IF  (I_ORG_TYPE = '-1' OR I_ORG_TYPE = '0' ) THEN      
    CALL PMART.REMD_SCAL_PDEPT_FUNC(I_DAY_ID);	  
  END IF;
  IF  (I_ORG_TYPE = '0') THEN      
	  CALL PMART.REMD_SCAL_SDEPT_FUNC(I_DAY_ID, I_ORG_ID);  
  END IF;
  IF (I_ORG_TYPE = '1') THEN
     CALL PMART.REMD_SCAL_SDEPT_FUNC(I_DAY_ID, V_UP_ORG_ID);
	 CALL PMART.REMD_SCAL_BRANCH_FUNC(I_DAY_ID,I_ORG_ID);     
  END IF ;
  IF (I_ORG_TYPE = '2') THEN
      CALL PMART.REMD_SCAL_BRANCH_FUNC(I_DAY_ID,V_UP_ORG_ID);
      CALL PMART.REMD_SCAL_RESPON_FUNC(I_DAY_ID,I_ORG_ID);
  END IF ;
  IF (I_ORG_TYPE = '3') THEN          
	 CALL PMART.REMD_SCAL_RESPON_FUNC(I_DAY_ID,V_UP_ORG_ID);
     CALL PMART.REMD_SCAL_OSTORE_FUNC(I_DAY_ID,I_ORG_ID);
  END IF ;       
  CALL PMART.P_DROP_TABLE ('#VT_WDWH_3_1_NEW_FUNC');
  IF (I_ORG_TYPE = '-1') THEN
      CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_NEW_FUNC AS
      (SELECT * FROM 
         (SELECT A.*, B.*                                                        
            FROM (                                                                 
                  SELECT DISTINCT 0 AS DISP_LEVEL
                        ,TOT_ID AS ORG_ID                
                        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO
                        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM
                        ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC                                            
                    FROM PMART.LAST_ORG_DIM                                        
            ) A                                                                    
              LEFT JOIN (                                                          
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21
                    FROM #VT_REMD_SCAL_TOT_FUNC                                    
              ) B ON A.ORG_ID = B.R_ORG_ID                                         
            UNION ALL                                                              
            SELECT A.*, B.*                                                        
            FROM (                                                                 
                  SELECT DISTINCT 1 AS DISP_LEVEL
                        ,TOT_ID AS ORG_ID                
                        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO                     
                        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM                     
                        ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                    FROM PMART.LAST_ORG_DIM
            ) A                                                                    
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                                     
                    FROM #VT_REMD_SCAL_TOT_FUNC                                    
              ) B ON A.ORG_ID = B.R_ORG_ID 
            UNION ALL                                                              
            SELECT A.*, B.*                                                        
            FROM (                                                                 
                  SELECT DISTINCT 2 AS DISP_LEVEL
                        ,PDEPT_ID AS ORG_ID
                        ,CAST(PDEPT_NO AS VARCHAR(10)) AS ORG_NO
                        ,CAST(PDEPT_NM AS VARCHAR(50)) AS ORG_NM
                        ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC                                            
                    FROM PMART.LAST_ORG_DIM A                                      
                   WHERE TOT_ID = I_ORG_ID                              
             ) A                                                                   
             LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                                                                      
                    FROM #VT_REMD_SCAL_PDEPT_FUNC                                   
             ) B ON A.ORG_ID = B.R_ORG_ID 
         )   AA      
      ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; 
  END IF ;
  IF (I_ORG_TYPE = '0' ) THEN        
      CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_NEW_FUNC AS
      (SELECT * FROM 
         (SELECT A.*, B.*
            FROM (
                  SELECT DISTINCT 0 AS DISP_LEVEL
                        ,TOT_ID AS ORG_ID
                        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO
                        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM
                        ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                    FROM PMART.LAST_ORG_DIM
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_TOT_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID
			  UNION ALL
            SELECT A.*, B.*
              FROM (
                    SELECT DISTINCT 1 AS DISP_LEVEL
                          ,PDEPT_ID AS ORG_ID
                          ,CAST(PDEPT_NO AS VARCHAR(10)) AS ORG_NO
                          ,CAST(PDEPT_NM AS VARCHAR(50)) AS ORG_NM
                          ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                      FROM PMART.LAST_ORG_DIM 
                     WHERE PDEPT_ID = I_ORG_ID     
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_PDEPT_FUNC
                    WHERE ORG_ID = I_ORG_ID
              ) B ON A.ORG_ID = B.R_ORG_ID
            UNION ALL
            SELECT A.*, B.*
              FROM (
                    SELECT DISTINCT 2 AS DISP_LEVEL
                          ,DEPT_ID AS ORG_ID
                          ,CAST(DEPT_NO AS VARCHAR(10)) AS ORG_NO
                          ,CAST(DEPT_NM AS VARCHAR(50)) AS ORG_NM
                          ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                      FROM PMART.LAST_ORG_DIM 
                     WHERE PDEPT_ID = I_ORG_ID   
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_SDEPT_FUNC                    
              ) B ON A.ORG_ID = B.R_ORG_ID
          )   AA 
      ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;   
  END IF ;
  IF (I_ORG_TYPE = '1' ) THEN        
      CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_NEW_FUNC AS
      (SELECT * FROM 
         (SELECT A.*, B.*
            FROM (
                  SELECT DISTINCT 0 AS DISP_LEVEL
                        ,TOT_ID AS ORG_ID
                        ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO
                        ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM
                        ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                    FROM PMART.LAST_ORG_DIM
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_TOT_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID		
		    UNION ALL
            SELECT A.*, B.*
              FROM (
                    SELECT DISTINCT 1 AS DISP_LEVEL
                          ,DEPT_ID AS ORG_ID
                          ,CAST(DEPT_NO AS VARCHAR(10)) AS ORG_NO
                          ,CAST(DEPT_NM AS VARCHAR(50)) AS ORG_NM
                          ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                      FROM PMART.LAST_ORG_DIM 
                     WHERE DEPT_ID = I_ORG_ID
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_SDEPT_FUNC
                    WHERE ORG_ID = I_ORG_ID
              ) B ON A.ORG_ID = B.R_ORG_ID
            UNION ALL
            SELECT A.*, B.*
              FROM (
                    SELECT DISTINCT 2 AS DISP_LEVEL
                          ,BRANCH_ID AS ORG_ID
                          ,BRANCH_NO AS ORG_NO
                          ,BRANCH_NM AS ORG_NM
                          ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                      FROM PMART.LAST_ORG_DIM 
                     WHERE DEPT_ID = I_ORG_ID
            ) A
              LEFT JOIN (
                  SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                        ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                        ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                        ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                        ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                        ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                        ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                        ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                        ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                        ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                        ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                        ,'無' AS R21                          
                     FROM #VT_REMD_SCAL_BRANCH_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID
          )   AA 
      ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;   
  END IF ;
  IF (I_ORG_TYPE = '2' ) THEN
      CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_NEW_FUNC AS
      (SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 0 AS DISP_LEVEL
                     ,TOT_ID AS ORG_ID
                     ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM
                     ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                 FROM PMART.LAST_ORG_DIM
              ) A LEFT JOIN 
              (
               SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                     ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                     ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                     ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                     ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                     ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                     ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                     ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                     ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                     ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                     ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                     ,'無' AS R21                      
                 FROM #VT_REMD_SCAL_TOT_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID
       UNION ALL
       SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 1 AS DISP_LEVEL
                     ,BRANCH_ID AS ORG_ID
                     ,CAST(BRANCH_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(BRANCH_NM AS VARCHAR(50)) AS ORG_NM
                     ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                 FROM PMART.LAST_ORG_DIM
                WHERE BRANCH_ID = I_ORG_ID 
              ) A LEFT JOIN 
              (
               SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                     ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                     ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                     ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                     ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                     ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                     ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                     ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                     ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                     ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                     ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                     ,'無' AS R21                      
                 FROM #VT_REMD_SCAL_BRANCH_FUNC
                WHERE ORG_ID = I_ORG_ID 
              ) B ON A.ORG_ID = B.R_ORG_ID
       UNION ALL
       SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 2 AS DISP_LEVEL
                     ,RESPON_ID AS ORG_ID
                     ,CAST(RESPON_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(RESPON_NM AS VARCHAR(50)) AS ORG_NM
                     ,CAST(NULL AS VARCHAR(20))AS PS_TYPE_SDESC
                FROM PMART.LAST_ORG_DIM 
               WHERE BRANCH_ID = I_ORG_ID 
              ) A LEFT JOIN 
              (
            SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                  ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                  ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                  ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                  ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                  ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                  ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                  ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                  ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                  ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                  ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                  ,'無' AS R21                   
              FROM #VT_REMD_SCAL_RESPON_FUNC
       ) B ON A.ORG_ID = B.R_ORG_ID
      ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
  END IF ;
  IF (I_ORG_TYPE = '3' ) THEN
      CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_NEW_FUNC AS
      (SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 0 AS DISP_LEVEL
                     ,TOT_ID AS ORG_ID
                     ,CAST(TOT_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(TOT_NM AS VARCHAR(50)) AS ORG_NM
                     ,CAST(NULL AS VARCHAR(20) Collate Chinese_Taiwan_Stroke_CI_AS)AS PS_TYPE_SDESC
                 FROM PMART.LAST_ORG_DIM
              ) A LEFT JOIN 
              (
               SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                     ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                     ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                     ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                     ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                     ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                     ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                     ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                     ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                     ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                     ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                     ,'無' AS R21                      
                 FROM #VT_REMD_SCAL_TOT_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID
       UNION ALL
       SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 1 AS DISP_LEVEL
                     ,RESPON_ID AS ORG_ID
                     ,CAST(RESPON_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(RESPON_NM AS VARCHAR(50)) AS ORG_NM
                     ,CAST(NULL AS VARCHAR(20) Collate Chinese_Taiwan_Stroke_CI_AS)AS PS_TYPE_SDESC
                 FROM PMART.LAST_ORG_DIM 
                WHERE RESPON_ID = I_ORG_ID
              ) A LEFT JOIN 
              (
               SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                     ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                     ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                     ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                     ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                     ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                     ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                     ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                     ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                     ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                     ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                     ,'無' AS R21                      
                FROM #VT_REMD_SCAL_RESPON_FUNC
               WHERE ORG_ID = I_ORG_ID
              ) B ON A.ORG_ID = B.R_ORG_ID
       UNION ALL
       SELECT A.*, B.*
         FROM (
               SELECT DISTINCT 2 AS DISP_LEVEL
                     ,T1.OSTORE_ID AS ORG_ID
                     ,CAST(T1.STORE_NO AS VARCHAR(10)) AS ORG_NO
                     ,CAST(T1.STORE_NM AS VARCHAR(50)) AS ORG_NM                     
                     ,CAST(T2.PS_TYPE_SDESC AS VARCHAR(20))AS PS_TYPE_SDESC
                 FROM PMART.LATEST_ORG_DIM T1 LEFT JOIN PMART.PLAN_STNUM_VIEW T2 
                   ON (T1.STORE_ID=T2.STORE_ID AND T2.L_DAY_ID= I_DAY_ID )
                WHERE T1.RESPON_ID = I_ORG_ID
                  AND ROUND(T1.OPNDT/100)<=ROUND( I_DAY_ID /100)
                  AND ROUND(T1.ENDDT/100)>=ROUND( I_DAY_ID /100)
                  AND ((T1.ASTORE_ID IS NULL AND T1.OPNDT<= I_DAY_ID ) OR
                       (T1.ASTORE_ID IS NOT NULL AND T1.OPNDT<= I_DAY_ID AND I_DAY_ID <T1.ENDDT))
              ) A LEFT JOIN 
              (
               SELECT CAST(ORG_ID AS INTEGER) AS  R_ORG_ID
                     ,TO_CHAR(ROUND(R2,0), 'FM999,999,990') AS R2
                     ,TO_CHAR(ROUND(R3,0), 'FM999,999,990') AS R3
                     ,CAST(ROUND(R4,2) AS DECIMAL(12,2)) AS R4
                     ,TO_CHAR(ROUND(R5,0), 'FM999,999,990') AS R5
                     ,CAST(ROUND(R6,2) AS DECIMAL(12,2))+'%' AS R6
                     ,CAST(ROUND(R7,2) AS DECIMAL(12,2))+'%' AS R7
                     ,TO_CHAR(ROUND(R9,0), 'FM999,999,990') AS R9
                     ,CAST(ROUND(R10,2) AS DECIMAL(12,2))+'%' AS R10
                     ,CAST(ROUND(R11,2) AS DECIMAL(12,2)) AS R11
                     ,CAST(ROUND(R12,2) AS DECIMAL(12,2)) AS R12
                     ,'無' AS R21                      
                FROM #VT_REMD_SCAL_OSTORE_FUNC
              ) B ON A.ORG_ID = B.R_ORG_ID
      ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;  
  END IF ;
END SP;