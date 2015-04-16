package interfaces.launcher;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UILauncher extends JPanel implements ActionListener,ILauncher,ILaucherDelegate{

	private ILaucherDelegate delegate;

	private JLabel jlPseudo,jlPassW,jlServeur,jlPort;

	private JTextField jtfPseudo,jtfServeur,jtfPort;
	private JPasswordField jpfPassW;

	private JButton jbConnexion,jbcreerCompte,jbLogin;

	private boolean anonymous;

	public UILauncher(){
		super();
		jlPseudo=new JLabel("Pseudo");
		jtfPseudo=new JTextField();

		jlPassW=new JLabel("Passord");
		jpfPassW=new JPasswordField();

		jlServeur=new JLabel("@Server");
		jtfServeur=new JTextField();

		jlPort=new JLabel("Port");
		jtfPort=new JTextField();

		jbConnexion=new JButton("Se connecter");
		jbcreerCompte=new JButton("Créer un Compte");
		jbLogin=new JButton("Mode Anonyme");

		anonymous=false;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source.equals(jbConnexion)){
			//if(anonymous)
				connectionAction();
			//else
				//changeStatus();
		}else if(source.equals(jbcreerCompte)){
			creerCompte();
		}else if(source.equals(jbLogin)){
			//if(anonymous)
				changeStatus();
			//else
			//	connectionAction();
		}
			
	}


	private void changeStatus() {
		
		if(anonymous){
			jlPassW.setVisible(true);
			jpfPassW.setVisible(true);
			jbLogin.setText("Mode Anonyme");
		}else{
			jlPassW.setVisible(false);
			jpfPassW.setVisible(false);
			jbLogin.setText("Mon Compte");
		}
		anonymous=!anonymous;
		
	}

	/********************ILauncher*************************/

	@Override
	public ILauncher init(int width, int height) {
		JPanel panelButton=new JPanel();
		this.setSize(width, height);
		Dimension dimension=new Dimension(width/2, 25);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


		jtfPseudo.setMaximumSize(dimension);
		jpfPassW.setMaximumSize(dimension);
		jtfServeur.setMaximumSize(dimension);
		jtfPort.setMaximumSize(dimension);

		jlPseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		jlPassW.setAlignmentX(Component.CENTER_ALIGNMENT);
		jlServeur.setAlignmentX(Component.CENTER_ALIGNMENT);
		jlPort.setAlignmentX(Component.CENTER_ALIGNMENT);

		jtfPseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		jpfPassW.setAlignmentX(Component.CENTER_ALIGNMENT);
		jtfServeur.setAlignmentX(Component.CENTER_ALIGNMENT);
		jtfPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelButton.setAlignmentX(Component.CENTER_ALIGNMENT);


		jbConnexion.addActionListener(this);
		jbcreerCompte.addActionListener(this);
		jbLogin.addActionListener(this);


		this.add(Box.createVerticalGlue());


		this.add(jlPseudo);
		this.add(Box.createRigidArea(dimension));

		this.add(jtfPseudo);
		this.add(Box.createRigidArea(dimension));

		//if(!anonymous){
			this.add(jlPassW);
			this.add(Box.createRigidArea(dimension));

			this.add(jpfPassW);
			this.add(Box.createRigidArea(dimension));
		//}
		this.add(jlServeur);
		this.add(Box.createRigidArea(dimension));

		this.add(jtfServeur);
		this.add(Box.createRigidArea(dimension));

		this.add(jlPort);
		this.add(Box.createRigidArea(dimension));

		this.add(jtfPort);
		this.add(Box.createRigidArea(dimension));

		panelButton.add(jbConnexion);
		panelButton.add(jbcreerCompte);
		panelButton.add(jbLogin);
		this.add(panelButton);
		this.add(Box.createVerticalGlue());

		return this;
	}

	@Override
	public void setDelegate(ILaucherDelegate delegate) {
		this.delegate=delegate;
	}

	@Override
	public void reset() {
		jtfPseudo.setText("");
		jpfPassW.setText("");
		jtfServeur.setText("localhost");
		jtfPort.setText("2015");
		jbConnexion.setEnabled(true);
	}

	/*****************ILauncherDelegate**********************/

	@Override
	public void connexion(String pseudo, String addr_serveur,
			String port_serveur) {
		if(delegate!=null){
			try {
				delegate.connexion(pseudo,addr_serveur,port_serveur);
			} catch (IOException e) {
				show_error(e.getMessage());
				jbConnexion.setEnabled(true);
			}
		}
	}


	private void connectionAction(){
		jbConnexion.setEnabled(false);
		if(verificationEntre())
			connexion(jtfPseudo.getText(),jtfServeur.getText(),jtfPort.getText());
		else
			jbConnexion.setEnabled(true);
	}

	private void show_error(String message){
		JOptionPane.showMessageDialog(this, message,"connection error",JOptionPane.ERROR_MESSAGE);
	}

	private boolean verificationEntre(){
		if(jtfPseudo.getText().isEmpty()){
			show_error("pseudo vide");
			return false;
		}
		if(jtfServeur.getText().isEmpty()){
			show_error("addresse serveur vide");
			return false;
		}
		if(jtfPort.getText().isEmpty()){
			show_error("port du serveur vide");
			return false;
		}else{
			if(!anonymous){
				if(jpfPassW.getPassword().length==0){
					show_error("mot de passe vide");
					return false;
				}
			}
		}
		return true;

	}

	@Override
	public void creerCompte() {
		if(delegate!=null)
			delegate.creerCompte();

	}


}
