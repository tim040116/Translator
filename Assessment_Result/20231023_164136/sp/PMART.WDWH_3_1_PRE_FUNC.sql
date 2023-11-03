CREATE PROCEDURE PMART.WDWH_3_1_PRE_FUNC
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
END SP;