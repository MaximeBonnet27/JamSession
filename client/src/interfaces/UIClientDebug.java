package interfaces;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIClientDebug extends JFrame {

	private JTextAreaOutput messageOutput;
	private JTextArea message;
	private JScrollPane scroll;
	
	public UIClientDebug(){
		super();
		setTitle("Client-Debug");
		setSize(300, 300);
		message=new JTextArea();
		message.setEditable(false);
		messageOutput=new JTextAreaOutput(message);
		scroll=new JScrollPane(message);
		
		getContentPane().add(scroll);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//setVisible(true);
	}

	public void add(String communication){
		message.append(communication+"\n");
	}
	
	public OutputStream getOutputStream(){
		return messageOutput;
	}
	
	public class JTextAreaOutput extends OutputStream{

		private JTextArea text;
		
		public JTextAreaOutput(JTextArea text){
			this.text=text;
		}
		
		@Override
		public void write(int b) throws IOException {
			text.append(String.valueOf((char)b));
		}

		@Override
		public void flush() throws IOException {
			super.flush();
		}
	}
}
