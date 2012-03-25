package testFile;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

// TODO
public class ZoomSlider extends JPanel
							implements ActionListener,
									WindowListener,
									ChangeListener {
	
	static final int FPS_MIN = 1;
	static final int FPS_MAX = 19;
	static final int FPS_INIT = 14;
	int frameNumber = 0;
	int NUM_FRAMES = 14;
	int zoomValue = 0;
	
	public ZoomSlider() {
		
		//Create the slider.
		JSlider zoomSlider = new JSlider( JSlider.HORIZONTAL,
                FPS_MIN, FPS_MAX, FPS_INIT );
		zoomSlider.addChangeListener( this );
		
		/*
		//Create the label.
		JLabel zoomLabel = new JLabel( "Zoom", JLabel.LEFT );
		zoomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);		
		
		//Turn on labels at major tick marks.
		zoomSlider.setMajorTickSpacing(10);
		zoomSlider.setMinorTickSpacing(1);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 15);
        zoomSlider.setFont(font);
        
        //Put together
        add(zoomLabel);
        */
        add(zoomSlider);
        //setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
	}
	
	 /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }
    
    public void stateChanged( ChangeEvent e ) {
    	JSlider source = (JSlider)e.getSource();
    	if ( !source.getValueIsAdjusting() ) {
    	}
    }

	@Override
	public void windowActivated(WindowEvent w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}