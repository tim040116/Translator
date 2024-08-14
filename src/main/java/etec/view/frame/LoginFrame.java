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
	private Button btnLogin = new Button("Log in");
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
		
		JPanel pnlId = new JPanel();
		pnlId.setBounds(83, 73, 268, 35);
		contentPane.add(pnlId);
		pnlId.setLayout(null);
		
		Label lblId = new Label("ID：");
		lblId.setBounds(10, 5, 80, 30);
		pnlId.add(lblId);
		
		txtId = new JTextField();
		txtId.setBounds(90, 5, 150, 30);
		pnlId.add(txtId);
		txtId.setColumns(10);
		
		JPanel pnlPass = new JPanel();
		pnlPass.setLayout(null);
		pnlPass.setBounds(83, 126, 268, 35);
		contentPane.add(pnlPass);
		
		Label lblPass = new Label("Password：");
		lblPass.setBounds(10, 5, 80, 30);
		pnlPass.add(lblPass);
		
		txtPass = new JTextField();
		txtPass.setColumns(10);
		txtPass.setBounds(90, 5, 150, 30);
		pnlPass.add(txtPass);
		
		btnLogin.setBounds(279, 167, 72, 22);
		contentPane.add(btnLogin);
	}
}
