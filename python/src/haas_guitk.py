"""
Created on Nov 17, 2010

@author: timothy
"""
import os
import sys
import time
import tkinter
import tkinter.font

from haas_card import Deck
from haas_logger import getLogger


class ActiveHand():
    def __init__(self, tkview):
        self.cardToThrow = tkinter.StringVar()
        self.tkview = tkview

    def enable(self):
        self.cardToThrow.set(None)
        for cardImageTag in self.tkview.table.hand.keys():
            self.tkview.canvas.tag_bind(cardImageTag, "<ButtonRelease-1>", self.throwCard)

    def disable(self):
        for cardImageTag in self.tkview.table.hand.keys():
            self.tkview.canvas.tag_unbind(cardImageTag, "<ButtonRelease-1>")

    def throwCard(self, event=None):
        if event is None:
            pass
        else:
            cardImageTag = self.tkview.canvas.find_closest(event.x, event.y)[0]
            # card = self.tkview.table.hand[int(cardImageTag)]
            self.cardToThrow.set(cardImageTag)


class CardInHand():
    def __init__(self, card, x, y):
        self.card = card
        self.x = x
        self.y = y


class BidButtons():
    def __init__(self, tkview):
        self.tkview = tkview
        self.bid = tkinter.StringVar()
        self.bidDialog = None

    def show(self, validBids, showButtons):
        self.bid.set(None)
        self.validBids = validBids
        if showButtons:
            self.validBids.remove("pass")

            self.button_bid = self.tkview.canvas.create_image(self.tkview.table.bidButtonCoord,
                                                              image=self.tkview.images.button_bid, anchor=tkinter.N)
            self.button_pass = self.tkview.canvas.create_image(self.tkview.table.passButtonCoord,
                                                               image=self.tkview.images.button_pass, anchor=tkinter.N)

            self.tkview.canvas.tag_bind(self.button_bid, "<ButtonRelease-1>", self.makeBid)
            self.tkview.canvas.tag_bind(self.button_pass, "<ButtonRelease-1>", self.passBid)
        else:
            self.button_bid = None
            self.button_pass = None

    def passBid(self, event=None):
        self.tkview.canvas.delete(self.button_bid)
        self.tkview.canvas.delete(self.button_pass)
        self.bid.set("pass")

    def makeBid(self, event=None):
        # delete buttons
        if not (self.button_bid is None):
            self.tkview.canvas.delete(self.button_bid)
        if not (self.button_pass is None):
            self.tkview.canvas.delete(self.button_pass)
        self.bidDialog = GetBidDialog(self.tkview.root, self.validBids)
        self.bidDialog.show()
        if not (self.bidDialog.buttonpressed):
            getLogger().log("Root windows closed, exiting....")
            sys.exit()
        self.bid.set(self.bidDialog.bid.get())
        self.bidDialog = None


class ContinueButton():
    def __init__(self, tkview):
        self.ok = tkinter.StringVar()
        self.tkview = tkview

    def show(self):
        self.ok.set(None)
        self.continue_button = self.tkview.canvas.create_image(self.tkview.table.okButtonCoord,
                                                               image=self.tkview.images.button_ok, anchor=tkinter.N)
        self.tkview.canvas.tag_bind(self.continue_button, "<ButtonRelease-1>", self.continueGame)

    def continueGame(self, event=None):
        self.tkview.canvas.delete(self.continue_button)
        self.ok.set("OK")


class ConfirmNamePartner:
    def __init__(self, tkview):
        self.tkview = tkview
        self.action = tkinter.StringVar()

    def show(self, validCardList):
        self.cardImage = self.tkview.canvas.create_image(self.tkview.table.partnerCardCoord,
                                                         image=self.tkview.images.imageForCard(validCardList[0]),
                                                         anchor=tkinter.CENTER)
        self.confirm_button = self.tkview.canvas.create_image(self.tkview.table.okButtonCoord,
                                                              image=self.tkview.images.button_ok, anchor=tkinter.N)
        self.change_button = self.tkview.canvas.create_image(self.tkview.table.bidButtonCoord,
                                                             image=self.tkview.images.button_pass, anchor=tkinter.N)

        self.tkview.canvas.tag_bind(self.confirm_button, "<ButtonRelease-1>", self.confirm)
        self.tkview.canvas.tag_bind(self.change_button, "<ButtonRelease-1>", self.change)
        self.action.set(None)

    def confirm(self, event=None):
        self.action.set("CONFIRM")

    def change(self, event=None):
        self.action.set("CHANGE")

    def clearImages(self):
        self.tkview.canvas.delete(self.cardImage)
        self.tkview.canvas.delete(self.confirm_button)
        self.tkview.canvas.delete(self.change_button)


class GameTypeDialog(tkinter.Toplevel):
    def __init__(self, parent):
        tkinter.Toplevel.__init__(self, parent)
        self.transient(parent)
        self.resizable(tkinter.FALSE, tkinter.FALSE)
        self.title("Start or join a game?")
        self.lift(parent)
        self.okpressed = False
        self.gameType = tkinter.StringVar()
        self.gameType.set('1')
        tkinter.Radiobutton(self, text="Start", variable=self.gameType, value='1').pack(anchor=tkinter.W)
        tkinter.Radiobutton(self, text="Join", variable=self.gameType, value='j').pack(anchor=tkinter.W)
        b = tkinter.Button(self, width=5, text="OK", command=self.ok)
        b.pack()
        self.grab_set()
        self.bind("<Return>", self.ok)
        self.bind("<Escape>", self.cancel)
        self.protocol("WM_DELETE_WINDOW", self.cancel)
        # calculate position x, y
        ws = self.winfo_screenwidth()
        hs = self.winfo_screenheight()
        w = 250
        h = 75
        x = (ws / 2) - (w / 2)
        y = (hs / 2) - (h / 2)
        self.geometry('%dx%d+%d+%d' % (w, h, x, y))
        self.wait_window(self)

    def cancel(self, event=None):
        pass

    def ok(self, event=None):
        self.okpressed = True
        self.destroy()

    def getGameType(self):
        return self.gameType.get()


class GetBidDialog(tkinter.Toplevel):
    def __init__(self, parent, validBids):
        tkinter.Toplevel.__init__(self, parent)
        self.transient(parent)
        self.resizable(tkinter.FALSE, tkinter.FALSE)
        self.title("Make Bid")
        self.lift(parent)
        self.bid = tkinter.StringVar()
        self.bid.set(validBids[0])
        self.buttonpressed = False
        tkinter.Label(self, text="Choose a bid: ").pack(side=tkinter.LEFT)
        option = tkinter.OptionMenu(self, self.bid, *validBids)
        option.pack(side=tkinter.LEFT)
        b1 = tkinter.Button(self, width=5, text="Bid", command=self.ok)
        b1.pack(side=tkinter.LEFT)
        self.grab_set()
        self.bind("<Return>", self.ok)
        self.bind("<Escape>", self.cancel)
        self.protocol("WM_DELETE_WINDOW", self.cancel)
        ws = self.winfo_screenwidth()
        hs = self.winfo_screenheight()
        # calculate position x, y
        w = 250
        h = 75
        x = (ws / 2) - (w / 2)
        y = (hs / 2) - (h / 2)
        self.geometry('%dx%d+%d+%d' % (w, h, x, y))

    def show(self):
        self.wait_window(self)

    def cancel(self, event=None):
        pass

    def ok(self, event=None):
        self.buttonpressed = True
        self.destroy()

    def getBid(self):
        return self.bid.get()


class GUITkView:
    def __init__(self):
        self.root = tkinter.Tk()
        self.root.title("Yost/Rundle Haas")
        ws = self.root.winfo_screenwidth()
        hs = self.root.winfo_screenheight()
        # calculate position x, y
        w = 1024
        self.height = 768
        x = (ws / 2) - (w / 2)
        y = (hs / 2) - (self.height / 2)
        getLogger().log('%dx%d+%d+%d' % (w, self.height, x, y))
        self.root.geometry('%dx%d+%d+%d' % (w, self.height, x, y))
        self.root.wait_visibility(self.root)
        self.root.config(bg="forest green")
        self.root.resizable(False, False)
        self.canvas = tkinter.Canvas(self.root, bg="forest green", width=w)
        self.canvas.pack(side=tkinter.LEFT, expand=1, fill=tkinter.BOTH)
        self.images = ImageMgrTk()
        self.table = GUITkTable(self)
        self.engine = None
        # self.root.bind("<Control-N>", self.engine.playGame)
        self.root.bind("<Control-n>", self.startGame)
        self.root.protocol("WM_DELETE_WINDOW", self.exitGame)
        self.root.protocol("<Destroy>", self.exitGame)
        self.textCoord = (445, 200)
        self.font = tkinter.font.Font(family="Helvetica", size=12, weight=tkinter.font.BOLD)
        self.startDiaglog = None
        self.blockingVar = None

        self.bidButtons = BidButtons(self)
        self.continueButton = ContinueButton(self)
        self.nameTrumpButtons = NameTrumpButtons(self)
        self.activeHand = ActiveHand(self)
        self.confirmNamePartner = ConfirmNamePartner(self)

        self.showKiddiePause = 3
        self.showPlayPause = 1.5
        self.showTrickPause = 2
        self.showTrickResultPause = 1

    def __blockForInput(self, var):
        self.blockingVar = var
        self.root.wait_variable(self.blockingVar)

        if self.blockingVar.get() == "__EXIT__":
            self.blockingVar.set(None)

        self.blockingVar = None

    def startGame(self, event=None):
        if self.engine.isIdle():
            getLogger().log("new game request")
            self.engine.launch()
        else:
            getLogger().log("ignoring new game request")

    def exitGame(self, event=None):
        if self.blockingVar:
            self.blockingVar.set("__EXIT__")
        self.engine.shutdown()
        self.root.destroy()
        self.root.quit()

    def addKiddieToHand(self, kiddie):
        for card in kiddie.cardList:
            self.table.addCardToHand(card)
        self.table.sortHandByTrump()
        self.canvas.update_idletasks()

    def announceBid(self, bid):
        self.table.displayBid(bid)
        self.canvas.update_idletasks()

    def announceBidder(self, bid):
        self.continueButton.show()
        self.__blockForInput(self.continueButton.ok)
        self.table.clearBids()
        self.table.displayBidder(bid)
        self.canvas.update_idletasks()

    def announceGameWinner(self, gameWinner):
        message = gameWinner.name + " won the game."
        textid = self.canvas.create_text(self.textCoord, text=message, font=self.font, anchor=tkinter.CENTER)
        self.table.resetForNextRound()
        self.continueButton.show()
        self.__blockForInput(self.continueButton.ok)
        self.canvas.delete(textid)
        self.canvas.update_idletasks()

    def announceMessage(self, message):
        #        getLogger().log("Announcing: " + message)
        pass

    def announcePartners(self, bidder, partner):
        self.table.displayPartner(partner)
        self.canvas.update_idletasks()

    def announcePartnerCard(self, partnerCard):
        self.table.displayPartnerCard(partnerCard)
        self.canvas.update_idletasks()

    def announceRoundResult(self, message):
        textid = self.canvas.create_text(self.textCoord, text=message, font=self.font, anchor=tkinter.NW)
        self.continueButton.show()
        self.__blockForInput(self.continueButton.ok)

        self.canvas.delete(textid)
        self.table.resetForNextRound()
        self.canvas.update_idletasks()

    def announceScore(self, player):
        self.table.updatePlayer(player)
        self.canvas.update_idletasks()

    def announceTrump(self, trump):
        self.table.trump = trump
        self.table.sortHandByTrump()
        self.table.displayTrump()
        self.canvas.update_idletasks()

    def readyToBegin(self):
        getLogger().log("Waiting for players to join")

        if self.startDiaglog is None:
            self.startDiaglog = StartGameDialog(self)
            self.canvas.update_idletasks()

        result = self.startDiaglog.ok.get()
        if result == "OK":
            return True
        else:
            return False

    def getBid(self, validBids, minBid):
        showButtons = "pass" in validBids
        self.bidButtons.show(validBids, showButtons)

        if showButtons:
            self.__blockForInput(self.bidButtons.bid)
        else:
            self.bidButtons.makeBid()

        if self.bidButtons.bid.get() == "None":
            bid = None
        else:
            bid = self.bidButtons.bid.get()
        return bid

    def getGameType(self):
        d = GameTypeDialog(self.root)

        if d.okpressed:
            return d.getGameType()
        else:
            getLogger().log("Root windows closed, exiting....")
            sys.exit()

    def getCardFromHand(self, hand, msg, possiblePlays=None):
        card = None
        if possiblePlays is None:
            possiblePlays = hand

        # msgTag = self.canvas.create_text(self.textCoord, text=msg, font=self.font, anchor=tkinter.NW)

        while card is None or not (card in possiblePlays):
            self.activeHand.enable()
            self.__blockForInput(self.activeHand.cardToThrow)
            self.activeHand.disable()
            if self.activeHand.cardToThrow.get() == "None":
                return
            try:
                card = self.table.hand[int(self.activeHand.cardToThrow.get())].card
            except KeyError:
                pass
        self.table.removeCardFromHand(self.activeHand.cardToThrow.get())
        # self.canvas.delete(msgTag)
        self.table.sortHandByTrump()
        return card

    def greet(self):
        d = GreetDialog(self.root)
        getLogger().log("Name is " + str(d.playerName))

        if d.playerName is None or len(d.playerName) == 0:
            getLogger().log("Root windows closed, exiting....")
            sys.exit()

        return d.playerName

    def joinTable(self, localPlayer):
        self.table.sitPlayer(localPlayer.name)
        player = localPlayer.toTheRight
        while not (player is None):
            self.table.sitPlayer(player.name, False)
            player = player.toTheRight

        self.root.update()

    def namePartnerCard(self, validCardList):
        self.confirmNamePartner.show(validCardList)

        while not (self.confirmNamePartner.action.get() in ["CONFIRM", "CHANGE"]):
            self.__blockForInput(self.confirmNamePartner.action)
        self.confirmNamePartner.clearImages()

        if self.confirmNamePartner.action.get() == "CONFIRM":
            cardToReturn = validCardList[0]
        else:
            namePartner = NamePartner(self, validCardList)

            while namePartner.partnerCard is None:
                self.continueButton.show()
                self.__blockForInput(self.continueButton.ok)

            namePartner.clearImages()
            cardToReturn = namePartner.partnerCard
        return cardToReturn

    def nameTrump(self):
        self.nameTrumpButtons.show()
        self.__blockForInput(self.nameTrumpButtons.trump)
        if self.nameTrumpButtons.trump.get() == "None":
            trump = None
        else:
            trump = self.nameTrumpButtons.trump.get()
        return trump

    #    def newGame(self):
    #        self.engine.initializeGame(self).play()

    def run(self, engine):
        self.engine = engine
        self.root.event_generate("<Control-n>", when="tail")
        self.root.mainloop()

    def showHand(self, hand):

        existingCardList = []
        for cardImageTag in self.table.hand.keys():
            existingCardList.append(cardImageTag)

        for cardImageTag in existingCardList:
            self.table.removeCardFromHand(cardImageTag)

        self.table.handX = self.table.handXStart

        for card in hand:
            self.table.addCardToHand(card)
            self.canvas.update_idletasks()

    def showDealtCards(self):
        for pos in self.table.playersAtTable.values():
            if pos == "S":
                continue

            (x, y) = self.table.handXY[pos]

            card = 0
            while card < 9:
                self.canvas.create_image((x, y), image=self.images.deck_back, anchor=tkinter.NW)
                if pos in ["W", "E"]:
                    (x, y) = (x, y + 20)
                else:
                    (x, y) = (x + 20, y)
                card += 1
        self.canvas.update_idletasks()

    def showPlay(self, play):
        pos = self.table.playersAtTable[play.player.name]
        (x, y) = self.table.playXY[pos]
        playId = self.canvas.create_image((x, y), image=self.images.imageForCard(play.card),
                                          anchor=self.table.playAnchors[pos])
        self.table.trickCardList.append(playId)
        self.canvas.update_idletasks()
        if type(play.player).__name__ == 'AIPlayer':
            time.sleep(self.showPlayPause)
        self.canvas.update_idletasks()

    def showKiddie(self, kiddie):
        kiddieIds = []
        (x, y) = self.table.kiddieCoords
        for card in kiddie.cardList:
            kiddieId = self.canvas.create_image((x, y), image=self.images.imageForCard(card), anchor=tkinter.CENTER)
            kiddieIds.append(kiddieId)
            x += self.images.imageForCard(card).width()
            x += 20

        self.canvas.update_idletasks()
        time.sleep(self.showKiddiePause)

        for kiddieId in kiddieIds:
            self.canvas.delete(kiddieId)
        self.canvas.update_idletasks()

    def showTrickResult(self, winningPlayer, trickCount):
        time.sleep(self.showTrickPause)
        pos = self.table.playersAtTable[winningPlayer.name]
        (x, y) = self.table.playXY[pos]
        winningCard = self.canvas.find_closest(x, y)
        winningCardAnchor = self.canvas.itemcget(winningCard, "anchor")
        for trickCardTag in self.table.trickCardList:
            self.canvas.delete(trickCardTag)
        trickPile = self.canvas.create_image((x, y), image=self.images.deck_back, anchor=winningCardAnchor)
        self.table.updateTrickCount(trickCount)
        # continueButtons = ContinueButton(self)
        # self.__blockForInput(continueButtons.ok)
        self.canvas.update_idletasks()
        time.sleep(self.showTrickResultPause)

        self.table.resetForNextTrick()
        self.canvas.delete(trickPile)
        self.canvas.update_idletasks()

    def sitPlayer(self, playerName):
        self.table.sitPlayer(playerName)
        self.announceMessage(playerName + " has joined the game!")


class GUITkTable():
    def __init__(self, tkview):
        self.__tablePositions = ["S", "E", "NE", "NW", "W"]
        self.handXStart = 360
        self.handX = self.handXStart
        self.handY = 570
        self.handXY = {"E": (105, 200), "NE": (140, 25), "NW": (575, 25), "W": (780, 200),
                       "S": (self.handXStart, self.handY)}
        self.playXY = {"E": (245, 340), "NE": (350, 150), "NW": (600, 150), "W": (705, 340),
                       "S": (self.handXStart + 110, self.handY - 15)}
        self.nameXY = {"E": (95, 310), "NE": (100, 50), "NW": (840, 50), "W": (860, 310),
                       "S": (self.handXStart - 10, self.handY + 35)}
        self.bidXY = {"E": (95, 325), "NE": (100, 65), "NW": (840, 65), "W": (860, 325),
                      "S": (self.handXStart - 10, self.handY + 50)}
        self.trickXY = {"E": None, "NE": None, "NW": None, "W": None, "S": None}
        self.nameAnchors = {"E": tkinter.NE, "NE": tkinter.NE, "NW": tkinter.NW, "W": tkinter.NW, "S": tkinter.NE}
        self.bidAnchors = {"E": tkinter.NE, "NE": tkinter.NE, "NW": tkinter.NW, "W": tkinter.NW, "S": tkinter.NE}
        self.playAnchors = {"E": tkinter.NW, "NE": tkinter.NE, "NW": tkinter.NW, "W": tkinter.NE, "S": tkinter.S}
        self.tkview = tkview
        self.playersAtTable = {}
        self.playerNameTag = {}
        self.lastCardCoord = None
        self.hand = {}
        self.trickCardList = []
        self.bidList = []
        self.kiddieCoords = (375, 300)
        self.trump = None
        self.trumpCoord = (300, 175)
        self.trumpTag = None
        self.partnerCardCoord = (465, 90)
        self.partnerCardTag = None
        self.okButtonCoord = (665, 610)
        self.passButtonCoord = (665, 610)
        self.bidButtonCoord = (665 + 65, 610)
        self.bidderTag = None
        self.partnerTag = None

    def addCardToHand(self, card):
        cardImageTag = self.tkview.canvas.create_image((self.handX, self.handY),
                                                       image=self.tkview.images.imageForCard(card), anchor=tkinter.NW)
        self.hand[cardImageTag] = CardInHand(card, self.handX, self.handY)
        self.handX += 20

    def clearBids(self):
        for bidId in self.bidList:
            self.tkview.canvas.delete(bidId)
        self.bidList = []

    def displayBid(self, bid):
        if bid.count == -1:
            msg = "Pass"
        elif bid.count == 18:
            msg = "HAAS"
        elif bid.count == 36:
            msg = "Double HAAS"
        elif bid.count == 4:
            msg = "Stuck for 4"
        else:
            msg = "Bids " + str(bid.count)

        pos = self.playersAtTable[bid.bidder.name]
        (x, y) = self.playXY[pos]
        anchor = self.playAnchors[pos]
        bidTag = self.tkview.canvas.create_text(x, y, text=msg, font=self.tkview.font, anchor=anchor)
        self.bidList.append(bidTag)

    def displayBidder(self, bid):
        msg = "Under for " + str(bid.count)

        pos = self.playersAtTable[bid.bidder.name]
        anchor = self.bidAnchors[pos]
        self.bidderTag = self.tkview.canvas.create_text(self.bidXY[pos], text=msg, font=self.tkview.font, anchor=anchor)

    def displayPartner(self, partner):
        pos = self.playersAtTable[partner.name]
        anchor = self.bidAnchors[pos]
        self.partnerTag = self.tkview.canvas.create_text(self.bidXY[pos], text="Partner", font=self.tkview.font,
                                                         anchor=anchor)

    def displayPartnerCard(self, partnerCard):
        self.partnerCardTag = self.tkview.canvas.create_image(self.partnerCardCoord,
                                                              image=self.tkview.images.imageForCard(partnerCard),
                                                              anchor=tkinter.CENTER)

    def displayTrump(self):
        if self.trump.suit == 'c':
            self.trumpTag = self.tkview.canvas.create_image(self.trumpCoord, image=self.tkview.images.suit_lrg_club,
                                                            anchor=tkinter.NW)
        if self.trump.suit == 'd':
            self.trumpTag = self.tkview.canvas.create_image(self.trumpCoord, image=self.tkview.images.suit_lrg_diamond,
                                                            anchor=tkinter.NW)
        if self.trump.suit == 'h':
            self.trumpTag = self.tkview.canvas.create_image(self.trumpCoord, image=self.tkview.images.suit_lrg_heart,
                                                            anchor=tkinter.NW)
        if self.trump.suit == 's':
            self.trumpTag = self.tkview.canvas.create_image(self.trumpCoord, image=self.tkview.images.suit_lrg_spade,
                                                            anchor=tkinter.NW)

    def removeCardFromHand(self, cardImageTag):
        self.tkview.canvas.delete(cardImageTag)
        del self.hand[int(cardImageTag)]

    def resetForNextTrick(self):
        for playId in self.trickCardList:
            self.tkview.canvas.delete(playId)
        self.trickCardList = []

        if self.partnerCardTag:
            self.tkview.canvas.delete(self.partnerCardTag)
            self.partnerCardTag = None

    def resetForNextRound(self):
        for pos in self.trickXY:
            tagId = self.trickXY[pos]
            if not (tagId is None):
                self.tkview.canvas.delete(tagId)

        if self.trumpTag:
            self.tkview.canvas.delete(self.trumpTag)
            self.trumpTag = None

        if self.bidderTag:
            self.tkview.canvas.delete(self.bidderTag)
            self.bidderTag = None

        if self.partnerTag:
            self.tkview.canvas.delete(self.partnerTag)
            self.partnerTag = None

        self.handX = self.handXStart
        self.trump = None

    def sitPlayer(self, playerName, toLeft=True):
        if toLeft is True:
            pos = self.__tablePositions.pop(0)
        else:
            pos = self.__tablePositions.pop(-1)
        self.playersAtTable[playerName] = pos
        anchor = self.nameAnchors[pos]
        text = playerName + ": 0"
        self.playerNameTag[playerName] = self.tkview.canvas.create_text(self.nameXY[pos], text=text,
                                                                        font=self.tkview.font, anchor=anchor)

    def sortHandByTrump(self):
        cardsInHand = []
        cardTags = []

        for cardTag in self.hand.keys():
            card = self.hand[cardTag].card
            if card == self.trump.leftBar:
                card.makeLeftBar()
            elif card == self.trump.rightBar:
                card.makeRightBar()

            cardsInHand.append(card)
            cardTags.append(cardTag)
        cardsInHand.sort()

        for cardTag in cardTags:
            self.removeCardFromHand(cardTag)

        self.handX = self.handXStart

        for card in cardsInHand:
            self.addCardToHand(card)

    def updatePlayer(self, player):
        playerTag = self.playerNameTag[player.name]
        self.tkview.canvas.delete(playerTag)
        pos = self.playersAtTable[player.name]
        anchor = self.nameAnchors[pos]
        text = player.name + ": " + str(player.score)
        self.playerNameTag[player.name] = self.tkview.canvas.create_text(self.nameXY[pos], text=text,
                                                                         font=self.tkview.font, anchor=anchor)

    def updateTrickCount(self, trickCount):
        for playerName in trickCount:
            pos = self.playersAtTable[playerName]
            if not (self.trickXY[pos] is None):
                self.tkview.canvas.delete(self.trickXY[pos])

            (x, y) = self.nameXY[pos]
            msg = "Tricks: " + str(trickCount[playerName])
            anchor = self.nameAnchors[pos]
            self.trickXY[pos] = self.tkview.canvas.create_text((x, y - 25), text=msg, font=self.tkview.font,
                                                               anchor=anchor)


class GreetDialog(tkinter.Toplevel):

    def __init__(self, parent):
        tkinter.Toplevel.__init__(self, parent)
        self.playerName = None
        self.transient(parent)
        self.resizable(tkinter.FALSE, tkinter.FALSE)
        self.title("Welcome")
        self.lift(parent)

        tkinter.Label(self, text="Enter your name:", anchor=tkinter.W).pack()
        self.e = tkinter.Entry(self)
        self.e.pack(padx=5)
        self.e.focus_set()

        b = tkinter.Button(self, text="OK", command=self.ok)
        b.pack(pady=5)

        self.grab_set()
        self.bind("<Return>", self.ok)
        self.bind("<Escape>", self.cancel)
        self.protocol("WM_DELETE_WINDOW", self.cancel)

        ws = self.winfo_screenwidth()
        hs = self.winfo_screenheight()
        # calculate position x, y
        w = 175
        h = 75
        x = (ws / 2) - (w / 2)
        y = (hs / 2) - (h / 2)
        self.geometry('%dx%d+%d+%d' % (w, h, x, y))
        self.wait_window(self)

    def ok(self, event=None):
        self.playerName = self.e.get()
        if self.playerName is None or len(self.playerName) == 0:
            pass
        else:
            self.destroy()

    def cancel(self, event=None):
        pass


class ImageMgrTk:
    def __init__(self):
        fullPathToPyFile = os.path.abspath(sys.argv[0])
        indexOfSlash = fullPathToPyFile.rfind('/') + 1
        imageDir = fullPathToPyFile[0:indexOfSlash] + "images"
        self.deck_back = tkinter.PhotoImage(file=os.path.join(imageDir, "Deck1.gif"))
        self.deck = {}
        for card in Deck().cardList:
            if card.value == "10":
                cV = "T"
            else:
                cV = card.value
            self.deck[card.value + card.suit] = tkinter.PhotoImage(
                file=os.path.join(imageDir, card.suit.upper() + cV + '.gif'))
        self.button_bid = tkinter.PhotoImage(file=os.path.join(imageDir, "bid.gif"))
        self.button_pass = tkinter.PhotoImage(file=os.path.join(imageDir, "pass.gif"))
        self.button_ok = tkinter.PhotoImage(file=os.path.join(imageDir, "ok.gif"))
        self.suit_club = tkinter.PhotoImage(file=os.path.join(imageDir, "Club.gif"))
        self.suit_diamond = tkinter.PhotoImage(file=os.path.join(imageDir, "Diamond.gif"))
        self.suit_heart = tkinter.PhotoImage(file=os.path.join(imageDir, "Heart.gif"))
        self.suit_spade = tkinter.PhotoImage(file=os.path.join(imageDir, "Spade.gif"))
        self.suit_lrg_club = tkinter.PhotoImage(file=os.path.join(imageDir, "Club_large.gif"))
        self.suit_lrg_diamond = tkinter.PhotoImage(file=os.path.join(imageDir, "Diamond_large.gif"))
        self.suit_lrg_heart = tkinter.PhotoImage(file=os.path.join(imageDir, "Heart_large.gif"))
        self.suit_lrg_spade = tkinter.PhotoImage(file=os.path.join(imageDir, "Spade_large.gif"))

    def imageForCard(self, card):
        return self.deck[card.value + card.suit]


class NamePartner:
    def __init__(self, tkview, validCardList):
        self.tkview = tkview
        self.partnerCard = None
        self.imageTable = {}
        x = 200
        self.partnerCardImage = None
        for card in validCardList:
            cardImageTag = self.tkview.canvas.create_image((x, 200), image=self.tkview.images.imageForCard(card),
                                                           anchor=tkinter.NW)
            self.imageTable[str(cardImageTag)] = card
            self.tkview.canvas.tag_bind(cardImageTag, "<ButtonRelease-1>", self.selectCard)
            x += 78

    def selectCard(self, event=None):
        if event is None:
            pass
        else:
            cardImageTag = self.tkview.canvas.find_closest(event.x, event.y)[0]
            if str(cardImageTag) in self.imageTable.keys():
                self.partnerCard = self.imageTable[str(cardImageTag)]
                if self.partnerCardImage:
                    self.tkview.canvas.delete(int(self.partnerCardImage))
                (cx, cy) = self.tkview.canvas.coords(cardImageTag)
                self.partnerCardImage = self.tkview.canvas.create_rectangle(cx - 5, cy - 5, cx + 75, cy + 100,
                                                                            outline="red", width=1.5)

    def clearImages(self):
        for imageTag in self.imageTable.keys():
            self.tkview.canvas.delete(int(imageTag))

        if self.partnerCardImage:
            self.tkview.canvas.delete(int(self.partnerCardImage))


class NameTrumpButtons:

    def __init__(self, tkview):
        self.trump = tkinter.StringVar()
        self.tkview = tkview

    def show(self):
        self.trump.set(None)

        self.club_image = self.tkview.canvas.create_image((450, 300), image=self.tkview.images.suit_club,
                                                          anchor=tkinter.S)
        self.diamond_image = self.tkview.canvas.create_image((500, 300), image=self.tkview.images.suit_diamond,
                                                             anchor=tkinter.S)
        self.heart_image = self.tkview.canvas.create_image((450, 350), image=self.tkview.images.suit_heart,
                                                           anchor=tkinter.S)
        self.spade_image = self.tkview.canvas.create_image((500, 350), image=self.tkview.images.suit_spade,
                                                           anchor=tkinter.S)

        self.tkview.canvas.tag_bind(self.club_image, "<ButtonRelease-1>", self.selectClubs)
        self.tkview.canvas.tag_bind(self.diamond_image, "<ButtonRelease-1>", self.selectDiamonds)
        self.tkview.canvas.tag_bind(self.heart_image, "<ButtonRelease-1>", self.selectHearts)
        self.tkview.canvas.tag_bind(self.spade_image, "<ButtonRelease-1>", self.selectSpades)

    def selectClubs(self, event=None):
        self.__clearImages()
        self.trump.set("c")

    def selectDiamonds(self, event=None):
        self.__clearImages()
        self.trump.set("d")

    def selectHearts(self, event=None):
        self.__clearImages()
        self.trump.set("h")

    def selectSpades(self, event=None):
        self.__clearImages()
        self.trump.set("s")

    def __clearImages(self):
        self.tkview.canvas.delete(self.club_image)
        self.tkview.canvas.delete(self.diamond_image)
        self.tkview.canvas.delete(self.heart_image)
        self.tkview.canvas.delete(self.spade_image)


class StartGameDialog(ContinueButton):

    def __init__(self, parent):
        ContinueButton.__init__(self, parent)

        self.startpressed = False


if __name__ == '__main__':
    pass
