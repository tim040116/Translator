package etec.framework.file.excel_maker.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <h1>CSV容器</h1>
 * <p>
 * <br>形容此物件為CSV格式的容器
 * <br>配合{@link Column} 使用
 * <br>{@link SheetModel#sheetName()}為此檔案的檔名
 * </p>或著xls檔的頁籤名
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年6月12日	Tim	建立功能
 *
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetModel {

	/**
	 * 頁簽名或檔名
	 * */
	String sheetName();

}
