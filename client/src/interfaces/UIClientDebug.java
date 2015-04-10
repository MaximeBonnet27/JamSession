package client.interfaces;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIClientDebug extends JFrame {

	private JTextArea message;
	private JScrollPane scroll;
	
	public UIClientDebug(){
		super();
		setTitle("Client-Debug");
		setSize(300, 300);
		message=new JTextArea();
		message.setEditable(false);
		
		scroll=new JScrollPane(message);
		
		getContentPane().add(scroll);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true);
	}

	public void add(String communication){
		message.append(communication+"\n");
	}
}
