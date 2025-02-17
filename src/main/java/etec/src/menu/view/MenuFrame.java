package etec.src.menu.view;

import java.awt.EventQueue;
import java.awt.Label;
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

import etec.common.model.VersionModel;
import etec.framework.code.interfaces.Controller;
import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.fm.hist_export.controller.HisExportController;

public class MenuFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Label lblDescription;
	
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
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(726, 368, 169, 110);
		getContentPane().add(btnNewButton);
		
		JScrollPane scpList = new JScrollPane();
		scpList.setBounds(6, 6, 230, 472);
		getContentPane().add(scpList);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(248, 6, 647, 350);
		getContentPane().add(scrollPane);
		
		lblDescription = new Label("New label");
		scrollPane.setViewportView(lblDescription);
		
		DefaultListModel<AppListModel> lstmdl = new DefaultListModel<AppListModel>();
		list = new JList<AppListModel>(lstmdl);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lblDescription.setText("Choose : "+list.getSelectedValue());
				
		}});
		scpList.setViewportView(list);

		JLabel lblAlert = new JLabel("");
		lblAlert.setBounds(248, 368, 466, 110);
		getContentPane().add(lblAlert);
		lstmdl.addElement(new AppListModel("a1","aaa"));
		lstmdl.addElement(new AppListModel("b1","bbb"));
		lstmdl.addElement(new AppListModel("c1","ccc"));
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
		
		public AppListModel(String name, String value) {
			super();
			this.name = name;
			this.value = value;
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
		
		
	}
}
