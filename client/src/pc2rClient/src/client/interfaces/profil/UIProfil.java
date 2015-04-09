package client.interfaces.profil;

import java.awt.Dimension;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import client.interfaces.launcher.ILaucherDelegate;

public class UIProfil extends JPanel implements IProfil,ActionListener,IProfilDelegate{

	private JFrame parent;
	private IProfilDelegate delegate;
	private JMenuBar menuBar;
	private JMenu	m_profil;
	private JMenu 	m_reseaux;
	private JMenuItem mi_deconnexion;
	private JMenuItem mi_communication;
	
	private JSplitPane split;
	private UITchat tchatView;
	
	public UIProfil(JFrame parent){
		super();
		this.parent=parent;
		this.menuBar=new JMenuBar();
		this.m_profil=new JMenu("Profil");
		this.m_reseaux=new JMenu("Reseaux");
		this.mi_deconnexion=new JMenuItem("Deconnexion");
		this.mi_communication=new JMenuItem("Show communications");
		this.split=new JSplitPane();
		this.tchatView=new UITchat();
	}
	
	@Override
	public IProfil init(int width, int height) {
		this.setSize(width, height);
		
		m_profil.add(mi_deconnexion);
		mi_deconnexion.addActionListener(this);
		menuBar.add(m_profil);
		
		m_reseaux.add(mi_communication);
		mi_communication.addActionListener(this);
		menuBar.add(m_reseaux);
	
		parent.setJMenuBar(menuBar);
		
		tchatView.init(width, height);
		add(tchatView);
		/*Dimension dimensionMax=new Dimension(width, height);
		tchatView.setPreferredSize(dimensionMax);
		
		split.setSize(width, height);
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(width/3);
		
		JTextArea a=new JTextArea();
		a.setSize(width/2, height);
		
		JTextArea b=new JTextArea();
		b.setSize(width/2, height);
		
		split.add(a);
		split.add(b);
		
		add(split);*/
		return this;
	}

	@Override
	public void setDelegate(IProfilDelegate delegate) {
		this.delegate=delegate;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		
		if(source.equals(mi_deconnexion)){
			deconnexion();
		}else if(source.equals(mi_communication)){
			
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

	@Override
	public void reset() {
		parent.getJMenuBar().setVisible(true);
	}

}
