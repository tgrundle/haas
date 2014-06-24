'''
Created on May 27, 2012

@author: timothy
'''

from haas_engine import GameManager, Engine
from haas_players import AIPlayerMode, InteractivePlayerMode
from haas_round import Player
from GUITkViewProxy import GUITkViewProxy

if __name__ == '__main__':

    player1 = Player()
    player1.name="A1"
    player2 = Player()
    player2.name="A2"
    player3 = Player()
    player3.name="A3"
    player4 = Player()
    player4.name="A4"
    player5 = Player()
    player5.name="A5"

    gameManager = GameManager()
    view = GUITkViewProxy(AIPlayerMode(player1), gameManager, 500)
    view.guitk.showKiddiePause = 0
    view.guitk.showPlayPause = 0
    view.guitk.showTrickPause = 0
    view.guitk.showTrickResultPause = 0
    engine = Engine(view)

    playerMode1 = InteractivePlayerMode(player1, view)
    playerMode2 = AIPlayerMode(player2)
    playerMode3 = AIPlayerMode(player3)
    playerMode4 = AIPlayerMode(player4)
    playerMode5 = AIPlayerMode(player5)
    gameManager.addPlayer(playerMode1)
    gameManager.addPlayer(playerMode2)
    gameManager.addPlayer(playerMode3)
    gameManager.addPlayer(playerMode4)
    gameManager.addPlayer(playerMode5)
    engine.gameManager = gameManager
    view.run(engine)
