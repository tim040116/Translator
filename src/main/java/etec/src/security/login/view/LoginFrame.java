package etec.src.security.login.view;

import java.awt.Button;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import etec.common.interfaces.TranslatorApplication;
import etec.common.model.VersionModel;
import etec.framework.security.restriction.interfaces.Reviewer;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtId;
	private JPasswordField txtPass;
	private Button btnLogin = new Button("Log in");
	private static int cntFail = 0;

	/**
	 * <h1>登入的UI</h1>
	 * <p></p>
	 * <p></p>
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年8月16日	Tim	建立功能
	 *
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	application	成功時會開啟的UI
	 * @param	reviewer	檢核邏輯
	 * @throws	e
	 * @see
	 * @return	return_type
			 */
	public LoginFrame(TranslatorApplication application,Reviewer reviewer) {
		setTitle("登入 - "+VersionModel.VERSION);
		style();
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Map<String,String> args = new HashMap<>();
				args.put("id", txtId.getText());
				args.put("pass", txtPass.getText());
				int rsp = reviewer.check(args);
				System.out.println(rsp);
				if(rsp==1) {
					application.run();
					dispose();
					return;
				}else if(rsp==-1){
					txtId.setText("權限過期");
					cntFail+=2;
				}else if(rsp==-2){
					txtId.setText("帳密錯誤");
					cntFail++;
				}else{
					txtId.setText("程式錯誤");
					cntFail+=2;
				}
				if(cntFail>3) {dispose();}
			}
		});
	}

	/**
	 * 物件的外觀奢定
	 * */
	private void style() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

		txtPass = new JPasswordField();
		txtPass.setColumns(10);
		txtPass.setBounds(90, 5, 150, 30);
		pnlPass.add(txtPass);

		btnLogin.setBounds(279, 167, 72, 22);
		contentPane.add(btnLogin);
	}
}
