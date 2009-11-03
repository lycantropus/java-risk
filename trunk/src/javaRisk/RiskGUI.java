package javaRisk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class RiskGUI extends JFrame {

	private UIListener listener;
	
	private JButton endTurn = new JButton("End Turn");
	private JLabel attackRoll = new JLabel();
	private JLabel defendRoll = new JLabel();
	
	private boolean myTurn = false;
	
	private JLabel[] gridTiles;
	private JLabel[] playerTitles;
	
	private JPanel buttonPanel;
	
	private String[] names;

	public RiskGUI()
	{
		super("Risk");
		this.setLayout(new BorderLayout());
	
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					listener.surrender();
				} catch (IOException ex) {}
			}
		});
		
		JPanel mainGrid = new JPanel(new GridLayout(Constants.BOARD_SIZE,Constants.BOARD_SIZE));
		gridTiles = new JLabel[49];
		for (int i = 0 ; i < Constants.BOARD_SIZE*Constants.BOARD_SIZE ; i++)
		{
			
			gridTiles[i] = new JLabel(Integer.toString(i+1), JLabel.CENTER);
			gridTiles[i].setOpaque(true);
			//gridTiles[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
			
			final int internal_i = i;
			gridTiles[i].addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (isPlayable())
					{
						try {
							listener.clicked(internal_i);	
						} catch (IOException ex) {
							// TODO Auto-generated catch block
							// do nothing i don't care
						}
					}
				}
			});
			
			mainGrid.add(gridTiles[i]);
		}
		this.add(mainGrid, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		JPanel infoLine = new JPanel(new GridLayout(1,0));
		JLabel ar = new JLabel("Attacker Roll: ");
		JLabel dr = new JLabel("Defender Roll: ");
		
		Font f = new Font("Arial", Font.PLAIN, 24);
		
		ar.setFont(f);
		dr.setFont(f);
		attackRoll.setFont(f);
		defendRoll.setFont(f);
		
		infoLine.add(ar);
		infoLine.add(attackRoll);
		infoLine.add(dr);
		infoLine.add(defendRoll);
		
		buttonPanel = new JPanel(new GridLayout(1,0));
		
		
		
		
		endTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					listener.endTurn();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
		buttonPanel.add(endTurn);
		
		bottomPanel.add(infoLine, BorderLayout.NORTH);
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		this.add(bottomPanel, BorderLayout.SOUTH);
	
		this.setSize(800, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public boolean isPlayable()
	{
		return myTurn;
	}
	
	public void showPlayerTurn(int player) {
		for (int i = 0 ; i < playerTitles.length ; i++)
		{
			playerTitles[i].setText(names[i]);
		}
		playerTitles[player].setText(">> " + playerTitles[player].getText() + " <<");
		
	}

	public void showGamePlayable(boolean b) {
		endTurn.setEnabled(b);
		myTurn = b;
		
	}

	public void showAttack(int src, int dest) {
//		gridTiles[src].setBorder(new BevelBorder(BevelBorder.RAISED));
//		gridTiles[dest].setBorder(new LineBorder(gridTiles[src].getBackground(), 10));
//		Thread.sleep(500);
//		reset();
	}

	public void showAttackRoll(int a_roll) {
		attackRoll.setText(Integer.toString(a_roll));
	}

	public void showDefenseRoll(int d_roll) {
		defendRoll.setText(Integer.toString(d_roll));	
	}
	
	public void reset()
	{
		clearRolls();
		for (JLabel gridItem : gridTiles)
		{
			gridItem.setBorder(null);
		}
	}
	
	public void clearRolls() {
		attackRoll.setText("");
		defendRoll.setText("");
	}

	public void updateTerritory(int index, Color color, int size) {
		gridTiles[index].setBackground(color);
		gridTiles[index].setText(Integer.toString(size));
	}
	
	public void highlight(int territory){
		gridTiles[territory].setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	public void setListener(UIListener listener)
	{
		this.listener = listener;
	}
	
	public void setNames(String[] names)
	{
		
		this.names = names;
		playerTitles = new JLabel[names.length];
		
		for (int i = 0 ; i < playerTitles.length ; i++)
		{
		
			playerTitles[i] = new JLabel(names[i]);
			buttonPanel.add(playerTitles[i]);
			
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		//TESTING THE GUI
		Socket s = new Socket("localhost", 1988);
		
		RiskGUI g = new RiskGUI();
		g.setNames(new String[]{"Joe","Bob","Tom","Al","Mike","Dan"});
		ServerProxy sp = new ServerProxy(s);
		
		g.setListener(sp);
		sp.setGUI(g);
		g.setVisible(true);
		java.util.Random r = new java.util.Random();
		
		ClientModel m = new ClientModel();
		sp.setModel(m);
		
		m.setMe(0);
		
		
		for (int i = 0 ; i < 6 ; i++)
		{
			Color col = Color.getHSBColor(r.nextFloat(), 1.0f, 1.0f);
			m.fillPlayerData(i, col, "Player " + i);
		}
		
		sp.start();

		
		
		
	
	}
	
	
}