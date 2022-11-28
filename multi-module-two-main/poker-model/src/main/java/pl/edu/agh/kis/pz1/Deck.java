package pl.edu.agh.kis.pz1;

import java.util.*;

public class Deck {
    ArrayList<Card> gameDeck;


    void fillDeck(){
        gameDeck.clear();
        for (Card.Rank r : Card.Rank.values()) {
            for (Card.Suit s : Card.Suit.values()) {
                gameDeck.add(new Card(r, s));
            }

        }
        sort();

    }
    Deck(){
        gameDeck=new ArrayList<>();
    }



    void shuffle(){
        Collections.shuffle(gameDeck);
    }
    void sort(){
        Collections.sort(gameDeck);
    }
    Card draw(){
        return gameDeck.remove(0);
    }
    ArrayList<Card> drawCard(int n){
        ArrayList<Card> cardsDrawn=new ArrayList<>();
        for(int i=0;i<n;i++){
            cardsDrawn.add(draw());

        }
        return cardsDrawn;
    }

    int getSize(){
        return gameDeck.size();
    }
}