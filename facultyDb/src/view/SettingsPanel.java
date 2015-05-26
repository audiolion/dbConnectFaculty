package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import view.FacultyListener;
import data.DataFetch;
import data.Utils;

@SuppressWarnings("serial")
public class SettingsPanel extends JPanel {

	private static final String USER_DEFAULT = "User Id...";
	private static final String PASS_DEFAULT = "Password...";
	
	private String msg = "Password incorrect.";
	private String title = "Authentication Error";
	
	private DataFetch df;
	private Integer userID;
	protected JTextField user;
	protected JPasswordField pass;
	private FacultyListener listener;
	private JButton back;
	private JPanel center;
	private JPanel loginPanel;
	private GridBagConstraints c;
	

	/**
	 * constructor
	 * @param listener parent component to report back to
	 */
	public SettingsPanel(FacultyListener listener) {
		super(new BorderLayout());
		this.listener = listener;
		center = new JPanel(new GridBagLayout());
		userID = new Integer(0);
		user = new JTextField(USER_DEFAULT);
		user.setForeground(Color.GRAY);
		user.setSize(new Dimension(110,25));
		pass = new JPasswordField(PASS_DEFAULT);
		pass.setSize(new Dimension(110,25));
		pass.setForeground(Color.GRAY);
		pass.setEchoChar((char)0);
		back = new JButton("Back");
		df = DataFetch.getInstance();
		fillComponents();
		initActions();
	}
	
	/**
	 * adds components to this panel
	 */
	private void fillComponents() {
		loginPanel = new JPanel();
		loginPanel.setSize(new Dimension(250,100));
		loginPanel.setLayout(new MigLayout());
		loginPanel.add(user, "growx, push, wrap, align");
		loginPanel.add(pass, "span, growx, wrap, align");
		loginPanel.add(new JLabel("Domain: RIT"), "align");
		this.add(loginPanel);
	}
	
	/**
	 * sets current user
	 */
	public void setID(Integer id) {
		this.userID = id;
	}
	
	/**
	 * clears text of the password field
	 */
	public void clearText() {
		this.pass.setText("");
	}
	
	/**
	 * initializes action listeners on components
	 */
	private void initActions() {
		this.back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.act("HOME", "");
			}
			
		});
		this.user.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e){
				if(user.getText().equals(USER_DEFAULT)){
					user.setText("");
					user.setForeground(Color.BLACK);
				}
			}
			@Override
			public void focusLost(FocusEvent e){
				if(user.getText().equals("")){
					user.setText(USER_DEFAULT);
					user.setForeground(Color.GRAY);
				}
			}
		});
		this.pass.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e){
				if(new String(pass.getPassword()).equals(PASS_DEFAULT)){
					pass.setText("");
					pass.setForeground(Color.BLACK);
					pass.setEchoChar('*');
				}
			}
			@Override
			public void focusLost(FocusEvent e){
				if(pass.getPassword().length == 0){
					pass.setEchoChar((char)0);
					pass.setForeground(Color.GRAY);
					pass.setText(PASS_DEFAULT);
				}
			}
		});
		this.pass.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					boolean result = df.login(user.getText(), String.valueOf(pass.getPassword()));
//					System.out.println(Utils.MD5(String.valueOf(pass.getPassword())));
//					System.out.println(result);
//					if(df.login(userID, Utils.MD5(String.valueOf(pass.getPassword())))) {
//						listener.act("SETTINGS", "");
//					} else {
//						JOptionPane.showMessageDialog(null, msg, title,
//								JOptionPane.ERROR_MESSAGE, null);
//					}
					user.setText("");
					pass.setText("");
				}
			}
			
		});
	}
}