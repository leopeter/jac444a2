package Provider.GoogleMapsStatic;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
 
public class ZoomSlider extends JPanel
                        implements 
                                   WindowListener,
                                   ChangeListener {
    static final int FPS_MIN = 1;
    static final int FPS_MAX = 19;
    static final int FPS_INIT = 14;

    public int value = 14;
    JTextField lblZoom = new JTextField("14");
    
    public ZoomSlider() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
 
        //Create the slider.
        JSlider js = new JSlider(JSlider.HORIZONTAL,
                                              FPS_MIN, FPS_MAX, FPS_INIT);
         
 
        js.addChangeListener(this);
 
        js.setMajorTickSpacing(19); // max value 
        js.setMinorTickSpacing(1); // min value
        js.setMajorTickSpacing(3); /* display the numbers by space of 3
        							  so it will be like 1, 4, 7, so on
        							  */
        js.setPaintTicks(true);  // set the paint ticks to true
        js.setPaintLabels(true); // set the paint label to true

        add(lblZoom);
        add(js);
        
    }
 
    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }
 
    //React to window events.
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
 
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
    	JSlider source = (JSlider)e.getSource();
    	if (!source.getValueIsAdjusting()) {
    		this.value = source.getValue();
    		lblZoom.setText(Integer.toString(value)); // set the textfield to the changed value
    		
    	}
    	
    }
 
    // main is just for run a demo of the class
    public static void main(String[] args) {
        //Create and set up the window.
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ZoomSlider test = new ZoomSlider();
                 
        //Add content to the window.
        frame.add(test, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
