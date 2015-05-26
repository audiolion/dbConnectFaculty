package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import data.DataFetch;

public class GUIEntryPoint extends JFrame implements FacultyListener {

	private JTabbedPane jtp;
	private PaperSearchPanel psp;
	private PaperAbstractPanel pap;
	private SettingsPanel sp;
	
	private static JMenuBar jmb;
	private JMenuItem login;
	private JMenuItem close;
	private JMenuItem about;
	private JMenuItem refreshConnectionOption;
	private JMenuItem terminateConnection;
	private JPanel tabs;
	private JPanel cards;
	
	private CardLayout layout;
	
	
	private final DataFetch df;
	private Properties prop;
	
	private String propsUSER = "";
	private String propsPASS = "";
	
	public static final String PROPS_PRIVATE = "/data/resources/props";
	public static final String TITLE = "FacultyApp";
	
	public GUIEntryPoint(String title) throws SQLException {
		super();
		this.df = DataFetch.getInstance();
		this.df.setListener(this);
		prop = new Properties();
		establishConnection();
		initComponents();
		initActions();
		fillComponents();
		
	}
	
	private void establishConnection(){
		InputStream is = GUIEntryPoint.class.getResourceAsStream(PROPS_PRIVATE);
		if(is != null){
			try{
				prop.load(is);
			}catch(IOException e){
				showError("Properties file could not be found.", "Error");
			}
		}else{
			try {
				throw new FileNotFoundException();
			} catch(FileNotFoundException e) {
				showError("property file '" + PROPS_PRIVATE
						+ "' not found in the classpath", "Error");
			}
		}
		
		propsUSER = prop.getProperty("user");
		propsPASS = prop.getProperty("password");
		this.df.connectToDb(propsUSER, propsPASS);
	}
	
	public void initComponents(){
		jtp = new JTabbedPane();
		psp = new PaperSearchPanel(this);
		pap = new PaperAbstractPanel(this);
		jmb = new JMenuBar();
		refreshConnectionOption = new JMenuItem("Connect");
		terminateConnection = new JMenuItem("Close Connection");
		login = new JMenuItem("Login");
		close = new JMenuItem("Close");
		about = new JMenuItem("About");
		tabs = new JPanel(new BorderLayout());
		cards = new JPanel(layout = new CardLayout());
	}
	
	private void initActions() {
		this.refreshConnectionOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				establishConnection();
				JOptionPane.showMessageDialog(null, "Connection Established");
			}
		});
		this.terminateConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				df.closeConnection();
				JOptionPane.showMessageDialog(null, "Connection Terminated");
			}
		});
		this.about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(null, "Author: Ryan Robert Castner\nVersion:1.0\nDesigned For ISTE-330");
			}
		});
		this.close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				df.closeConnection();
				dispose();
			}
		});
	}
	
	private void fillComponents() {
		JMenu fileMenu = new JMenu("File");
		JMenu connectionMenu = new JMenu("Connection");
		connectionMenu.add(refreshConnectionOption);
		connectionMenu.add(terminateConnection);
		jmb.add(fileMenu);
		jmb.add(connectionMenu);
		setJMenuBar(jmb);
		jtp.addTab("HOME", psp = new PaperSearchPanel(this));
		jtp.addTab("PAPERABS", pap = new PaperAbstractPanel(this));
//		jtp.addTab("FIELDS", fldp = new FieldsPanel(this));
		jtp.addTab("SETTINGS", sp = new SettingsPanel(this));

		jtp.setMnemonicAt(0, KeyEvent.VK_1);
		jtp.setMnemonicAt(1, KeyEvent.VK_2);
		jtp.setMnemonicAt(2, KeyEvent.VK_3);
//		jtp.setMnemonicAt(3, KeyEvent.VK_4);


		tabs.add(jtp, BorderLayout.CENTER);
		cards.add(tabs, "ENTER");
		layout.show(cards, "ENTER");
		this.add(cards);
	}
	
	public static void showError(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title,
				JOptionPane.ERROR_MESSAGE, null);
	}
	
	protected static void createAndShowGUI() {
		JFrame f = null;
		try {
			f = new GUIEntryPoint(TITLE);
			
		} catch (SQLException e) {
			showError(e.getMessage(), "SQLException");
		}


		// Setup JFrame
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setPreferredSize(new Dimension(1300,600));
		f.pack(); // Pack before setting location (this determines size)

		// Get the current screen's size
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		// Compute and set the location so the frame is centered
		int x = screen.width/2-f.getSize().width/2;
		int y = screen.height/2-f.getSize().height/2;
		f.setLocation(x, y);
		f.setVisible(true);
	}
	
	public void act(String command, String argument) {
		switch(command) {
		case "ENTER":
			layout.show(cards, "ENTER");
			break;
		case "HOME":
			psp.jta.setText(argument);
			psp.updateModel();
			jtp.setSelectedIndex(0);
			break;
		case "PAPERABS":
			pap.updatePaperAbstractEntry(argument);
			jtp.setSelectedIndex(1);
			break;
		case "SETTINGS":
			jtp.setSelectedIndex(2);
			break;
		}
	}
	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException{
		// Set the Nimbus look and feel because it's new and cool looking
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// Will be set to default LAF
		}
		Runnable doCreateAndShowGUI = new Runnable() {

			@Override
			public void run() {
				createAndShowGUI();
			}
		};
		SwingUtilities.invokeAndWait(doCreateAndShowGUI);
	}
}
