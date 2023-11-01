CREATE FUNCTION PMART.SUBSIDY_THROW_CNT1_FUNC(L_ORDER_MQTY INTEGER,L_INPRD_CNT INTEGER,L_SALES_CNT INTEGER,L_THROW_CNT INTEGER,L_ITEM_KIND VARCHAR(2))
   RETURNS INTEGER
   LANGUAGE SQL
   CONTAINS SQL
   DETERMINISTIC
   SQL SECURITY DEFINER
   COLLATION INVOKER
   INLINE TYPE 1
   RETURN
CASE 
		WHEN PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND)<0   
		  THEN 0
		WHEN PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND)>=0 AND 
		             PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND) < L_THROW_CNT 
		  THEN PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND)
		WHEN PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND)>=0 AND 
		             PDATA.SUBSIDY_INPRD_SALES_QTY_FUNC(L_ORDER_MQTY, L_INPRD_CNT, L_SALES_CNT, L_ITEM_KIND)>= L_THROW_CNT 
		   THEN L_THROW_CNT
		   ELSE L_THROW_CNT END;