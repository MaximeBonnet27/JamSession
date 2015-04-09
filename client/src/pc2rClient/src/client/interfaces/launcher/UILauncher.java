package client.interfaces.launcher;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UILauncher extends JPanel implements ActionListener,ILauncher,ILaucherDelegate{

	private ILaucherDelegate delegate;
	
	private JTextField jtfPseudo,jtfServeur,jtfPort;
	
	private JButton jbConnexion;
	
	public UILauncher(){
		super();
		jtfPseudo=new JTextField();
		jtfServeur=new JTextField();
		jtfPort=new JTextField();
		jbConnexion=new JButton();
	}
	
	@Override
	public ILauncher init(int width, int height) {
		this.setSize(width, height);
		
		Dimension dimension=new Dimension(width/2, 50);
		
		jtfPseudo.setMaximumSize(dimension);
		jtfPseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		jtfServeur.setMaximumSize(dimension);
		jtfServeur.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		jtfPort.setMaximumSize(dimension);
		jtfPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		jbConnexion.setMaximumSize(dimension);
		jbConnexion.setAlignmentX(Component.CENTER_ALIGNMENT);
		jbConnexion.addActionListener(this);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(Box.createVerticalGlue());
		this.add(jtfPseudo);
		this.add(Box.createRigidArea(dimension));
		this.add(jtfServeur);
		this.add(Box.createRigidArea(dimension));
		this.add(jtfPort);
		this.add(Box.createRigidArea(dimension));
		this.add(jbConnexion);
		this.add(Box.createVerticalGlue());
		return this;
	}

	@Override
	public void setDelegate(ILaucherDelegate delegate) {
		this.delegate=delegate;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(jbConnexion)){
			connexion(jtfPseudo.getText(),jtfServeur.getText(),jtfPort.getText());
		}
	}

	@Override
	public void connexion(String pseudo, String addr_serveur,
			String port_serveur) {
		if(delegate!=null){
			delegate.connexion(jtfPseudo.getText(),jtfServeur.getText(),jtfPort.getText());
		}
	}

	@Override
	public void reset() {
		jtfPseudo.setText("Pseudo");		
		jtfServeur.setText("@serveur");
		jtfPort.setText("port");
		jbConnexion.setText("connexion");
	}

	


}
