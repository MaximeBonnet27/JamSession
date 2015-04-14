package interfaces.profil;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import interfaces.profil.tchat.UITchat;

public class UIProfil extends JPanel implements IProfil,ActionListener,IProfilDelegate{

	private JFrame parent;
	private IProfilDelegate delegate;
	private JMenuBar menuBar;
	private JMenu	m_profil;
	private JMenu 	m_reseaux;
	private JMenuItem mi_deconnexion;
	private JMenuItem mi_communication;

	private UITchat tchatView;
	private UIContacts contactsView;

	public UIProfil(JFrame parent){
		super();

		setLayout(new BorderLayout());

		this.parent=parent;

		menuBar=new JMenuBar();

		/*menu profil*/
		m_profil=new JMenu("Profil");
		mi_deconnexion=new JMenuItem("Deconnexion");
		mi_deconnexion.addActionListener(this);
		m_profil.add(mi_deconnexion);
		menuBar.add(m_profil);

		/*menu reseaux*/
		m_reseaux=new JMenu("Reseaux");
		mi_communication=new JMenuItem("Show communications");
		m_reseaux.add(mi_communication);
		mi_communication.addActionListener(this);
		menuBar.add(m_reseaux);

		tchatView=new UITchat();
		tchatView.setDelegate(this);
		contactsView=new UIContacts();


		add(tchatView,BorderLayout.CENTER);
		add(contactsView,BorderLayout.LINE_START);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				init(getWidth(), getHeight());
			}
		});
	}

	@Override
	public IProfil init(int width, int height) {
		parent.setJMenuBar(menuBar);

		tchatView.init(width*3/4, height);
		contactsView.init(width*1/4, height);
		return this;
	}

	public void setDelegate(IProfilDelegate delegate) {
		this.delegate=delegate;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();

		if(source.equals(mi_deconnexion)){
			deconnexion();
		}else if(source.equals(mi_communication)){
			show_communication();
		}else{

		}
	}

	@Override
	public void deconnexion() {
		if(delegate!=null)
			delegate.deconnexion();
	}

	@Override
	public void show_communication() {
		if(delegate!=null)
			delegate.show_communication();
	}

	public void reset() {
		parent.getJMenuBar().setVisible(true);
	}

	@Override
	public void sendMessage(String message) {
		if(delegate!=null)
			delegate.sendMessage(message);

	}

	@Override
	public void receiveMessage(String message, String from) {
		tchatView.receiveMessage(message, from);
	}

	@Override
	public void addContact(String name) {
		contactsView.addContact(name);
	}

}
