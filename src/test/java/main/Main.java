package main;

import java.text.SimpleDateFormat;
import java.util.Date;

import etec.common.utils.convert_safely.ConvertRemarkSafely;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\familymart\\T1\\SQLAExport.txt";
	
	public static void main(String[] args) {
		try {
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			
			String a = "remd_add_tksl<saveTranslateFunctionMark_leftquater_3>'+@P_day_id+'<saveTranslateFunctionMark_comma_3>'m'<saveTranslateFunctionMark_comma_3>cast<saveTranslateFunctionMark_leftquater_4>t1.tot_amt_last_month as number<saveTranslateFunctionMark_rightquater_4><saveTranslateFunctionMark_comma_3>cast<saveTranslateFunctionMark_leftquater_4>tot_tksl_last_month as number<saveTranslateFunctionMark_rightquater_4><saveTranslateFunctionMark_rightquater_3>/ 					 <ConvertRemarkSafely_dash_6>							decode<saveTranslateFunctionMark_leftquater_3>t1.tot_plan_stnum_last_month<saveTranslateFunctionMark_comma_3>0<saveTranslateFunctionMark_comma_3>null<saveTranslateFunctionMark_comma_3>t1.tot_plan_stnum_last_month<saveTranslateFunctionMark_rightquater_3>";
			String b = " remd_add_tksl<saveTranslateFunctionMark_leftquater_3>'+@P_day_id+'<saveTranslateFunctionMark_comma_3>'m'<saveTranslateFunctionMark_comma_3>cast<saveTranslateFunctionMark_leftquater_4>t1.tot_amt_last_month as number<saveTranslateFunctionMark_rightquater_4><saveTranslateFunctionMark_comma_3>cast<saveTranslateFunctionMark_leftquater_4>t1.tot_tksl_last_month as number<saveTranslateFunctionMark_rightquater_4><saveTranslateFunctionMark_rightquater_3>/ 					 <ConvertRemarkSafely_dash_8>							decode<saveTranslateFunctionMark_leftquater_3>t1.tot_plan_stnum_last_month<saveTranslateFunctionMark_comma_3>0<saveTranslateFunctionMark_comma_3>null<saveTranslateFunctionMark_comma_3>t1.tot_plan_stnum_last_month<saveTranslateFunctionMark_rightquater_3>";
			boolean ismatch = ConvertRemarkSafely.equals(a,b,(eq)->{
				return eq.replaceAll("\\w+\\.(\\w+)", "$1");
			});
			System.out.println(ismatch);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
