package interfaces.profil.tchat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class UITchat extends JPanel implements ITchat, ITchatDelegate,ActionListener{

	private static final Color color_moi=Color.BLUE;
	private static final Color color_autres=Color.RED;
	private static final Color color_serveur=Color.GREEN;
	
	private ITchatDelegate delegate;

	private JSplitPane split;

	private JTextPane messageReceiver;
	private JScrollPane messageReceiver_scroll;

	private JTextArea messageSender;
	private JScrollPane messageSender_scroll;

	private JButton jbEnvoyer;

	public UITchat(){
		split=new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		messageReceiver=new JTextPane();
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
		message=message.replaceAll("\n", "\\\\n");
		
		if(delegate!=null)
			delegate.sendMessage(message);
	}

	
	
	@Override
	public void receiveMessage(String message, String from) {
		message=message.replaceAll("\\\\n", "\n");
		
		Color color=color_autres;
		if(from.equals(getName())){
			color=color_moi;
			from="moi";
		}else if(from.equals("serveur"))
			color=color_serveur;
		
		SimpleDateFormat formater=new SimpleDateFormat("HH:mm");
		Date date=new Date();
		
		append(messageReceiver,from+" ["+formater.format(date)+"] "+" : "+message+"\n",color);
	}

	
	private void append(JTextPane tp, String msg, Color c)
    {
		StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

	    int len = tp.getDocument().getLength();
	    try {
			tp.getDocument().insertString(len, msg, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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
	
	public void reset(){
		this.messageReceiver.setText("");
		this.messageSender.setText("");
	}
}
