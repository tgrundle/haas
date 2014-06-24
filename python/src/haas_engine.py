"""
Created on Nov 3, 2010

@author: timothy
"""

import pickle
import socket
import sys
import threading
import time
import traceback
from haas_logger import getLogger
from haas_players import AIPlayerMode, \
    InteractivePlayerMode
from haas_round import Round, Dealer, Player


class Engine:
    def __init__(self, view=None):
        self.view = view
        self.gameManager = None
        self.__idle = True
        
    def isIdle(self):
        return self.__idle
    
    def launch(self):
        self.__idle = None
        
        if self.gameManager is None:
            localPlayerMode = None
            if self.view:
                localPlayer = Player()
                localPlayerMode = InteractivePlayerMode(localPlayer, self.view)
                if "--name" in sys.argv:
                    try:
                        localPlayerMode.player.name = sys.argv[sys.argv.index("--name") + 1]
                        if localPlayerMode.player.name[0:2] == "--":
                            localPlayerMode.player.name = None
                    except:
                        pass
        
                if localPlayerMode.player.name is None:
                    localPlayerMode.player.name = self.view.greet() 
                
                if "--single" in sys.argv:
                    getLogger().log("Starting new game single player")
                    gameType = "1"
                elif "--join" in sys.argv:
                    getLogger().log("Joining exiting game")
                    gameType = "j"
                else:
                    gameType = self.view.getGameType()
            else:
                gameType = "s"
                
            if gameType == "s":
                self.gameManager = NetworkGameManager()
            elif(gameType == "1"):
                self.gameManager = LocalGameManager()
            elif(gameType == "j"):
                try:
                    HOST = sys.argv[sys.argv.index("--host") + 1]
                except:
                    HOST = "localhost"
        
                PORT = 50007              # The same port as used by the server
                self.gameManager = RemoteGameManager(HOST, PORT)
            else:
                raise Exception("GameType not supported!")
    
            if localPlayerMode:
                self.gameManager.addPlayer(localPlayerMode)
        else:
            self.gameManager.reset()
            
        try:
            self.gameManager.launchGame()
            self.__idle = True
        except:
            traceback.print_exc()
            pass
                
    def shutdown(self):
        if self.gameManager:
            self.gameManager.shutdown()

class Game:
    def __init__(self):
        self.gameRound = None
    
    def __haveWinner(self, bidder, playersAtTable):
        gameWinner = None
        
        if bidder.score >= 64:
            gameWinner = bidder
        else:
            for playerMode in playersAtTable.values():
                if not(gameWinner) and playerMode.player.score >= 64:
                    gameWinner = playerMode.player
                elif gameWinner and playerMode.player.score > gameWinner.score:
                    gameWinner = playerMode.player
        
        return gameWinner
    
    def play(self, intialDealer, playersAtTable):
        gameWinner = None
        dealer = Dealer(intialDealer)
        
        while not(gameWinner):
            self.gameRound = Round(dealer, playersAtTable)
            self.gameRound.playRound()
            for playerMode in playersAtTable.values():
                for playerMode2 in playersAtTable.values():
                    playerMode.announceScore(playerMode2.player)
            gameWinner = self.__haveWinner(self.gameRound.bid.bidder, playersAtTable)
            dealer = dealer.nextDealer()

        getLogger().log("Found game winner: " + gameWinner.name)        
        for playerMode in playersAtTable.values():    
            playerMode.announceGameWinner(gameWinner)
           
class GameManager:
    def __init__(self):
        self.playerTable = {}
        self.gameOwner = None
        self.game = None
    def waitForPlayers(self):
        pass    
    def __addAIPlayers(self):
        while len(self.playerTable.keys()) != 5:
            aiPlayer = Player()
        
            if not("Anna" in self.playerTable.keys()):
                aiPlayer.name = "Anna"
            elif not("TJ" in self.playerTable.keys()):
                aiPlayer.name = "TJ"
            elif not("Timothy" in self.playerTable.keys()):
                aiPlayer.name = "Timothy"
            elif not("Kathryn" in self.playerTable.keys()):
                aiPlayer.name = "Kathryn"
            elif not("Granville" in self.playerTable.keys()):
                aiPlayer.name = "Granville"
            
            self.addPlayer(AIPlayerMode(aiPlayer))
    def addPlayer(self, newPlayerMode):
        self.playerTable[newPlayerMode.player.name] = newPlayerMode

        if self.gameOwner is None:
            self.gameOwner = newPlayerMode.player
        else:
            player = self.gameOwner
            while not(player.toTheLeft is None):
                player = player.toTheLeft
    
            player.toTheLeft = newPlayerMode.player
            newPlayerMode.player.toTheRight = player

            getLogger().log(newPlayerMode.player.name + " is sitting to the left of " + player.name)

            if len(self.playerTable) == 5:
                newPlayerMode.player.toTheLeft = self.gameOwner
                self.gameOwner.toTheRight = newPlayerMode.player
                getLogger().log(self.gameOwner.name + " is sitting to the left of " + newPlayerMode.player.name)

            while not(player is None) and player != newPlayerMode.player:
                self.playerTable[player.name].sitPlayer(newPlayerMode.player.name)
                player = player.toTheRight
        newPlayerMode.joinTable()            
    def launchGame(self):
        if len(self.playerTable) < 5:
            self.waitForPlayers()
            self.__addAIPlayers()
          
        self.game = Game()  
        self.game.play(self.gameOwner, self.playerTable)
        self.gameOwner = self.gameOwner.toTheLeft
    def reset(self):
        for playerMode in self.playerTable.values():
            playerMode.player.score = 0
            for playerMode2 in self.playerTable.values():
                playerMode2.announceScore(playerMode.player)
    def shutdown(self):
        for playerMode in self.playerTable.values():
            playerMode.leaveTable()
class LocalGameManager(GameManager):
    def __init__(self):
        GameManager.__init__(self)
        
class NetworkGameManager(GameManager):
    def __init__(self):
        GameManager.__init__(self)
    def waitForPlayers(self):
        getLogger().log("Waiting For Players....")
        coord = RemotePlayerCoordinator(self)
        coord.start()
        try:
            while not(self.gameOwner):
                time.sleep(.5)
            self.playerTable[self.gameOwner.name].announceMessage("Waiting for additional players....")
            while not(self.playerTable[self.gameOwner.name].readyToBegin()):
                time.sleep(1)
        finally:
            coord.stop()
class RemoteGameManager(GameManager):
    def __init__(self, host, port):
        GameManager.__init__(self)
        self.playerMode = None
        self.host = host
        self.port = port
        self.data = b''
        self.isActive = True
        
    def addPlayer(self, newPlayerMode):
        self.playerMode = newPlayerMode
        self.conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.conn.connect((self.host, self.port))
        self.conn.send(self.playerMode.player.name.encode())
        self.conn.setblocking(False)
        
    def launchGame(self):
        
        commandReceived = True
        while self.data.find(b'\r\n') == -1:
            try:
                self.data += self.conn.recv(1024)
            except socket.error as e:
                if e.errno == 11:
                    commandReceived = False
                    break
                else:
                    #silently return, otherwise exception will cause havoc
                    return

        if commandReceived is True:
            #print ("DEBUG: " + str(data))
            if not(self.data.endswith(b'\r\n')):
                nextCommandIndex=self.data.rfind(b'\r\n')
                nextCommand = self.data[nextCommandIndex+len(b'\r\n'):]
                commandList = self.data[0:nextCommandIndex].split(b'\r\n')
            else:
                commandList = self.data.split(b'\r\n')
                nextCommand = b''
            self.data = nextCommand        
            for command in commandList:
                #print ("DEBUG: " + str(command))
                if command.find(b'::') != -1:
                    commandItemList = command.split(b'::')
                    attrib = getattr(self.playerMode, commandItemList[0].decode("UTF-8"))
                    func = getattr(attrib, commandItemList[1].decode("UTF-8"))
                    
    
                    if len(commandItemList) == 2:
                        response = func()
                    else:
                        argumentList = commandItemList[2:]
                        args = []
                        for arg in argumentList:
                            args.append(pickle.loads(arg))
                        response = func(*args)
                    
                    if self.conn and not (response == None):
                        self.conn.send(pickle.dumps(response))
                    
        self.playerMode.view.root.after(100, self.launchGame)
    def shutdown(self):
        self.conn.close()
        self.conn = None
class ProxyView():        
    def __init__(self, conn):
        self.conn = conn
    def shutdown(self):
        getLogger().log("Closing Connection!")
        self.conn.close()
    def announceMessage(self, message):
        pmessage = pickle.dumps(message)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceMessage')
        self.conn.send(b'::')
        self.conn.send(pmessage)
        self.conn.send(b'\r\n')
    def announceTrickWinner(self, playerName):
        pplayerName = pickle.dumps(playerName)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceTrickWinner')
        self.conn.send(b'::')
        self.conn.send(pplayerName)
        self.conn.send(b'\r\n')
    def joinTable(self, player):
        pplayer = pickle.dumps(player)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'joinTable')
        self.conn.send(b'::')
        self.conn.send(pplayer)
        self.conn.send(b'\r\n')
    def showDealtCards(self):
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'showDealtCards')
        self.conn.send(b'\r\n')
    def showHand(self, hand):
        phand = pickle.dumps(hand)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'showHand')
        self.conn.send(b'::')
        self.conn.send(phand)
        self.conn.send(b'\r\n')
    def showKiddie(self, kiddie):
        pkiddie = pickle.dumps(kiddie)        
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'showKiddie')
        self.conn.send(b'::')
        self.conn.send(pkiddie)
        self.conn.send(b'\r\n')
    def sitPlayer(self, playerName):
        pplayerName = pickle.dumps(playerName)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'sitPlayer')
        self.conn.send(b'::')
        self.conn.send(pplayerName)
        self.conn.send(b'\r\n')
    def addKiddieToHand(self, kiddie):
        pkiddie = pickle.dumps(kiddie)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'addKiddieToHand')
        self.conn.send(b'::')
        self.conn.send(pkiddie)
        self.conn.send(b'\r\n')
    def announceBid(self, bid):
        pbid = pickle.dumps(bid)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceBid')
        self.conn.send(b'::')
        self.conn.send(pbid)
        self.conn.send(b'\r\n')
    def announceBidder(self, bid):
        pbid = pickle.dumps(bid)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceBidder')
        self.conn.send(b'::')
        self.conn.send(pbid)
        self.conn.send(b'\r\n')
    def announceGameWinner(self, gameWinner):
        pgameWinner = pickle.dumps(gameWinner)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceGameWinner')
        self.conn.send(b'::')
        self.conn.send(pgameWinner)
        self.conn.send(b'\r\n')
    def announcePartners(self, bidder, partner):
        pbidder = pickle.dumps(bidder)
        ppartner = pickle.dumps(partner)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announcePartners')
        self.conn.send(b'::')
        self.conn.send(pbidder)
        self.conn.send(b'::')
        self.conn.send(ppartner)
        self.conn.send(b'\r\n')
    def announcePartnerCard(self, partnerCard):
        ppartnerCard = pickle.dumps(partnerCard)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announcePartnerCard')
        self.conn.send(b'::')
        self.conn.send(ppartnerCard)
        self.conn.send(b'\r\n')
    def announceRoundResult(self, message):
        pmessage = pickle.dumps(message)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceRoundResult')
        self.conn.send(b'::')
        self.conn.send(pmessage)
        self.conn.send(b'\r\n')
    def announceScore(self, player):
        pplayer = pickle.dumps(player)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceScore')
        self.conn.send(b'::')
        self.conn.send(pplayer)
        self.conn.send(b'\r\n')
    def announceTrump(self, trump):
        ptrump = pickle.dumps(trump)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'announceTrump')
        self.conn.send(b'::')
        self.conn.send(ptrump)
        self.conn.send(b'\r\n')
    def getBid(self, validBids, minBid):
        pvalidBids = pickle.dumps(validBids)
        pminBid = pickle.dumps(minBid)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'getBid')
        self.conn.send(b'::')
        self.conn.send(pvalidBids)
        self.conn.send(b'::')
        self.conn.send(pminBid)
        self.conn.send(b'\r\n')
       
        ret = self.conn.recv(1024)
        return pickle.loads(ret)

        
    def getCardFromHand(self, hand, msg, possiblePlays=None):
        phand = pickle.dumps(hand)
        pmsg = pickle.dumps(msg)
        ppossiblePlays = pickle.dumps(possiblePlays)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'getCardFromHand')
        self.conn.send(b'::')
        self.conn.send(phand)
        self.conn.send(b'::')
        self.conn.send(pmsg)
        self.conn.send(b'::')
        self.conn.send(ppossiblePlays)
        self.conn.send(b'\r\n')
        
        ret = self.conn.recv(1024)
        return pickle.loads(ret)        
    def namePartnerCard(self, validCardList):
        pvalidCardList = pickle.dumps(validCardList)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'namePartnerCard')
        self.conn.send(b'::')
        self.conn.send(pvalidCardList)
        self.conn.send(b'\r\n')
        ret = self.conn.recv(1024)
        return pickle.loads(ret)        
    def nameTrump(self):
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'nameTrump')
        self.conn.send(b'\r\n')
        ret = self.conn.recv(1024)
        return pickle.loads(ret)        
    def showPlay(self, play):
        pplay = pickle.dumps(play)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'showPlay')
        self.conn.send(b'::')
        self.conn.send(pplay)
        self.conn.send(b'\r\n')
    def showTrickResult(self, winningPlayer, trickCount):
        pwinningPlayer = pickle.dumps(winningPlayer)
        ptrickCount = pickle.dumps(trickCount)
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'showTrickResult')
        self.conn.send(b'::')
        self.conn.send(pwinningPlayer)
        self.conn.send(b'::')
        self.conn.send(ptrickCount)
        self.conn.send(b'\r\n')
    def readyToBegin(self):
        self.conn.send(b'view')
        self.conn.send(b'::')
        self.conn.send(b'readyToBegin')
        self.conn.send(b'\r\n')
        ret = self.conn.recv(1024)
        return pickle.loads(ret)        
class RemotePlayerCoordinator(threading.Thread):
    def __init__(self, game):
        threading.Thread.__init__(self)    
        self.game = game
        self.waiting = True

    def run(self):
        
        HOST = ''                 # Symbolic name meaning all available interfaces
        PORT = 50007              # Arbitrary non-privileged port
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((HOST, PORT))
        s.listen(1)
        s.settimeout(1)

        while self.waiting:
            try:
                conn, addr = s.accept()
                conn.settimeout(None)
                remotePlayerName = conn.recv(1024).decode('UTF-8')
                getLogger().log("Received join request from " + remotePlayerName)
                newPlayer = Player()
                newPlayerMode = InteractivePlayerMode(newPlayer, ProxyView(conn))
                if remotePlayerName in self.game.playerTable.keys():
                    getLogger().log("Name already in use")
                    newPlayerMode.announceMessage("Name already in use, please enter a unique name")
                else:
                    newPlayer.name = remotePlayerName
                    self.game.addPlayer(newPlayerMode)

                if len(self.game.playerTable) == 5:
                    self.game.gameOwner.announceMessage("5 Players have joined. Press b to begin.")
                    self.stop()

            except socket.timeout:
                pass
        s.close()
               
    def stop(self):
        self.waiting = False


            
        
        
        
