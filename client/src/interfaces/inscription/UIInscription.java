package interfaces.inscription;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UIInscription extends JPanel implements ActionListener,IInscriptionDelegate,IInscription {

	private IInscriptionDelegate delegate;

	private JLabel jlPseudo,jlPassW,jlPassWC,jlServeur,jlPort;
	private JTextField jtfPseudo,jtfServeur,jtfPort;
	private JPasswordField jpfPassW,jpfPassWC;

	private JButton jbValider,jbAnnuler;

	public UIInscription() {
		super();
		jlPseudo=new JLabel("Pseudo");
		jtfPseudo=new JTextField();

		jlPassW=new JLabel("Password");
		jpfPassW=new JPasswordField();

		jlPassWC=new JLabel("Confirm password");
		jpfPassWC=new JPasswordField();

		jlServeur=new JLabel("@Server");
		jtfServeur=new JTextField();
		
		jlPort=new JLabel("Port");
		jtfPort=new JTextField();
		
		jbValider=new JButton("Valider");
		jbAnnuler=new JButton("Annuler");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();

		if(source.equals(jbAnnuler))
			annulerRegister();
		else if(source.equals(jbValider))
			valider();
	}

	private void valider(){
		
		jbValider.setEnabled(false);
		if(verificationEntre()){
			try{
				register(jtfPseudo.getText(), new String(jpfPassW.getPassword()),jtfServeur.getText(),jtfPort.getText());
			}catch(Exception e){
				show_error(e.getMessage());
			}
		}else{
			jbValider.setEnabled(true);
		}
	}

	private boolean verificationEntre(){
		String pass1=new String(jpfPassW.getPassword());
		String pass2=new String(jpfPassWC.getPassword());
		
		if(!pass1.equals(pass2)){
			show_error("Confirmation du mot de  passe incorrect");
			return false;
		}else
			return true;
	}
	
	public void show_error(String message){
		JOptionPane.showMessageDialog(this, message,"inscription error",JOptionPane.ERROR_MESSAGE);
	}

	/****************************IInscription**************************/

	@Override
	public IInscription init(int width, int height) {
		JPanel panelButton=new JPanel();
		Dimension dimension=new Dimension(width/2, 25);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


		jtfPseudo.setMaximumSize(dimension);
		jpfPassW.setMaximumSize(dimension);
		jpfPassWC.setMaximumSize(dimension);

		jlPseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		jlPassW.setAlignmentX(Component.CENTER_ALIGNMENT);
		jlPassWC.setAlignmentX(Component.CENTER_ALIGNMENT);
		jtfServeur.setMaximumSize(dimension);
		jtfPort.setMaximumSize(dimension);

		jtfPseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		jpfPassW.setAlignmentX(Component.CENTER_ALIGNMENT);
		jpfPassWC.setAlignmentX(Component.CENTER_ALIGNMENT);
		jtfServeur.setAlignmentX(Component.CENTER_ALIGNMENT);
		jtfPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		jbValider.addActionListener(this);
		jbAnnuler.addActionListener(this);

		this.add(Box.createVerticalGlue());


		this.add(jlPseudo);
		this.add(Box.createRigidArea(dimension));

		this.add(jtfPseudo);
		this.add(Box.createRigidArea(dimension));

		this.add(jlPassW);
		this.add(Box.createRigidArea(dimension));

		this.add(jpfPassW);
		this.add(Box.createRigidArea(dimension));

		this.add(jlPassWC);
		this.add(Box.createRigidArea(dimension));

		this.add(jpfPassWC);
		this.add(Box.createRigidArea(dimension));
		
		this.add(jlServeur);
		this.add(Box.createRigidArea(dimension));

		this.add(jtfServeur);
		this.add(Box.createRigidArea(dimension));
		
		this.add(jlPort);
		this.add(Box.createRigidArea(dimension));
		
		this.add(jtfPort);
		this.add(Box.createRigidArea(dimension));

		panelButton.add(jbAnnuler);
		panelButton.add(jbValider);
		this.add(panelButton);
		this.add(Box.createVerticalGlue());

		return this;
	}

	@Override
	public void setDelegate(IInscriptionDelegate delegate) {
		this.delegate=delegate;
	}

	@Override
	public void reset() {
		jtfPseudo.setText("");
		jpfPassW.setText("");
		jpfPassWC.setText("");
		jtfServeur.setText("localhost");
		jtfPort.setText("2015");
		jbValider.setEnabled(true);
	}


	/**************************IInscriptionDelegate********************/

	@Override
	public void register(String pseudo, String password,String addr_serveur, String port_serveur){
		if(delegate!=null){
			try {
				delegate.register(pseudo, password, addr_serveur, port_serveur);
			} catch (Exception e) {
				show_error(e.getMessage());
				jbValider.setEnabled(true);
			}
		}
	}

	@Override
	public void annulerRegister() {
		if(delegate!=null)
			delegate.annulerRegister();

	}
}
