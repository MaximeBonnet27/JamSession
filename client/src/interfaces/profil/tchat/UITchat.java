package interfaces.profil.tchat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class UITchat extends JPanel implements ITchat, ITchatDelegate,ActionListener{

	private ITchatDelegate delegate;

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
		messageReceiver_scroll=new JScrollPane(messageReceiver);

		messageSender=new JTextArea();
		messageSender_scroll=new JScrollPane(messageSender);

		jbEnvoyer=new JButton("envoyer");
		jbEnvoyer.addActionListener(this);

		JPanel panelSender=new JPanel();
		panelSender.setLayout(new BorderLayout());

		JPanel panelJBEnvoyer=new JPanel();
		panelJBEnvoyer.setLayout(new BoxLayout(panelJBEnvoyer, BoxLayout.PAGE_AXIS));
		panelJBEnvoyer.add(Box.createGlue());
		panelJBEnvoyer.add(jbEnvoyer,BorderLayout.LINE_END);
		panelJBEnvoyer.add(Box.createGlue());

		panelSender.add(messageSender_scroll,BorderLayout.CENTER);
		panelSender.add(panelJBEnvoyer,BorderLayout.LINE_END);

		split.setTopComponent(messageReceiver_scroll);
		split.setBottomComponent(panelSender);
		add(split);
	}

	public void init(int width, int height){
		setPreferredSize(new Dimension(width, height));

		split.setPreferredSize(new Dimension(width, height));
		split.setDividerLocation(height*3/4);
		messageReceiver_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}

	@Override
	public void sendMessage(String message) {
		if(delegate!=null)
			delegate.sendMessage(message);
	}

	@Override
	public void receiveMessage(String message, String from) {
		messageReceiver.append(from+" : "+message+"\n");
	}

	public void setDelegate(ITchatDelegate delegate) {
		if(delegate!=null)
			this.delegate=delegate;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(jbEnvoyer)){
			sendMessage(messageSender.getText());
			messageSender.setText("");
		}
	}

}
