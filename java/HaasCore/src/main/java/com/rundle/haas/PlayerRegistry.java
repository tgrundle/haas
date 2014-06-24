package com.rundle.haas;

import java.util.HashMap;
import java.util.Map;

import com.rundle.haas.events.AnnouncePlayerSeated;
import com.rundle.haas.events.Announcement;



public final class PlayerRegistry {

	private static PlayerRegistry registry; 
	public static final PlayerRegistry getInstance() {
		if(registry == null) {
			registry = new PlayerRegistry();
		}
		return registry;
	}

	private final Map<String, PlayerHandler> handleRegistry = new HashMap<String, PlayerHandler>();
	private final Map<String, Integer> playerRegistry = new HashMap<String, Integer>();

	public PlayerHandler getPlayerHandler(String playerName) {
		return handleRegistry.get(playerName);
	}
	
	public Player getPlayer(String playerName) {
		return Table.getInstance().players[playerRegistry.get(playerName)];
	}
	
	public void registerPlayerHandler(Table table, String playerName, PlayerHandler handle) {
		//System.out.println("Seated: " + playerName);
		handleRegistry.put(playerName, handle);
		playerRegistry.put(playerName, table.seatedPlayers);
		Player player = new Player();
		player.name = playerName;
		table.players[table.seatedPlayers] = player;
		
		if(table.seatedPlayers == 0) {
			table.dealer = player;
		} else {
			table.players[table.seatedPlayers-1].playerToTheLeft = player;
		}
		
		if (table.seatedPlayers == 4) {
			player.playerToTheLeft = table.dealer;
		}
		makePublicAnnouncement(new AnnouncePlayerSeated(playerName, table.seatedPlayers));
		table.seatedPlayers++;
		
	}

	public void makePublicAnnouncement(Announcement announcement) {
		for(PlayerHandler handle: handleRegistry.values()) {
			if(handle instanceof PlayerHandlerLocalInteractive) {
				PlayerHandlerLocalInteractive ph = (PlayerHandlerLocalInteractive) handle;
				ph.messagesToClientQueue.offer(announcement);
				synchronized (ph.SYNC_MSG_TO_CLIENT) {
					ph.SYNC_MSG_TO_CLIENT.notify();
				}
			}
		}
	}
	
	public PlayerHandlerLocalInteractive registerLocalPlayer(Table table, String name) {
	PlayerHandlerLocalInteractive phi = (PlayerHandlerLocalInteractive) getPlayerHandler(name);
	if(phi == null) {
			phi = new PlayerHandlerLocalInteractive();
			registerPlayerHandler(table, name, phi);
	}
	return phi;
}
	
//	public static void makeRestrictedAnnouncement(Announcement publicAnnouncement, String playerName, Announcement privateAnnouncement) {
//		for(String name: handleRegistry.keySet()) {
//			if(!name.equals(playerName)) {
//				PlayerHandler handle = handleRegistry.get(name);
//				if(handle instanceof PlayerHandlerInteractive) {
//					PlayerHandlerInteractive ph = (PlayerHandlerInteractive) handle;
//					ph.messagesToClient.offer(publicAnnouncement);
//					synchronized (ph.SYNC_MSG_TO_CLIENT) {
//						ph.SYNC_MSG_TO_CLIENT.notify();
//					}
//				}
//			} else {
//				PlayerHandler handle = handleRegistry.get(name);
//				if(handle instanceof PlayerHandlerInteractive) {
//					PlayerHandlerInteractive ph = (PlayerHandlerInteractive) handle;
//					ph.messagesToClient.offer(privateAnnouncement);
//					synchronized (ph.SYNC_MSG_TO_CLIENT) {
//						ph.SYNC_MSG_TO_CLIENT.notify();
//					}
//				}
//			}
//		}
//	}
	
}
