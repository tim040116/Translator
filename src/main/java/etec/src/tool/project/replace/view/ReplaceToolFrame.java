package etec.src.tool.project.replace.view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Controller;
import etec.src.tool.project.replace.controller.ReplaceToolController;

public class ReplaceToolFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private JTextField txtMapping;
//	JProgressBar progressBar;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReplaceToolFrame frame = new ReplaceToolFrame(new ReplaceToolController());
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
	public ReplaceToolFrame(Controller controller) {
		setTitle("快速轉換");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 383);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 6, 500, 334);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JPanel pnlInput = new JPanel();
		pnlInput.setBounds(10, 10, 484, 50);
		panel.add(pnlInput);
		pnlInput.setLayout(null);
		
		JLabel lblInput = new JLabel("    檔案目錄：");
		lblInput.setBounds(6, 10, 80, 30);
		pnlInput.add(lblInput);
		
		txtInput = new JTextField();
		txtInput.setText(Params.config.INIT_INPUT_PATH);
		txtInput.setBounds(86, 10, 350, 30);
		pnlInput.add(txtInput);
		txtInput.setColumns(10);
		
		Button button = new Button("...");
		button.setBounds(442, 10, 42, 30);
		pnlInput.add(button);
		
		JPanel pnlMapping = new JPanel();
		pnlMapping.setBounds(10, 65, 484, 50);
		panel.add(pnlMapping);
		pnlMapping.setLayout(null);
		
		JLabel lblMapping = new JLabel("設定檔路徑：");
		lblMapping.setBounds(6, 10, 80, 30);
		pnlMapping.add(lblMapping);
		
		txtMapping = new JTextField();
		txtMapping.setText(Params.config.INIT_INPUT_PATH.replace("Target\\","replace_list.csv"));
		txtMapping.setColumns(10);
		txtMapping.setBounds(86, 10, 350, 30);
		pnlMapping.add(txtMapping);
		
		Button button_1 = new Button("...");
		button_1.setBounds(442, 10, 42, 30);
		pnlMapping.add(button_1);
		
		JTextArea txtLog = new JTextArea();
		txtLog.setEditable(false);
		txtLog.setBounds(10, 155, 484, 173);
		panel.add(txtLog);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(54, 167, 341, 40);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(26, 24, 10, 10);
		panel_2.add(panel_1);
		panel_1.setBackground(new Color(0, 0, 0));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(45, 123, 449, 20);
		panel.add(progressBar);
		
		JPanel pnlStatusColor = new JPanel();
		pnlStatusColor.setBackground(new Color(0, 0, 0));
		pnlStatusColor.setBounds(20, 123, 20, 20);
		panel.add(pnlStatusColor);
		
		Button btnRun = new Button("GO");
		btnRun.setBounds(519, 43, 157, 40);
		contentPane.add(btnRun);
		btnRun.setFont(new Font("Dialog", Font.BOLD, 14));
		btnRun.setActionCommand("");
		
		Checkbox ckbCaseInsensitive = new Checkbox("Case Insensitive ");
		ckbCaseInsensitive.setBounds(516, 89, 160, 36);
		contentPane.add(ckbCaseInsensitive);
		ckbCaseInsensitive.setState(true);
		
		
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						try {
							Map<String,Object> args = new HashMap<String,Object>();
							args.put("caseInsensitive",ckbCaseInsensitive.getState());
							args.put("inputPath",txtInput.getText());
							args.put("outputPath",Params.config.INIT_OUTPUT_PATH);
							args.put("mappingPath",txtMapping.getText());
							args.put("progressBar",progressBar);
							args.put("txtLog",txtLog);
							args.put("pnlStatusColor",pnlStatusColor);
							controller.run(args);
						} catch (Exception e1) {
							txtLog.append(e1.getMessage());
							pnlStatusColor.setBackground(Color.RED);
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
	}
}
