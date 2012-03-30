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
		
		readAddress();
		setDropList();
		_btnSave.setText("Save");
		_btnSave.setMnemonic('S');
		_btnSave.setHorizontalAlignment(SwingConstants.LEFT);
		_btnSave.setHorizontalTextPosition(SwingConstants.RIGHT);
		_btnSave.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MapPosition savePos = new MapPosition();
				savePos._street = _ttfStreet.getText();
				savePos._city = _ttfCity.getText();
				savePos._province = _ttfProvince.getText();
				savePos._country = _ttfCountry.getText();
				savePos._pCode = _ttfPostCode.getText();
				savePos.writeAddress();
				_readList.add(savePos);
				_dropList.insertItemAt(savePos, _readList.size() - 1);
			}
		});
		
		_btnClear.setText("Clear");
		_btnClear.setMnemonic('C');
		_btnClear.setHorizontalAlignment(SwingConstants.LEFT);
		_btnClear.setHorizontalTextPosition(SwingConstants.RIGHT);
		_btnClear.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_ttfCountry.setText("");
				_ttfProvince.setText("");
				_ttfStreet.setText("");
				_ttfPostCode.setText("");
				_ttfCity.setText("");
			}
		});
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		
		
		//add(output);
		JPanel dialogPane = new JPanel();
		dialogPane.setBorder(new EmptyBorder(0, 0, 0, 0));
      	dialogPane.setOpaque(false);
      	dialogPane.setLayout(new BorderLayout());
      	dialogPane.setPreferredSize(new Dimension(350, 150));//
      	dialogPane.setBorder(new CompoundBorder(
  				new TitledBorder("Address"),
  				Borders.DIALOG_BORDER));
      	dialogPane.setLayout(new TableLayout(new double[][] {
  				{0.14, 0.2, 0.2, 0.2, TableLayout.FILL},// decide the rows
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
	/*
	public AddressDropList() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		_dropList = new JComboBox();
		_dropList.setSelectedIndex(0);
		_dropList.addActionListener(this);
		add(_dropList);
		add(_lblCountry);
		add(_ttfCountry);
		add(_lblProvince);
		add(_ttfProvince);
		add(_lblCity);
		add(_ttfCity);
		add(_ttfAddress);
		add(_lblAddress);
		add(_lblPostCode);
		add(_ttfPostCode);
		_mapPos = new ArrayList<MapPosition>();
	}
	*/
	
	class ComboBoxRenderer extends JLabel
    					   implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			int selectedIndex = ((Integer)value).intValue();
			/*
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            */
			readAddress();
			
            MapPosition Addr =  _readList.get(selectedIndex);
            setText(Addr.toString());
			return this;
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		MapPosition Selected = (MapPosition)cb.getSelectedItem();
		_ttfCountry.setText(Selected._country);
		_ttfProvince.setText(Selected._province);
		_ttfCity.setText(Selected._city);
		_ttfStreet.setText(Selected._street);
		_ttfPostCode.setText(Selected._pCode);
		/*
		OutputAddr = "";
		String [] sep = {Selected._street, Selected._city, Selected._province, Selected._country, Selected._pCode};
		int Comma = -1;
		for ( int i = 0; i < 5; i++ ) {
			if (!sep[i].equals(""))
				Comma ++;
		}
		
		for ( int i = 0; i < 5; i++ ) {
			if ( !sep[i].equals("") ) {
				OutputAddr += sep[i].replaceAll("\\s+", "+");
				if ( Comma > 0) {
					OutputAddr += ",";
					Comma --;
				}
			}
		}
		*/
		//output.setText(OutputAddr);
	}
	
	public String getAddress(){
		return this.OutputAddr;
	}
	
	public void readAddress(){
		try {
			FileInputStream fstream = new FileInputStream("AddressList.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				
				for ( int i = 0; i < 5; i++ ) { // clear tokens everytime 
					tokens[i] = "";
				}
				
				MapPosition temp = new MapPosition();
				//System.out.println(strLine);
				tokens = strLine.split(",", 5); // 5 is necessary here
				/*
				System.out.println("<" + tokens[0] + ">" +
								   "<" + tokens[1] + ">" +
								   "<" + tokens[2] + ">" +
								   "<" + tokens[3] + ">" +
								   "<" + tokens[4] + ">");
				*/
				temp._street = tokens[0];
				temp._city = tokens[1];
				temp._province = tokens[2];
				temp._country = tokens[3];
				temp._pCode = tokens[4];
				_readList.add(temp);
			}
			in.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void setDropList() {

		
		if (_readList.size() > 0) {
			MapPosition[] PosArray = new MapPosition[_readList.size()];
			_dropList = new JComboBox(_readList.toArray(PosArray));
			_dropList.setSelectedIndex(_selectedIndex);
			_dropList.addActionListener(this);
		}
	}
	
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
