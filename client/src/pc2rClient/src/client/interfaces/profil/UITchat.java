package client.interfaces.profil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class UITchat extends JComponent{

	private JSplitPane split;
	private JTextArea messageReceiver;
	private JScrollPane messageReceiver_scroll;
	private JTextArea messageSender;
	private JScrollPane messageSender_scroll;
	private JButton jbEnvoyer;
	
	public UITchat(){
		split=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		messageReceiver=new JTextArea();
		messageReceiver.setEditable(false);
		//messageReceiver_scroll=new JScrollPane(messageReceiver);
		//add(messageReceiver);
	}
	
	public void init(int width, int height){
		messageReceiver.setSize(width, height);
		add(messageReceiver);
	}
	
}
