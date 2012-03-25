package Provider.GoogleMapsStatic;

import javax.swing.JLabel;

public class MapPosition {
	
	public String _country;
	public String _province;
	public String _city;
	public String _address;
	public String _pCode;
	public String _outputString;
	
	public MapPosition() {
		_country = "";
		_province = "";
		_city = "";
		_address = "";
		_pCode = "";
	}
	
	public String toString() {
		String s = "";
		String [] sep = {_address, _city, _province, _country, _pCode};
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
	/*
	public String outputString() {
		String s = "";
		String [] sep = {_address, _city, _province, _country, _pCode};
		int Comma = -1;
		for ( int i = 0; i < 5; i++ ) {
			if (!sep[i].equals(""))
				Comma ++;
		}
		
		for ( int i = 0; i < 5; i++ ) {
			if ( !sep[i].equals("") ) {
				s += sep[i].replaceAll("\\s+", "+");
				if ( Comma > 0) {
					s += ",";
					Comma --;
				}
			}
		}
		return s;
	}
	*/
}
