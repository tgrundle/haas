package com.rundle.haas.swing;

import java.awt.Color;

import javax.swing.*;

import com.rundle.haas.GameEngine;
import com.rundle.haas.GameEventQueueManager;
import com.rundle.haas.PlayerHandlerLocalInteractive;
import com.rundle.haas.PlayerRegistry;
import com.rundle.haas.Table;
import com.rundle.haas.events.AnnounceBid;
import com.rundle.haas.events.AnnounceBidder;
import com.rundle.haas.events.AnnounceDealtCard;
import com.rundle.haas.events.AnnounceKiddieDiscard;
import com.rundle.haas.events.AnnouncePartner;
import com.rundle.haas.events.AnnouncePartnerCard;
import com.rundle.haas.events.AnnouncePlay;
import com.rundle.haas.events.AnnouncePlayerSeated;
import com.rundle.haas.events.AnnounceRoundResult;
import com.rundle.haas.events.AnnounceTrickWinner;
import com.rundle.haas.events.AnnounceTrump;
import com.rundle.haas.events.AnnounceTurn;
import com.rundle.haas.events.AnnounceWinningBid;
import com.rundle.haas.events.GameEventHandler;
import com.rundle.haas.events.ShowKiddie;

public class HaasSwing implements GameEventHandler {

	private static final String NAME =  "Timothy";
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void onCreate() {
        //Create and set up the window.
        JFrame frame = new JFrame("Yost/Rundle Haas");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 114, 53));
        

        frame.getContentPane().add(panel);

        //Display the window.
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(0,0,1024, 768);
        frame.setVisible(true);
        
        GameEngine gameEngine = new GameEngine();
		PlayerHandlerLocalInteractive phi = PlayerRegistry.getInstance().registerLocalPlayer(Table.getInstance(), NAME);
		GameEventQueueManager eventListener = new GameEventQueueManager(new HaasSwing(), phi);
		eventListener.resume();
		gameEngine.start();
        
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> (new HaasSwing()).onCreate());
    }

	@Override
	public void onAnnounceBid(AnnounceBid event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceBidder(AnnounceBidder event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceDealtCard(AnnounceDealtCard event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceKiddieDiscard(AnnounceKiddieDiscard event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnouncePartner(AnnouncePartner event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnouncePartnerCard(AnnouncePartnerCard event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnouncePlay(AnnouncePlay event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnouncePlayerSeated(AnnouncePlayerSeated event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceTrickWinner(AnnounceTrickWinner event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceTrump(AnnounceTrump event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceTurn(AnnounceTurn event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnnounceWinningBid(AnnounceWinningBid event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowKiddie(ShowKiddie event) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void  onAnnounceRoundResult(AnnounceRoundResult event) {

    }
}
