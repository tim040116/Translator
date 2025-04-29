package etec.src.tool.project.replace.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class CompareToolFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtField_1;
	private JTextField txtField_2;
	private JTextArea txtLog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CompareToolFrame frame = new CompareToolFrame();
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
	public CompareToolFrame() {
		setTitle("字元比對工具");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1051, 543);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel pnlMain = new JPanel();
		pnlMain.setBounds(10, 10, 1020, 488);
		contentPane.add(pnlMain);
		pnlMain.setLayout(null);
		
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBounds(10, 10, 1000, 135);
		pnlMain.add(pnlHeader);
		pnlHeader.setLayout(null);
		
		JPanel pnlInput = new JPanel();
		pnlInput.setBounds(0, 0, 773, 135);
		pnlHeader.add(pnlInput);
		pnlInput.setLayout(null);
		
		JPanel pnlInput1 = new JPanel();
		pnlInput1.setBounds(10, 10, 753, 51);
		pnlInput.add(pnlInput1);
		pnlInput1.setLayout(null);
		
		JLabel lbl1 = new JLabel("    字串一 : ");
		lbl1.setBounds(10, 10, 75, 31);
		pnlInput1.add(lbl1);
		
		txtField_1 = new JTextField();
		txtField_1.setBounds(95, 10, 648, 31);
		pnlInput1.add(txtField_1);
		txtField_1.setColumns(10);
		
		JPanel pnlInput2 = new JPanel();
		pnlInput2.setBounds(10, 74, 753, 51);
		pnlInput.add(pnlInput2);
		pnlInput2.setLayout(null);
		
		JLabel lbl2 = new JLabel("    字串二 : ");
		lbl2.setBounds(10, 10, 75, 31);
		pnlInput2.add(lbl2);
		
		txtField_2 = new JTextField();
		txtField_2.setColumns(10);
		txtField_2.setBounds(95, 10, 648, 31);
		pnlInput2.add(txtField_2);
		
		JButton btnCompare = new JButton("比對");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtLog.setText("");
				String str1 = txtField_1.getText();
				String str2 = txtField_2.getText();
				int len1 = str1.length();
				int len2 = str2.length();
				int len = Math.max(len1,len2);
				for(int i=0 ; i<len ; i++) {
					String c1 = i+1>len1?"NULL":String.valueOf(str1.charAt(i));
					String c2 = i+1>len2?"NULL":String.valueOf(str2.charAt(i));
					if(!c1.equals(c2)) {
						txtLog.append("出現差異\t位置："+(i+1)+"\t字串1："+c1+"\t字串2："+c2+"\r\n");
					}
				}
			}
		});
		btnCompare.setBounds(783, 10, 207, 51);
		pnlHeader.add(btnCompare);
		
		JButton btnTruncate = new JButton("清除");
		btnTruncate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtField_1.setText("");
				txtField_2.setText("");
				txtLog.setText("");
			}
		});
		btnTruncate.setBounds(783, 71, 207, 51);
		pnlHeader.add(btnTruncate);
		
		JPanel pnlLog = new JPanel();
		pnlLog.setBounds(10, 155, 1000, 323);
		pnlMain.add(pnlLog);
		pnlLog.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 1000, 323);
		pnlLog.add(scrollPane);
		
		txtLog = new JTextArea();
		scrollPane.setViewportView(txtLog);
		txtLog.setEditable(false);
		txtLog.setLineWrap(false);
		txtLog.setWrapStyleWord(false);
		((DefaultCaret)txtLog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
}
