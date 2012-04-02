/*This is the class of Dropdown List
  This class has its own layout. 
  In the SampleApp, I dont use the layout directly
  I add all the textfields and labels one by one
*/
package Provider.GoogleMapsStatic;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import Provider.GoogleMapsStatic.MapPosition;

import com.jgoodies.forms.factories.Borders;
import java.io.*;

public class AddressDropList extends JPanel
							 implements ActionListener {
	
	public JLabel _lblCountry = new JLabel("Country");
	public JTextField _ttfCountry = new JTextField();
	public JLabel _lblProvince = new JLabel("Province");
	public JTextField _ttfProvince = new JTextField();
	public JLabel _lblCity = new JLabel("City");
	public JTextField _ttfCity = new JTextField();
	public JLabel _lblStreet = new JLabel("Street");
	public JTextField _ttfStreet = new JTextField();
	public JLabel _lblPostCode = new JLabel("Postal Code");
	public JTextField _ttfPostCode = new JTextField();
	public String OutputAddr;
	public JLabel _lblSave = new JLabel("Saved");
	public JButton _btnSave = new JButton();
	public JButton _btnClear = new JButton();
	public ArrayList<MapPosition> _readList = new ArrayList<MapPosition>();
	public String [] tokens = {"", "", "", "", ""};
	
	public ArrayList<MapPosition> _mapPos;
	private int _selectedIndex = 0;
	public JComboBox _dropList;
	
	public AddressDropList() {
		
		readAddress(); // read the saved address
		setDropList(); // set the ComboBox 
		_btnSave.setText("Save");
		_btnSave.setMnemonic('S');
		_btnSave.setHorizontalAlignment(SwingConstants.LEFT);
		_btnSave.setHorizontalTextPosition(SwingConstants.RIGHT);
		_btnSave.addActionListener( new ActionListener() { // when click "save", the address in the textfield will be saved
			public void actionPerformed(ActionEvent e) {
				MapPosition savePos = new MapPosition(); // create a new MapPosition Object
				savePos._street = _ttfStreet.getText(); // set the value of the Object
				savePos._city = _ttfCity.getText();
				savePos._province = _ttfProvince.getText();
				savePos._country = _ttfCountry.getText();
				savePos._pCode = _ttfPostCode.getText();
				savePos.writeAddress(); // write into file
				_readList.add(savePos); // add the new Object into the ArrayList
				_dropList.insertItemAt(savePos, _readList.size() - 1);
			}
		});
		
		_btnClear.setText("Clear");
		_btnClear.setMnemonic('C');
		_btnClear.setHorizontalAlignment(SwingConstants.LEFT);
		_btnClear.setHorizontalTextPosition(SwingConstants.RIGHT);
		_btnClear.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) { // when click "clear", the textfields of address will be blank
				_ttfCountry.setText("");
				_ttfProvince.setText("");
				_ttfStreet.setText("");
				_ttfPostCode.setText("");
				_ttfCity.setText("");
			}
		});
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		
		
		
		JPanel dialogPane = new JPanel();
		dialogPane.setBorder(new EmptyBorder(0, 0, 0, 0));
      	dialogPane.setOpaque(false);
      	dialogPane.setLayout(new BorderLayout());
      	dialogPane.setPreferredSize(new Dimension(350, 150));//
      	dialogPane.setBorder(new CompoundBorder(
  				new TitledBorder("Address"),
  				Borders.DIALOG_BORDER));
      	dialogPane.setLayout(new TableLayout(new double[][] { //decide the rows and columns here
  				{0.14, 0.2, 0.2, 0.2, TableLayout.FILL},// decide the columns and the percentage of each row to the whole panel
  				{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));// decide columns
  			((TableLayout)dialogPane.getLayout()).setHGap(5);
  			((TableLayout)dialogPane.getLayout()).setVGap(5);
  		_lblStreet.setHorizontalAlignment(SwingConstants.RIGHT);
  		_lblCity.setHorizontalAlignment(SwingConstants.RIGHT);
  		_lblProvince.setHorizontalAlignment(SwingConstants.RIGHT);
  		_lblCountry.setHorizontalAlignment(SwingConstants.RIGHT);
  		_lblPostCode.setHorizontalAlignment(SwingConstants.RIGHT);
  		_lblSave.setHorizontalAlignment(SwingConstants.RIGHT);
  		dialogPane.add(_lblStreet, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_ttfStreet, new TableLayoutConstraints(1, 0, 4, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_lblCity, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_ttfCity, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_lblProvince, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_ttfProvince, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_lblCountry, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_ttfCountry, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_lblPostCode, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_ttfPostCode, new TableLayoutConstraints(3, 2, 3, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_lblSave, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_dropList, new TableLayoutConstraints(1, 3, 4, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_btnSave, new TableLayoutConstraints(4, 1, 4, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		dialogPane.add(_btnClear, new TableLayoutConstraints(4, 2, 4, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		add(dialogPane);
	}
	
	// because the item in the ComboBox is not String, is a Object so I need to write a renderer
	class ComboBoxRenderer extends JLabel
    					   implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			int selectedIndex = ((Integer)value).intValue(); 

			readAddress(); 
			
            MapPosition Addr =  _readList.get(selectedIndex); // get the selected Object
            setText(Addr.toString()); // set the text of displaying in the ComboBox
			return this;
		}
		
	}
	
	/* 
	 * when I choose one address in the dropdown list, 
	 * the textfield will be filled automatically with the selected address
	 */
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		MapPosition Selected = (MapPosition)cb.getSelectedItem();
		_ttfCountry.setText(Selected._country);
		_ttfProvince.setText(Selected._province);
		_ttfCity.setText(Selected._city);
		_ttfStreet.setText(Selected._street);
		_ttfPostCode.setText(Selected._pCode);

	}
	
	public String getAddress(){
		return this.OutputAddr; // return the string for output (get map)
	}
	
	public void readAddress(){
		try {//read file
			FileInputStream fstream = new FileInputStream("AddressList.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) { // read line until reach the end of the file
				
				for ( int i = 0; i < 5; i++ ) { // clear tokens everytime 
					tokens[i] = "";
				}
				
				MapPosition temp = new MapPosition();
				//System.out.println(strLine);
				tokens = strLine.split(",", 5); // 5 is necessary here, get 5 strings
				/*
				System.out.println("<" + tokens[0] + ">" +
								   "<" + tokens[1] + ">" +
								   "<" + tokens[2] + ">" +
								   "<" + tokens[3] + ">" +
								   "<" + tokens[4] + ">");
				*/
				temp._street = tokens[0]; // set the value of the MapPosition
				temp._city = tokens[1];
				temp._province = tokens[2];
				temp._country = tokens[3];
				temp._pCode = tokens[4];
				_readList.add(temp); // add the red address into the ArrayList
			}
			in.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void setDropList() {

		// initial the ComboBox
		if (_readList.size() > 0) {
			MapPosition[] PosArray = new MapPosition[_readList.size()];
			_dropList = new JComboBox(_readList.toArray(PosArray));
			_dropList.setSelectedIndex(_selectedIndex);
			_dropList.addActionListener(this);
		}
	}
	
	// main is just for run a demo
	public static void main(String[] args) { 
        //Create and set up the window.
		ArrayList<MapPosition> temp = new ArrayList<MapPosition>();
		MapPosition p1 = new MapPosition();
		MapPosition p2 = new MapPosition();
		MapPosition p3 = new MapPosition();
		p1._street = "70 The Pond Road";
		p2._city = "Toronto";
		p2._province = "Ontario";
		p3._pCode = "M5V 3V9";
		p3._street = "35 Mariner Terrace";
		p3._city = "Toronto";
		p3._province = "Ontario";
		p3._country = "Canada";
		temp.add(p1);
		temp.add(p2);
		temp.add(p3);
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AddressDropList test = new AddressDropList();
        
                 
        //Add content to the window.
        frame.add(test, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}
	
}
