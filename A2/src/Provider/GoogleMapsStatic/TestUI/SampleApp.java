/* 
 * Liang Xue
 */

package Provider.GoogleMapsStatic.TestUI;

import Provider.GoogleMapsStatic.*;
import Task.*;
import Task.Manager.*;
import Task.ProgressMonitor.*;
import Task.Support.CoreSupport.*;
import Task.Support.GUISupport.*;
import com.jgoodies.forms.factories.*;
import info.clearthought.layout.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import javax.accessibility.Accessible;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.io.Serializable;
import java.text.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import Provider.GoogleMapsStatic.*;

/** @author nazmul idris */
public class SampleApp extends JFrame {
	

/** reference to task */
private SimpleTask _task;
/** this might be null. holds the image to display in a popup */
private BufferedImage _img;
/** this might be null. holds the text in case image doesn't display */
private String _respStr;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// main method...
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static void main(String[] args) {
  Utils.createInEDT(SampleApp.class);
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// constructor
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

private void doInit() {
  GUIUtils.setAppIcon(this, "burn.png");
  GUIUtils.centerOnScreen(this);
  setVisible(true);

  int W = 28, H = W;
  boolean blur = false;
  float alpha = .7f;

  try {
    btnGetMap.setIcon(ImageUtils.loadScaledBufferedIcon("ok1.png", W, H, blur, alpha));
    btnQuit.setIcon(ImageUtils.loadScaledBufferedIcon("charging.png", W, H, blur, alpha));
    // TODO
    /*
    Icon icon = new ImageIcon("C:/Users/Liang/git/jac444a_2/A2/54.ico");
    SavedAddr._btnSave.setIcon(icon);
    */
    SavedAddr._btnSave.setIcon(ImageUtils.loadScaledBufferedIcon("71.png", W, H, blur, alpha));
    SavedAddr._btnClear.setIcon(ImageUtils.loadScaledBufferedIcon("69.png", W, H, blur, alpha));
  }	
  catch (Exception e) {
    System.out.println(e);
  }

  _setupTask();
}

/** create a test task and wire it up with a task handler that dumps output to the textarea */
@SuppressWarnings("unchecked")
private void _setupTask() {

  TaskExecutorIF<ByteBuffer> functor = new TaskExecutorAdapter<ByteBuffer>() {
    public ByteBuffer doInBackground(Future<ByteBuffer> swingWorker,
                                     SwingUIHookAdapter hook) throws Exception
    {

      _initHook(hook);

      // set the license key
      MapLookup.setLicenseKey(ttfLicense.getText());
      // get the uri for the static map
      // TODO
      String Addr = "";
      String [] sep = {SavedAddr._ttfStreet.getText(), 
    		  		   SavedAddr._ttfCity.getText(), 
    		  		   SavedAddr._ttfProvince.getText(), 
    		  		   SavedAddr._ttfCountry.getText(), 
    		  		   SavedAddr._ttfPostCode.getText()};
		int Comma = -1;
		for ( int i = 0; i < 5; i++ ) {
			if (!sep[i].equals(""))
				Comma ++;
		}
		
		for ( int i = 0; i < 5; i++ ) {
			if ( !sep[i].equals("") ) {
				Addr += sep[i].replaceAll("\\s+", "+");
				if ( Comma > 0) {
					Addr += ",";
					Comma --;
				}
			}
		}
      String uri = MapLookup.getMap((Addr),
                                    Integer.parseInt(ttfSizeW.getValue()),
                                    Integer.parseInt(ttfSizeH.getValue()),
                                    Integer.parseInt(Integer.toString(jsZoom.value))
      );
      sout("Google Maps URI=" + uri);

      // get the map from Google
      GetMethod get = new GetMethod(uri);
      new HttpClient().executeMethod(get);

      ByteBuffer data = HttpUtils.getMonitoredResponse(hook, get);

      try {
        _img = ImageUtils.toCompatibleImage(ImageIO.read(data.getInputStream()));
        sout("converted downloaded data to image...");
      }
      catch (Exception e) {
        _img = null;
        sout("The URI is not an image. Data is downloaded, can't display it as an image.");
        _respStr = new String(data.getBytes());
      }

      return data;
    }

    @Override public String getName() {
      return _task.getName();
    }
  };

  _task = new SimpleTask(
      new TaskManager(),
      functor,
      "HTTP GET Task",
      "Download an image from a URL",
      AutoShutdownSignals.Daemon
  );

  _task.addStatusListener(new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      sout(":: task status change - " + ProgressMonitorUtils.parseStatusMessageFrom(evt));
      lblProgressStatus.setText(ProgressMonitorUtils.parseStatusMessageFrom(evt));
    }
  });

  _task.setTaskHandler(new
      SimpleTaskHandler<ByteBuffer>() {
        @Override public void beforeStart(AbstractTask task) {
          sout(":: taskHandler - beforeStart");
        }
        @Override public void started(AbstractTask task) {
          sout(":: taskHandler - started ");
        }
        /** {@link SampleApp#_initHook} adds the task status listener, which is removed here */
        @Override public void stopped(long time, AbstractTask task) {
          sout(":: taskHandler [" + task.getName() + "]- stopped");
          sout(":: time = " + time / 1000f + "sec");
          task.getUIHook().clearAllStatusListeners();
        }
        @Override public void interrupted(Throwable e, AbstractTask task) {
          sout(":: taskHandler [" + task.getName() + "]- interrupted - " + e.toString());
        }
        @Override public void ok(ByteBuffer value, long time, AbstractTask task) {
          sout(":: taskHandler [" + task.getName() + "]- ok - size=" + (value == null
              ? "null"
              : value.toString()));
          if (_img != null) {
            _displayImgInFrame();
          }
          else _displayRespStrInFrame();

        }
        @Override public void error(Throwable e, long time, AbstractTask task) {
          sout(":: taskHandler [" + task.getName() + "]- error - " + e.toString());
        }
        @Override public void cancelled(long time, AbstractTask task) {
          sout(" :: taskHandler [" + task.getName() + "]- cancelled");
        }
      }
  );
}

private SwingUIHookAdapter _initHook(SwingUIHookAdapter hook) {
  hook.enableRecieveStatusNotification(checkboxRecvStatus.isSelected());
  hook.enableSendStatusNotification(checkboxSendStatus.isSelected());

  hook.setProgressMessage(ttfProgressMsg.getText());

  PropertyChangeListener listener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      SwingUIHookAdapter.PropertyList type = ProgressMonitorUtils.parseTypeFrom(evt);
      int progress = ProgressMonitorUtils.parsePercentFrom(evt);
      String msg = ProgressMonitorUtils.parseMessageFrom(evt);

      progressBar.setValue(progress);
      progressBar.setString(type.toString());

      sout(msg);
    }
  };

  hook.addRecieveStatusListener(listener);
  hook.addSendStatusListener(listener);
  hook.addUnderlyingIOStreamInterruptedOrClosed(new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      sout(evt.getPropertyName() + " fired!!!");
    }
  });

  return hook;
}

private void _displayImgInFrame() {

  final JFrame frame = new JFrame("Google Static Map");
  GUIUtils.setAppIcon(frame, "71.png");
  frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

  JLabel imgLbl = new JLabel(new ImageIcon(_img));
  imgLbl.setToolTipText(MessageFormat.format("<html>Image downloaded from URI<br>size: w={0}, h={1}</html>",
                                             _img.getWidth(), _img.getHeight()));
  imgLbl.addMouseListener(new MouseListener() {
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) { frame.dispose();}
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
  });

  frame.setContentPane(imgLbl);
  frame.pack();

  GUIUtils.centerOnScreen(frame);
  frame.setVisible(true);
}

private void _displayRespStrInFrame() {

  final JFrame frame = new JFrame("Google Static Map - Error");
  GUIUtils.setAppIcon(frame, "69.png");
  frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

  JTextArea response = new JTextArea(_respStr, 25, 80);
  response.addMouseListener(new MouseListener() {
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) { frame.dispose();}
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
  });

  frame.setContentPane(new JScrollPane(response));
  frame.pack();

  GUIUtils.centerOnScreen(frame);
  frame.setVisible(true);
}

/** simply dump status info to the textarea */
private void sout(final String s) {
  Runnable soutRunner = new Runnable() {
    public void run() {
      if (ttaStatus.getText().equals("")) {
        ttaStatus.setText(s);
      }
      else {
        ttaStatus.setText(ttaStatus.getText() + "\n" + s);
      }
    }
  };

  if (ThreadUtils.isInEDT()) {
    soutRunner.run();
  }
  else {
    SwingUtilities.invokeLater(soutRunner);
  }
}

private void startTaskAction() {
  try {
    _task.execute();
  }
  catch (TaskException e) {
    sout(e.getMessage());
  }
}


public SampleApp() {
  initComponents();
  doInit();
}

private void quitProgram() {
  _task.shutdown();
  System.exit(0);
}

private void initComponents() {
  // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
  // Generated using JFormDesigner non-commercial license
  dialogPane = new JPanel();
  contentPanel = new JPanel();
  panel1 = new JPanel();
  label2 = new JLabel();
  ttfSizeW = new Spinner();
  label4 = new JLabel();
  ttfLat = new JTextField();
  btnGetMap = new JButton();
  label3 = new JLabel();
  ttfSizeH = new Spinner();
  label5 = new JLabel();
  ttfLon = new JTextField();
  btnQuit = new JButton();
  label1 = new JLabel();
  ttfLicense = new JTextField();
  label6 = new JLabel();
  ttfZoom = new JTextField();
  scrollPane1 = new JScrollPane();
  ttaStatus = new JTextArea();
  panel2 = new JPanel();
  panel3 = new JPanel();
  checkboxRecvStatus = new JCheckBox();
  checkboxSendStatus = new JCheckBox();
  ttfProgressMsg = new JTextField();
  progressBar = new JProgressBar();
  lblProgressStatus = new JLabel();
  
  
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

  
  // TODO
  //======== my declare ========
  jsZoom = new ZoomSlider();
  savePos = new JButton();
  SavedAddr = new AddressDropList();
  
  //======== this ========
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  setTitle("Google Static Maps");
  setIconImage(null);
  Container contentPane = getContentPane();
  contentPane.setLayout(new BorderLayout());

  //======== dialogPane ========
  {
  	dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
  	dialogPane.setOpaque(false);
  	dialogPane.setLayout(new BorderLayout());

  	//======== contentPanel ========
  	{
  		contentPanel.setOpaque(false);
  		contentPanel.setLayout(new TableLayout(new double[][] {
  			{TableLayout.FILL},
  			{TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED}}));
  		((TableLayout)contentPanel.getLayout()).setHGap(5);
  		((TableLayout)contentPanel.getLayout()).setVGap(5);

  		//======== panel1 ========
  		{
  			// TODO
  			panel1.setOpaque(false);
  			panel1.setBorder(new CompoundBorder(
  				new TitledBorder("Configure the inputs to Google Static Maps"),
  				Borders.DLU2_BORDER));
  			panel1.setLayout(new TableLayout(new double[][] {
  				{0.14, 0.13, 0.10, 0.14, 0.12, 0.19, TableLayout.FILL},// decide the rows
  				{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));// decide columns
  			((TableLayout)panel1.getLayout()).setHGap(5);
  			((TableLayout)panel1.getLayout()).setVGap(5);

  			//---- label2 ----
  			label2.setText("Size Width");
  			label2.setHorizontalAlignment(SwingConstants.RIGHT);
  			panel1.add(label2, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- ttfSizeW ----
  			ttfSizeW = new Spinner();
  			panel1.add(ttfSizeW, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- btnGetMap ----
  			btnGetMap.setText("Get Map");
  			btnGetMap.setHorizontalAlignment(SwingConstants.LEFT);
  			btnGetMap.setMnemonic('G');
  			btnGetMap.addActionListener(new ActionListener() {
  				public void actionPerformed(ActionEvent e) {
  					startTaskAction();
  				}
  			});
  			panel1.add(btnGetMap, new TableLayoutConstraints(5, 4, 5, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- label3 ----
  			label3.setText("Size Height");
  			label3.setHorizontalAlignment(SwingConstants.RIGHT);
  			panel1.add(label3, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- ttfSizeH ----
  			ttfSizeH = new Spinner();
  			panel1.add(ttfSizeH, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

 			//---- btnQuit ----
  			btnQuit.setText("Quit");
  			btnQuit.setMnemonic('Q');
  			btnQuit.setHorizontalAlignment(SwingConstants.LEFT);
  			btnQuit.setHorizontalTextPosition(SwingConstants.RIGHT);
  			btnQuit.addActionListener(new ActionListener() {
  				public void actionPerformed(ActionEvent e) {
  					quitProgram();
  				}
  			});
  			panel1.add(btnQuit, new TableLayoutConstraints(6, 4, 6, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- label1 ----
  			label1.setText("License Key");
  			label1.setHorizontalAlignment(SwingConstants.RIGHT);
  			panel1.add(label1, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- ttfLicense ----
  			ttfLicense.setToolTipText("Enter your own URI for a file to download in the background");
  			panel1.add(ttfLicense, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- label6 ----
  			label6.setText("Zoom");
  			label6.setHorizontalAlignment(SwingConstants.RIGHT);
  			panel1.add(label6, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			// TODO 
  			//---- Zoom Slider ----
  			panel1.add(jsZoom, new TableLayoutConstraints(1, 4, 3, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			/*
  			//---- FavAddr ----
  			panel1.add(SavedAddr, new TableLayoutConstraints(2, 0, 6, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
			*/
  			panel1.add(SavedAddr._lblStreet, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._ttfStreet, new TableLayoutConstraints(3, 0, 6, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._lblCity, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._ttfCity, new TableLayoutConstraints(3, 1, 3 ,1 ,TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._lblProvince, new TableLayoutConstraints(4, 1, 4, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._ttfProvince, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._lblCountry, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._ttfCountry, new TableLayoutConstraints(3, 2, 3, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._lblPostCode, new TableLayoutConstraints(4, 2, 4, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._ttfPostCode, new TableLayoutConstraints(5, 2, 5, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._lblSave, new TableLayoutConstraints(2, 3, 2, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._dropList, new TableLayoutConstraints(3, 3, 6, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._btnSave, new TableLayoutConstraints(6, 1, 6, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  			panel1.add(SavedAddr._btnClear, new TableLayoutConstraints(6, 2, 6, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		}
  		contentPanel.add(panel1, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  		//======== scrollPane1 ========
  		{
  			scrollPane1.setBorder(new TitledBorder("System.out - displays all status and progress messages, etc."));
  			scrollPane1.setOpaque(false);

  			//---- ttaStatus ----
  			ttaStatus.setBorder(Borders.createEmptyBorder("1dlu, 1dlu, 1dlu, 1dlu"));
  			ttaStatus.setToolTipText("<html>Task progress updates (messages) are displayed here,<br>along with any other output generated by the Task.<html>");
  			scrollPane1.setViewportView(ttaStatus);
  		}
  		contentPanel.add(scrollPane1, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		
  		//======== scrollPane1 ========
  		{
  			scrollPane1.setBorder(new TitledBorder("System.out - displays all status and progress messages, etc."));
  			scrollPane1.setOpaque(false);

  			//---- ttaStatus ----
  			ttaStatus.setBorder(Borders.createEmptyBorder("1dlu, 1dlu, 1dlu, 1dlu"));
  			ttaStatus.setToolTipText("<html>Task progress updates (messages) are displayed here,<br>along with any other output generated by the Task.<html>");
  			scrollPane1.setViewportView(ttaStatus);
  		}
  		contentPanel.add(scrollPane1, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		//======== panel2 ========
  		{
  			panel2.setOpaque(false);
  			panel2.setBorder(new CompoundBorder(
  				new TitledBorder("Status - control progress reporting"),
  				Borders.DLU2_BORDER));
  			panel2.setLayout(new TableLayout(new double[][] {
  				{0.45, TableLayout.FILL, 0.45},
  				{TableLayout.PREFERRED, TableLayout.PREFERRED}}));
  			((TableLayout)panel2.getLayout()).setHGap(5);
  			((TableLayout)panel2.getLayout()).setVGap(5);

  			//======== panel3 ========
  			{
  				panel3.setOpaque(false);
  				panel3.setLayout(new GridLayout(1, 2));

  				//---- checkboxRecvStatus ----
  				checkboxRecvStatus.setText("Enable \"Recieve\"");
  				checkboxRecvStatus.setOpaque(false);
  				checkboxRecvStatus.setToolTipText("Task will fire \"send\" status updates");
  				checkboxRecvStatus.setSelected(true);
  				panel3.add(checkboxRecvStatus);

  				//---- checkboxSendStatus ----
  				checkboxSendStatus.setText("Enable \"Send\"");
  				checkboxSendStatus.setOpaque(false);
  				checkboxSendStatus.setToolTipText("Task will fire \"recieve\" status updates");
  				panel3.add(checkboxSendStatus);
  			}
  			panel2.add(panel3, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- ttfProgressMsg ----
  			ttfProgressMsg.setText("Loading map from Google Static Maps");
  			ttfProgressMsg.setToolTipText("Set the task progress message here");
  			panel2.add(ttfProgressMsg, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- progressBar ----
  			progressBar.setStringPainted(true);
  			progressBar.setString("progress %");
  			progressBar.setToolTipText("% progress is displayed here");
  			panel2.add(progressBar, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

  			//---- lblProgressStatus ----
  			lblProgressStatus.setText("task status listener");
  			lblProgressStatus.setHorizontalTextPosition(SwingConstants.LEFT);
  			lblProgressStatus.setHorizontalAlignment(SwingConstants.LEFT);
  			lblProgressStatus.setToolTipText("Task status messages are displayed here when the task runs");
  			panel2.add(lblProgressStatus, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  		}
  		contentPanel.add(panel2, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
  	}
  	dialogPane.add(contentPanel, BorderLayout.CENTER);
  }
  contentPane.add(dialogPane, BorderLayout.CENTER);
  setSize(675, 485);
  setLocationRelativeTo(null);
  // JFormDesigner - End of component initialization  //GEN-END:initComponents
}

// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
// Generated using JFormDesigner non-commercial license
private JPanel dialogPane;
private JPanel contentPanel;
private JPanel panel1;
private JLabel label2;
private Spinner ttfSizeW;
private JLabel label4;
private JTextField ttfLat;
private JButton btnGetMap;
private JLabel label3;
private Spinner ttfSizeH;
private JLabel label5;
private JTextField ttfLon;
private JButton btnQuit;
private JLabel label1;
private JTextField ttfLicense;
private JLabel label6;
private JTextField ttfZoom;
private JScrollPane scrollPane1;
private JTextArea ttaStatus;
private JPanel panel2;
private JPanel panel3;
private JCheckBox checkboxRecvStatus;
private JCheckBox checkboxSendStatus;
private JTextField ttfProgressMsg;
private JProgressBar progressBar;
private JLabel lblProgressStatus;


// TODO 
//======== My declare ========
private ZoomSlider jsZoom; 
private JButton savePos;
private AddressDropList SavedAddr;
// JFormDesigner - End of variables declaration  //GEN-END:variables



}
