package etec.src.translator.project.azure.fm.formal.view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Controller;
import etec.src.tool.project.replace.controller.ReplaceToolController;
import etec.src.translator.project.azure.fm.formal.controller.FamilyMartController2;

public class TransduceFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private JTextField txtOutput;
	private JProgressBar progressBar;
	private boolean isLock = false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TransduceFrame frame = new TransduceFrame(new FamilyMartController2());
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
	public TransduceFrame(Controller controller) {
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

		Button btnInput = new Button("...");
		btnInput.setBounds(442, 10, 42, 30);
		pnlInput.add(btnInput);

		JPanel pnlOutput = new JPanel();
		pnlOutput.setBounds(10, 65, 484, 50);
		panel.add(pnlOutput);
		pnlOutput.setLayout(null);

		JLabel lblOutput = new JLabel("    產檔路徑：");
		lblOutput.setBounds(6, 10, 80, 30);
		pnlOutput.add(lblOutput);

		txtOutput = new JTextField();
		txtOutput.setText(Params.config.INIT_OUTPUT_PATH);
		txtOutput.setColumns(10);
		txtOutput.setBounds(86, 10, 350, 30);
		pnlOutput.add(txtOutput);

		Button btnOutput = new Button("...");
		btnOutput.setBounds(442, 10, 42, 30);
		pnlOutput.add(btnOutput);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 155, 488, 173);
		panel.add(scrollPane);
		
		JTextArea txtLog = new JTextArea();
		txtLog.setEditable(false);
		txtLog.setLineWrap(false);
		txtLog.setWrapStyleWord(false);
		((DefaultCaret)txtLog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		txtLog.setBounds(10, 155, 484, 173);
		scrollPane.setViewportView(txtLog);

		progressBar = new JProgressBar();
		progressBar.setBounds(45, 121, 449, 30);
		panel.add(progressBar);
		progressBar.setString("0 %");
		progressBar.setStringPainted(true);

		JPanel pnlStatusColor = new JPanel();
		pnlStatusColor.setBackground(new Color(0, 0, 0));
		pnlStatusColor.setBounds(10, 121, 30, 30);
		panel.add(pnlStatusColor);
		
		Button btnRun = new Button("GO");
		btnRun.setBounds(519, 43, 157, 40);
		contentPane.add(btnRun);
		btnRun.setFont(new Font("Dialog", Font.BOLD, 14));
		btnRun.setActionCommand("");

//		Checkbox ckbCaseInsensitive = new Checkbox("Case Insensitive ");
//		ckbCaseInsensitive.setBounds(516, 89, 160, 36);
//		contentPane.add(ckbCaseInsensitive);
//		ckbCaseInsensitive.setState(true);

		// 執行程式
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						if(isLock) {
							return;
						}
						isLock=true;
						try {
							Map<String, Object> args = new HashMap<String, Object>();
//							args.put("caseInsensitive", ckbCaseInsensitive.getState());
							args.put("inputPath", txtInput.getText());
							args.put("outputPath", Params.config.INIT_OUTPUT_PATH);
							args.put("mappingPath", txtOutput.getText());
							args.put("progressBar", progressBar);
							args.put("txtLog", txtLog);
							args.put("pnlStatusColor", pnlStatusColor);
							controller.run(args);
						} catch (Exception e1) {
							txtLog.append(e1.getMessage());
							pnlStatusColor.setBackground(Color.RED);
							e1.printStackTrace();
						}
						isLock=false;
					}
				}.start();
			}
		});

		// 來源路徑
		btnInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser;
				chooser = new JFileChooser(new File(txtInput.getText()));
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = chooser.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					txtInput.setText(file.getPath());
				}
			}
		});

		// 比對檔路徑
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser;
				chooser = new JFileChooser(new File(txtOutput.getText()));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = chooser.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					txtOutput.setText(file.getPath());
				}
			}
		});
	}

}
