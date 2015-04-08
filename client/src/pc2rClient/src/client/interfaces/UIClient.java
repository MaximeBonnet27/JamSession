package client.interfaces;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class UIClient extends JFrame implements UIClientInterface,UIClientDelegateInterface {
	private UIClientDelegateInterface delegate;
	
	private JTextArea tchatReceiver;
	private JTextArea tchatSender;
	private JSplitPane tchatSpliter;
	
	public UIClient(){
		super();
		
		tchatReceiver=new JTextArea();
		tchatSender=new JTextArea();
		tchatSpliter=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	}

	@Override
	public void initWithDimension(int width, int height) {
		this.setSize(width, height);
		this.setTitle("JamSession-Client");
		
		/*construction du chat*/
		tchatSpliter.setSize(width, height);
		tchatReceiver.setSize(width/2, height/2);
		tchatReceiver.setEditable(false);
		tchatSender.setSize(width/2, height/2);
		
		tchatSpliter.add(tchatReceiver);
		tchatSpliter.add(tchatSender);
		tchatSpliter.setDividerLocation(width/2);
		this.getContentPane().add(tchatSpliter);
		/**********************/
		
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void setDelegate(UIClientDelegateInterface delegate) {
		this.delegate=delegate;
	}
	
}
