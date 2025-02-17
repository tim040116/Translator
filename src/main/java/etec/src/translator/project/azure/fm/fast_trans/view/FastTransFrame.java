package etec.src.translator.project.azure.fm.fast_trans.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JCheckBoxMenuItem;
import java.awt.Choice;
import java.awt.Button;
import java.awt.TextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class FastTransFrame {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FastTransFrame window = new FastTransFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FastTransFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 816, 100);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(10, 10, 796, 50);
		panel.add(panel_4);
		panel_4.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("replace file");
		lblNewLabel_1.setBounds(10, 0, 100, 50);
		panel_4.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(120, 10, 394, 28);
		panel_4.add(textField);
		textField.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 120, 816, 383);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 10, 300, 363);
		panel_1.add(panel_2);
		panel_2.setLayout(null);
		
		TextArea textArea = new TextArea();
		textArea.setBounds(0, 0, 300, 363);
		panel_2.add(textArea);
		
		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBounds(320, 10, 176, 363);
		panel_1.add(panel_2_1);
		panel_2_1.setLayout(null);
		
		Button button = new Button("GO");
		button.setBounds(10, 69, 156, 45);
		panel_2_1.add(button);
		
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("New check item");
		chckbxmntmNewCheckItem.setBounds(0, 120, 176, 233);
		panel_2_1.add(chckbxmntmNewCheckItem);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 0, 176, 63);
		panel_2_1.add(panel_3);
		panel_3.setLayout(null);
		
		Choice choice = new Choice();
		choice.setBounds(10, 31, 156, 20);
		panel_3.add(choice);
		
		JLabel lblNewLabel = new JLabel("SQL Type : ");
		lblNewLabel.setBounds(10, 0, 156, 28);
		panel_3.add(lblNewLabel);
				
		JPanel panel_2_2 = new JPanel();
		panel_2_2.setBounds(506, 10, 300, 363);
		panel_1.add(panel_2_2);
		panel_2_2.setLayout(null);
		
		TextArea textArea_1 = new TextArea();
		textArea_1.setBounds(0, 0, 300, 363);
		panel_2_2.add(textArea_1);
	}
}
