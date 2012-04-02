// This is the class used to save the details of an address
// This class create a Object with 

package Provider.GoogleMapsStatic;

import javax.swing.JLabel;
import java.io.*;

public class MapPosition {
	
	public String _country;
	public String _province;
	public String _city;
	public String _street;
	public String _pCode;
	public String _outputString;
	
	// default constructor
	public MapPosition() {
		_country = "";
		_province = "";
		_city = "";
		_street = "";
		_pCode = "";
	}
	
	// write the address into the AddressList.txt
	public void writeAddress() {
		File f;
		f = new File("AddressList.txt");
		if (!f.exists()){ // if file is not exist, create a new one
			try{
				f.createNewFile();
				System.out.println("file is success to create");	 
			}catch (Exception e) {
				System.out.println("file is not exist and fail to create");	 
			}
		}
		try{
			if (!_street.equals("") ||
				!_city.equals("") ||
				!_province.equals("") ||
				!_country.equals("") ||
				!_pCode.equals("")) { // if the address in not blank
				
				FileWriter fstream = new FileWriter("AddressList.txt",true); 
				BufferedWriter out = new BufferedWriter(fstream);
				String w = "";
				w = _street + "," 
				  + _city + ","
				  + _province + ","
				  + _country + ","
				  + _pCode; // this is the format to write the address, easy to read and use later
				out.write(w); // write the address
				out.newLine(); // each address use a single line
				out.close();
			}
		}catch (Exception e) {
			System.out.println("fail to write");
		}
	}
	
	public String toString() {
		String s = "";
		String [] sep = {_street, _city, _province, _country, _pCode};
		int Comma = -1;
		for ( int i = 0; i < 5; i++ ) {
			if (!sep[i].equals(""))
				Comma ++;
		}
		
		for ( int i = 0; i < 5; i++ ) {
			if ( !sep[i].equals("") ) {
				s += sep[i];
				this._outputString += sep[i];
				if ( Comma > 0) {
					s += ", ";
					Comma --;
				}
			}}
		return s;
	}
}
