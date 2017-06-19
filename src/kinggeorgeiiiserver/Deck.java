/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kinggeorgeiiiserver;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author coolsmileman
 */
public class Deck {
    private enum cardValues{hearts, clubs, diamonds, spades};
    
    private ArrayList<Card> deck = new ArrayList<>();
    
    public Deck(){
        for(int i= 2; i<=14; i++)
            for(cardValues values: cardValues.values())
                deck.add(new Card(i,values.name()));
    }
    
    public void shuffle(){
        Collections.shuffle(deck);
    }
    
    public boolean isEmpty(){
        return deck.isEmpty();
    }
    /*
    public Card showCard(){
        return deck.get(0);
    }
    */
    public Card getCard(){
        if(deck.isEmpty())
            return null;
        else{
            Card firstCard = deck.get(0);
            deck.remove(0);
            return firstCard;
        }
    }
    
    @Override
    public String toString(){
        String showDeck = "";
        for(Card card: deck)
            showDeck+=card+"\n";
        return("Deck: "+showDeck);
    }
    
}
