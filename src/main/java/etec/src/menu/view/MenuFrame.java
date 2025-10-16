package etec.src.menu.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import etec.app.application.CompareToolApplication;
import etec.app.application.GreenPlumFileApplication;
import etec.app.application.HisExportApplication;
import etec.app.application.ReplaceToolApplication;
import etec.common.model.VersionModel;
import etec.framework.code.interfaces.Controller;
import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.fm.hist_export.controller.HisExportController;

public class MenuFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblDescription;
	
	private UIApplication targetApp;//選擇到的功能
	
	JList<AppListModel> list;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuFrame frame = new MenuFrame(new HisExportController());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MenuFrame(Controller controller) {
		setTitle("小工具  " + VersionModel.VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 915, 544);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("檔案");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("關閉");
		mnNewMenu.add(mntmNewMenuItem);
		getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("啟動");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(targetApp!=null) {
					targetApp.run();
				}
			}
		});
		btnNewButton.setBounds(726, 368, 169, 110);
		getContentPane().add(btnNewButton);
		
		JScrollPane scpList = new JScrollPane();
		scpList.setBounds(6, 6, 230, 472);
		getContentPane().add(scpList);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(248, 6, 647, 350);
		getContentPane().add(scrollPane);
		
		lblDescription = new JLabel("");
		scrollPane.setViewportView(lblDescription);
		
		DefaultListModel<AppListModel> lstmdl = new DefaultListModel<AppListModel>();
		list = new JList<AppListModel>(lstmdl);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lblDescription.setText("<html>"+list.getSelectedValue().getDescription()+"</html>");
				targetApp = list.getSelectedValue().getApp();
				
		}});
		scpList.setViewportView(list);

		JLabel lblAlert = new JLabel("");
		lblAlert.setBounds(248, 368, 466, 110);
		getContentPane().add(lblAlert);
		lstmdl.addElement(new AppListModel("PostgreSQL語法轉換","將Teradata語法轉換成PostgreSQL語法",new GreenPlumFileApplication()));
		lstmdl.addElement(new AppListModel("批量取代工具","對清單檔中所有項目進行取代",new ReplaceToolApplication()));
		lstmdl.addElement(new AppListModel("歷史資料匯出檔產生器","產出歷史資料匯出排程語法，"
				+ "讀取excel檔，產出tpt跟btq檔"
				+ "<br>excel要有兩個sheet"
				+ "<ol>"
				+ "<li>Table</li>"
				+ "需要匯出的表及相關設定"
				+ "<ol>"
				+ "<li>Database		: schema name</li>"
				+ "<li>Table name	: table name</li>"
				+ "<li>Where 條件		: 匯出資料的範圍條件</li>"
				+ "<li>路徑			: 產出檔案的路徑</li>"
				+ "<li>檔名			: </li>"
				+ "</ol>"
				+ "<li>column</li>"
				+ "表裡所包含的欄位資訊"
				+ "<ol>"
				+ "<li>Database		: schema name</li>"
				+ "<li>Table name	: table name</li>"
				+ "<li>Column		: column name</li>"
				+ "</ol>"
				+ "</ol>",new HisExportApplication()));
		lstmdl.addElement(new AppListModel("比對工具","找出兩個字串不一致的字元位置",new CompareToolApplication()));	
		
	}
	
	private static void p_getModel() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String,UIApplication> map = UIApplication.ApplicationScanner("etec.app.application");
	}
	
	class AppListModel{
		
		private String code;
		
		private UIApplication app;
		
		private String name;
		
		private String description;
		
		private String active;
		
		private String version;
		
		private String auth;
		
		private String upd_date;
		
		private String remark;
		
		public AppListModel(String name, String desc,UIApplication app) {
			super();
			this.name = name;
			this.setDescription(desc);
			this.setApp(app);
		}

		
		
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return name;
		}

		public UIApplication getApp() {
			return app;
		}

		public void setApp(UIApplication app) {
			this.app = app;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		
	}
}
