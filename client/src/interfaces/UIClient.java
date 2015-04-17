package interfaces;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.text.StyledEditorKit.BoldAction;

import interfaces.inscription.UIInscription;
import interfaces.launcher.UILauncher;
import interfaces.profil.UIProfil;

public class UIClient extends JFrame implements IClientInterface,IClientInterfaceDelegate {
	private IClientInterfaceDelegate delegate;

	private UILauncher launcher;
	private UIProfil profil;
	private UIInscription inscription;
	
	private JProgressBar progressbar;
	
	private Boolean initialized;

	public UIClient(){
		super();
		this.launcher=new UILauncher();
		this.launcher.setDelegate(this);
		
		this.profil=new UIProfil(this);
		this.profil.setDelegate(this);
		
		this.inscription=new UIInscription();
		this.inscription.setDelegate(this);
		
		progressbar=new JProgressBar(JProgressBar.HORIZONTAL);
		progressbar.setIndeterminate(true);
		progressbar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		initialized=false;
	}


	@Override
	public void setDelegate(IClientInterfaceDelegate delegate) {
		this.delegate=delegate;
	}

	public void setInitialized(Boolean val) {
		synchronized (this) {
			this.initialized = val;
			if(initialized)
				notifyAll();
		}
		
	}
	
	public Boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public IClientInterface init(int width, int height) {
		this.setSize(width, height);
		//this.getContentPane().setSize(this.getSize());
		this.launcher.init(this.getWidth(),this.getHeight());
		this.profil.init(this.getWidth(),this.getHeight());
		this.inscription.init(this.getWidth(),this.getHeight());
		this.progressbar.setSize(this.getWidth()/2,50);
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
			String port_serveur) throws Exception {
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
	public void showLauncher(String message) {
		clean();
		setTitle("JamSession-Launcher");
		launcher.reset();
		getContentPane().add(launcher);
		refresh();
		launcher.show_error(message);
	}

	@Override
	public void showProfil(String name) {
		clean();
		setTitle("JamSession-Profil-"+name);
		profil.reset();
		profil.setName(name);
		getContentPane().add(profil);
		refresh();
	}

	@Override
	public void showInscription() {
		clean();
		setTitle("JamSession-Inscription");
		inscription.reset();
		getContentPane().add(inscription);
		refresh();
	}

	@Override
	public void showInscription(String message) {
		clean();
		setTitle("JamSession-Inscription");
		inscription.reset();
		getContentPane().add(inscription);
		refresh();
		inscription.show_error(message);
	}
	
	@Override
	public void showConnexionEnCours() {
		clean();
		setTitle("JamSession-Connexion");
		JPanel panel= new JPanel();
		panel.setSize(getSize());
		panel.add(progressbar);
		getContentPane().add(progressbar);
		//getContentPane().add(panel);
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
		setInitialized(true);
	}
	
	private void clean(){
		setInitialized(false);
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
	public void removeContact(String name) {
	 profil.removeContact(name); 
	}
	
	@Override
	public void addContact(String name) {
		profil.addContact(name);
	}


	@Override
	public void creerCompte() {
		if(delegate!=null)
			delegate.creerCompte();
		
	}


	@Override
	public void register(String pseudo, String password,String addr_serveur,String port_serveur) throws Exception {
		if(delegate!=null)
			delegate.register(pseudo, password, addr_serveur, port_serveur);
		
	}


	@Override
	public void annulerRegister() {
		if(delegate!=null)
			delegate.annulerRegister();
		
	}


	/* (non-Javadoc)
	 * @see interfaces.launcher.ILaucherDelegate#login(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void login(String pseudo, String addr_serveur, String port_serveur,
			String password) throws Exception {
		if(delegate!=null)
			delegate.login(pseudo, addr_serveur, port_serveur, password);
		
	}



}
