package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import view.GUIEntryPoint;


/**
 * This class will be used for the majority of Database IO
 * @author Ryan Castner
 */
public class DataFetch {

	/*
	 * static data
	 */
	private static final String driver = "org.postgresql.Driver"; 
	private static final String url = "jdbc:postgresql://localhost:8081/";
	private static final String SQLERROR = "SQLException";
	
	/*
	 * instance variables
	 */
	private Statement stmt;
	private Connection con;
	private GUIEntryPoint listener;

	public static void main(String[] args){
		DataFetch instance = DataFetch.getInstance();
		instance.connectToDb("castnersstuff", "glint51%barbecue");
		try {
			instance.executeAndPrintTestQuery();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Singleton Wrapper class 
	 */
	private static class SingletonWrapper {
		private static DataFetch INSTANCE = new DataFetch();
	}

	/**
	 * Use as an alternative to a constructor. This will ensure singleton
	 * behavior
	 * @return	the singleton instance of DataFetch
	 */
	public static DataFetch getInstance() {
		return SingletonWrapper.INSTANCE;
	}

	/**
	 * Private constructor ensures no extraneous DataFetchers will be created
	 */
	private DataFetch() {
		listener = null;
	}

	/**
	 * Sets the "listener" for error messages to be displayed
	 * @param listener	the component to be updated with error messages
	 */
	public void setListener(GUIEntryPoint listener) {
		if(this.listener == null) {
			this.listener = listener;
		} else {
			System.err.println("Listener has already been set.");
		}
	}

	/**
	 * returns the main faculty model
	 * @return
	 */
	public DefaultTableModel getMainFacultyTableModel() {
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select t_id, t_name from trainer order by t_id;");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public String getPaperAbs(String paper){
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select abstract from paper where title ilike '" + paper + "';");
		} catch (SQLException e){
			error = true;
			displayError(e.getMessage(),SQLERROR);
		}
		String abs = "";
		if(error)
			return abs;
		else{
			try {
				if(rs.next()){
					abs = rs.getString("abstract");
				}
			} catch (SQLException e) {
				displayError(e.getMessage(), SQLERROR);
			}
		}
		return abs;
	}
	
	/**
	 * removes the researcher from the database
	 * @param researcherId the unique researcher ID to remove from database
	 */
	public void removeResearcher(String researcherId) {
		try {
			stmt.execute("delete from researcher where id = " + researcherId + " cascade;");
		} catch (SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
	}
	
	
	public void addResearcher(String fname, String lname, String hash,
			String salt, String email, String status) {
		try {
			stmt.execute("insert into researcher (first_name, last_name,"
					+ " password_hash, password_salt, email, status)"
					+ " values ('+fname+','+lname+','+hash+','+salt+','+email+','+status+');");
		} catch (SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
	}
	
	public String getResearcherNameFromID(Integer id) {
		ResultSet rs = null;
		String name = new String();
		try {
			rs = stmt.executeQuery("select id from researcher where " +
					"id = " + id + ";");
			if(rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e){
			displayError(e.getMessage(), SQLERROR);
		}
		return name;
	}
	
	/**
	 * 
	 * @param id id of trainer whose name is to be updated
	 * @param newName new name of the trainer
	 */
	public void updateTrainerNameWithID(Integer id, String newName) {
		try {
			stmt.execute("update trainer set t_name = '" + 
					newName + "' where t_id = " + id + ";");
		} catch (SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
	}
	
	public DefaultTableModel getDefaultAuthorModel() {
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select * from v_author;");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public DefaultTableModel getSearchAuthorModel(String search){
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select distinct researcher.first_name, researcher.last_name"
					+" from paper_keywords left join paper on paper.id = paper_keywords.paper_id"
					+" left join authorship on authorship.paper_id = paper.id left join researcher"
					+" on authorship.researcher_id = researcher.id where paper.title ilike '" + search + "';");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public DefaultTableModel getDefaultKeywordModel() {
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select keyword from paper_keywords order by keyword;");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public DefaultTableModel getSearchKeywordModel(String search){
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select keyword from paper_keywords left join paper on id = paper_id where title ilike '" + search + "' order by keyword;");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public DefaultTableModel getDefaultPaperPanelModel() {
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select * from v_paper order by title;");
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public DefaultTableModel getSearchPaperPanelModel(String search){
		boolean error = false;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(
					"select * from v_paper where title ilike '" + search + "%' union "
					+ "select * from v_paper where keyword ilike '" + search + "%' union "
					+ "select * from v_paper where first_name ilike '" + search + "%' union "
					+ "select * from v_paper where last_name ilike '" + search + "%' order by title;"
					);
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		}
		
		return error ? new DefaultTableModel() : buildTableModel(rs);
	}
	
	public void connectToDb(String user, String pass) {
		try {
			this.establishConnection(url, user, pass);
			this.createStatement();
		} catch (SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
	}
	
	/**
	 * 
	 * @param username who is logging in
	 * @param hash password of user
	 * @return true if the user logged in successfully
	 */
	public boolean login(String username, String pass) {
		boolean authResult = false;
		MemberOfAuth auth = new MemberOfAuth("MAIN");
		authResult = auth.isMemberOf("STUDENTS", username, pass);
//		ResultSet rs = null;
//		try {
//			rs = stmt.executeQuery("select password_hash from researcher where id = " + userID + ";");
//			if(rs.next()) {
//				String dbHash = rs.getString("password_hash");
//				authResult = dbHash.equals(hash);
//			}
//		} catch (SQLException e) {
//			displayError(e.getMessage(), SQLERROR);
//		}
		return authResult;
	}
	
	public DefaultTableModel buildTableModel2(ArrayList<ArrayList<String>> rs, ArrayList<String> coln){
		Vector<String> columnNames = new Vector<String>();
		for(int i = 0; i < coln.size(); i++){
			columnNames.add(coln.get(i));
		}
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
		for(ArrayList<String> lst : rs){
			Vector<String> row = new Vector<String>();
			for(String field : lst){
				row.add(field);
			}
			data.add(row);
		}
		return new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int col){
				return false;
			}
		};
	}
	
	public ArrayList<String> getColumnNames(ResultSetMetaData rsmd){
		ArrayList<String> columnNames = new ArrayList<String>();
		
		int columnCount;
		try{
			columnCount = rsmd.getColumnCount();
		
			for(int column = 1; column < columnCount; column++){
				columnNames.add(rsmd.getColumnName(column));
			}
		}catch (SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
		return columnNames;
	}
	
	public ArrayList<ArrayList<String>> getData(ResultSet rs){
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
		int columnCount;
		try{
			columnCount = rs.getMetaData().getColumnCount();
		
			while(rs.next()){
				ArrayList<String> record = new ArrayList<String>();
				
				for(int column = 1; column < columnCount; column++){
					record.add(rs.getObject(column).toString());
				}
				
				data.add(record);
			}
		}catch(SQLException e) {
			displayError(e.getMessage(), SQLERROR);
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param rs result set to make table model of
	 * @return table model of the result set
	 */
	@SuppressWarnings("serial")
	public DefaultTableModel buildTableModel(ResultSet rs) {
		boolean error = false;
		Vector<String> columnNames = new Vector<String>();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		try {
			ResultSetMetaData metaData = rs.getMetaData();

			// names of columns

			int columnCount = metaData.getColumnCount();
			for (int column = 1; column <= columnCount; column++) {
				columnNames.add(metaData.getColumnName(column));
			}

			// data of the table

			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
					vector.add(rs.getObject(columnIndex));
				}
				data.add(vector);
			}
		} catch (SQLException e) {
			error = true;
			displayError(e.getMessage(), SQLERROR);
		} catch (NullPointerException e) {
			error = true;
		}
		return error ? new DefaultTableModel() : new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int col){
				return false;
			}
		};
	}

	/**
	 * Displays the error message that occurred
	 * @param msg 	the message 
	 * @param title	the title of the error message
	 */
	private void displayError(String msg, String title) {
		if(listener == null) {
			JOptionPane.showMessageDialog(null, msg, title,
					JOptionPane.ERROR_MESSAGE, null);
		} else {
			listener.showError(msg, title);
		}
	}


	/**
	 * 
	 * @param url	full url to database including database name (not needed though)
	 * @param user	username of the psql account
	 * @param pass	that user's password
	 * @throws SQLException				if something is wrong with the connection
	 * @throws ClassNotFoundException	if the driver for psql can not be found
	 */	
	public void establishConnection(String url, String user, String pass) 
			throws SQLException {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			displayError(e.getMessage(), "ClassNotFoundExcpetion");
		}
		this.con = DriverManager.getConnection(url + user, user, pass);
	}
	
	public void closeConnection() {
		try {
			this.con.close();
		} catch (SQLException e) {
			displayError(e.getMessage(), "SQLException");
		}
	}

	/**
	 * Creates a statement for the DataFetch object to execute queries with.
	 * @throws SQLException
	 */
	public void createStatement() throws SQLException {
		this.stmt = con.createStatement();
	}

	/**
	 * @return the statement that manages queries
	 */
	public Statement getStatement() {
		return this.stmt;
	}

	/**
	 * Tests the connection to database
	 * @throws SQLException
	 */
	public void executeAndPrintTestQuery() throws SQLException {
		ResultSet rs = stmt.executeQuery("select * from field;");
		while(rs.next()) {
			System.out.println("Field Id: " + rs.getInt("id") + '\t' + rs.getString("name"));
		}
	}
	
	public ArrayList<String> getResearcherFname(String search){
		ArrayList<String> queryData = new ArrayList<String>();
		try{
			ResultSet rs = stmt.executeQuery("select * from researcher where fname ilike '" + search + "';");
			while(rs.next()){
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
					queryData.add(rs.getString(i));
				}
			}
		}catch(SQLException e){
			displayError(e.getMessage(), "SQL Exception");
		}
		return queryData;
	}
	
	public ArrayList<String> getResearcherLname(String search){
		ArrayList<String> queryData = new ArrayList<String>();
		try{
			ResultSet rs = stmt.executeQuery("select * from researcher where lname ilike '" + search + "';");
			while(rs.next()){
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
					queryData.add(rs.getString(i));
				}
			}
		}catch(SQLException e){
			displayError(e.getMessage(), "SQL Exception");
		}
		return queryData;
	}
}
