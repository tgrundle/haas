"""
Created on Nov 27, 2010

@author: timothy
"""

import sys

logger = None


def getLogger():
    global logger

    if logger is None:
        logger = Logger("--logging" in sys.argv)

    return logger


class Logger:
    '''
    classdocs
    '''

    def __init__(self, enabled):
        self.enabled = enabled
        self.prefix = "   ==>"

    def log(self, message, msgList=[]):
        if self.enabled == True:
            if len(msgList) > 0:
                print(self.prefix, message, msgList)
            else:
                print(self.prefix, message)

    def logCardList(self, label, cardList1, cardList2=[]):
        if self.enabled == True:
            printableList1 = []
            for card in cardList1:
                printableList1.append(str(card))

            if len(cardList2) > 0:
                printableList2 = []
                for card in cardList2:
                    printableList2.append(str(card))

                print(self.prefix + label + " " + str(printableList1) + " of " + str(printableList2))
            else:
                print(self.prefix + label + " " + str(printableList1))
