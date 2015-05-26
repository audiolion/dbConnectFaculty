package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import data.DataFetch;

@SuppressWarnings("serial")
public class PaperAbstractPanel extends JPanel {

	
	private static final String DEFAULT = "Search...";
	private DataFetch df;
	private JPanel west;
	private JTextArea abs;
	private JScrollPane jsp;
	private JScrollPane jspabs;
	private JScrollPane jspaut;
	private JTable table;
	private JTable tableaut;
	private FacultyListener listen;
	
	/**
	 * constructor
	 * @param listen the parent component to report back to
	 */
	public PaperAbstractPanel(FacultyListener listen) {
		super(new BorderLayout());
		this.setFocusable(true);
		this.listen = listen;
		this.df = DataFetch.getInstance();
		this.table = new JTable();
		this.table.getTableHeader().setReorderingAllowed(false);
		this.tableaut = new JTable();
		this.tableaut.getTableHeader().setReorderingAllowed(false);
		this.jsp = new JScrollPane(table);
		this.jspaut = new JScrollPane(tableaut);
		this.abs = new JTextArea();
		this.abs.setEditable(false);
		this.abs.setWrapStyleWord(true);
		this.abs.setLineWrap(true);
		this.jspabs = new JScrollPane(abs);
		this.initActions();
		JPanel center = new JPanel(new GridLayout(2,2));
		this.west = new JPanel();
		west.add(jsp);
		center.add(west, BorderLayout.WEST);
		center.add(jspabs);
		center.add(new JPanel());
		center.add(jspaut);
		this.add(center, BorderLayout.NORTH);

		this.updatePaperAbstractEntry("");
	}
	
	public JTable getTable() {
		return this.table;
	}
	
	/**
	 */
	public void updatePaperAbstractEntry(String paper){
		if(paper.equalsIgnoreCase("")){
			this.abs.setText("");
			this.table.setModel(df.getDefaultKeywordModel());
			this.tableaut.setModel(df.getDefaultAuthorModel());
			if(table.getColumnModel().getColumnCount() == 0) {
				return;
			}
			table.getColumnModel().getColumn(0).setHeaderValue("Keywords");
			tableaut.getColumnModel().getColumn(0).setHeaderValue("Author First");
			tableaut.getColumnModel().getColumn(1).setHeaderValue("Author Last");
			int width = 0;
			 for (int col = 0; col < tableaut.getColumnCount(); col++){
				 for (int row = 0; row < tableaut.getRowCount(); row++) {
				     TableCellRenderer renderer = tableaut.getCellRenderer(row, col);
				     Component comp = tableaut.prepareRenderer(renderer, row, col);
				     width = Math.max (comp.getPreferredSize().width, width);
				 }
			 }
		}else{
			this.abs.setText(df.getPaperAbs(paper));
			this.table.setModel(df.getSearchKeywordModel(paper));
			this.tableaut.setModel(df.getSearchAuthorModel(paper));
			if(table.getColumnModel().getColumnCount() == 0) {
				return;
			}
			table.getColumnModel().getColumn(0).setHeaderValue("Keywords");
			tableaut.getColumnModel().getColumn(0).setHeaderValue("Author First");
			tableaut.getColumnModel().getColumn(1).setHeaderValue("Author Last");
			int width = 0;
			 for (int col = 0; col < tableaut.getColumnCount(); col++){
				 for (int row = 0; row < tableaut.getRowCount(); row++) {
				     TableCellRenderer renderer = tableaut.getCellRenderer(row, col);
				     Component comp = tableaut.prepareRenderer(renderer, row, col);
				     width = Math.max (comp.getPreferredSize().width, width);
				 }
			 }
			 
		}
	}
	
	/**
	 * initializes actions of action components
	 */
	private void initActions() {
		this.table.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = table.getSelectedRow();
					
				String paper = (String) table.getValueAt(index, 0);
				listen.act("HOME", paper);
							
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
}

