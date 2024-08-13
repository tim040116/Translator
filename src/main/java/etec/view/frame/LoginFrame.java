package etec.view.frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import etec.view.application.AzureFileApplication;

import javax.swing.JTextField;
import java.awt.Label;
import java.awt.Button;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtId;
	private JTextField txtPass;
	private Button btnLogin = new Button("登入");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
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
	public LoginFrame() {
		style();
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AzureFileApplication.run();
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
	}
	
	/**
	 * 物件的外觀奢定
	 * */
	private void style() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(83, 73, 268, 35);
		contentPane.add(panel);
		panel.setLayout(null);
		
		Label label = new Label("帳號：");
		label.setBounds(10, 5, 52, 22);
		panel.add(label);
		
		txtId = new JTextField();
		txtId.setBounds(68, 5, 190, 21);
		panel.add(txtId);
		txtId.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBounds(83, 126, 268, 35);
		contentPane.add(panel_1);
		
		Label label_1 = new Label("密碼：");
		label_1.setBounds(10, 5, 52, 22);
		panel_1.add(label_1);
		
		txtPass = new JTextField();
		txtPass.setColumns(10);
		txtPass.setBounds(68, 5, 190, 21);
		panel_1.add(txtPass);
		
		btnLogin.setBounds(279, 167, 72, 22);
		contentPane.add(btnLogin);
	}
}
