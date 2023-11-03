REPLACE  PROCEDURE PDATA.PRDREL_PROMO_NEW()
SP:BEGIN
	DECLARE V_JOB_ID INTEGER;
	DECLARE V_PROMO SMALLINT;
	DECLARE V_SET_PROMO INTEGER;
	SET V_JOB_ID = (SELECT JOB_ID FROM PTEMP.PRDREL_SET);
	SET V_PROMO = (SELECT PROMO FROM PTEMP.PRDREL_SET);
	SET V_SET_PROMO = (SELECT COUNT(*) FROM PDATA.PRDREL_POMO_SET WHERE JOB_ID=V_JOB_ID);
	IF V_SET_PROMO <> 0 THEN
		DELETE PTEMP.PS_TRANS_MM_PROMO
		WHERE TX_SEQ  NOT IN (
			SELECT T.TX_SEQ 
			FROM PTEMP.PS_TRANS_MM_PROMO T
				INNER JOIN (
					SELECT BDAY_ID,EDAY_ID,POMOID 
					FROM PDATA.PROMO 
					WHERE (POMOYM,POMOID) IN (
						SELECT MONTH_ID, MM_ID FROM PDATA.PRDREL_POMO_SET WHERE JOB_ID=V_JOB_ID)
				) M ON TO_CHAR(T.TX_DTTM,'YYYYMMDD') BETWEEN BDAY_ID AND EDAY_ID AND T.MM_ID = M.POMOID
		);
	END IF;
	IF V_PROMO = 2 THEN 
		IF V_SET_PROMO = 0 THEN
			DELETE PTEMP.PS_TRANS
			WHERE TX_SEQ IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);
			DELETE PTEMP.PS_TRANS_PRODUCT_DETAIL
			WHERE TX_SEQ IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);
		ELSE
			DELETE PTEMP.PS_TRANS
			WHERE TX_SEQ IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);
			DELETE PTEMP.PS_TRANS_PRODUCT_DETAIL
			WHERE TX_SEQ IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);		
		END IF;
	ELSEIF V_PROMO = 3 THEN 
		DELETE PTEMP.PS_TRANS
		WHERE TX_SEQ NOT IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);
		DELETE PTEMP.PS_TRANS_PRODUCT_DETAIL
		WHERE TX_SEQ NOT IN (SELECT TX_SEQ FROM PTEMP.PS_TRANS_MM_PROMO);
	END IF;
END;