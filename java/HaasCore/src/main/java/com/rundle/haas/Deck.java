/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/ 
package com.rundle.haas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Deck {

  protected List<Card> cardList;
  private int cardListCount;

  public Deck() {
	
    cardListCount = Card.CardSuit.values().length * Card.CardValue.values().length * 2; //pinochle deck
    cardList = new ArrayList<Card>(cardListCount);

    for(Card.CardIndex index : Card.CardIndex.values()) {
        for (Card.CardSuit suit : Card.CardSuit.values()) {
            for (Card.CardValue value : Card.CardValue.values()) {
                cardList.add(new Card(value, suit, index));
            }
        }
    }

    shuffle();
    shuffle();
    shuffle();
  }

  public boolean Empty() {
    return cardListCount == 0;
  }

  public void shuffle() {
    int lastIdx = cardListCount - 1;
    int swapIdx;
    Card swapCard;
    Random rand = new Random();

    while (lastIdx > 1) {
      swapIdx = rand.nextInt(lastIdx);
      swapCard = cardList.get(swapIdx);
      cardList.set(swapIdx, cardList.get(lastIdx));
      cardList.set(lastIdx, swapCard);
      lastIdx--;
    }
  }
}
