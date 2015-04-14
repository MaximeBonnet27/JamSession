package interfaces;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import interfaces.launcher.ILauncher;
import interfaces.launcher.UILauncher;
import interfaces.profil.IProfil;
import interfaces.profil.UIProfil;

public class UIClient extends JFrame implements IClientInterface,IClientInterfaceDelegate {
	private IClientInterfaceDelegate delegate;

	private UILauncher launcher;
	private UIProfil profil;
	
	public UIClient(){
		super();
		this.launcher=new UILauncher();
		this.launcher.setDelegate(this);
		
		this.profil=new UIProfil(this);
		this.profil.setDelegate(this);
	}


	@Override
	public void setDelegate(IClientInterfaceDelegate delegate) {
		this.delegate=delegate;
	}

	@Override
	public IClientInterface init(int width, int height) {
		this.setSize(width, height);
		
		this.launcher.init(this.getWidth(),this.getHeight());
		this.profil.init(this.getWidth(),this.getHeight());
		
		this.setLocationRelativeTo(null);
		
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//send deconnexion
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(delegate!=null)
					deconnexion();
				System.exit(0);
			}
		});
		return this;
	}

	@Override
	public void connexion(String pseudo, String addr_serveur,
			String port_serveur) {
		if(delegate!=null)
			this.delegate.connexion(pseudo, addr_serveur, port_serveur);

	}

	@Override
	public void showLauncher() {
		clean();
		setTitle("JamSession-Launcher");
		launcher.reset();
		getContentPane().add(launcher);
		refresh();
	}


	@Override
	public void showProfil() {
		clean();
		setTitle("JamSession-Profil");
		profil.reset();
		getContentPane().add(profil);
		refresh();
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

	private void refresh(){
		this.getContentPane().repaint();
		pack();
		this.setVisible(true);
	}
	
	private void clean(){
		this.getContentPane().removeAll();
		if(getJMenuBar()!=null)
			getJMenuBar().setVisible(false);
		refresh();
	}


	@Override
	public void receiveMessage(String message, String from) {
		profil.receiveMessage(message, from);
		
	}


	@Override
	public void sendMessage(String message) {
		if(delegate!=null)
			delegate.sendMessage(message);
	}


	@Override
	public void addContact(String name) {
		profil.addContact(name);
	}



}
