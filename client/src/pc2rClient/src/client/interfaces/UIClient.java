package client.interfaces;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import client.interfaces.launcher.ILauncher;
import client.interfaces.launcher.UILauncher;
import client.interfaces.profil.IProfil;
import client.interfaces.profil.UIProfil;

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
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//send deconnexion
		
		this.launcher.init(this.getWidth(),this.getHeight());
		this.profil.init(this.getWidth(),this.getHeight());
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
		this.setVisible(true);
	}
	
	private void clean(){
		this.getContentPane().removeAll();
		if(getJMenuBar()!=null)
			getJMenuBar().setVisible(false);
		refresh();
	}



}
