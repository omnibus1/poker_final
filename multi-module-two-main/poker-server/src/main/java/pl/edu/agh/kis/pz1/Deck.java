package pl.edu.agh.kis.pz1;

import java.util.*;

public class Deck {
    static ArrayList<Card> deck=new ArrayList<>();


    void fillDeck(){
        for(Card.Rank r:Card.Rank.values()){
            for(Card.Suit s:Card.Suit.values()){
                deck.add(new Card(r,s));
            }

        }
        sort();
    }



    void shuffle(){
        Collections.shuffle(deck);
    }
    void sort(){
        Collections.sort(deck);
    }
    static Card draw(){
        return deck.remove(0);
    }
    ArrayList<Card> drawCard(int n){
        ArrayList<Card> cardsDrawn=new ArrayList<>();
        for(int i=0;i<n;i++){
            cardsDrawn.add(draw());

        }
        return cardsDrawn;
    }

    int getSize(){
        return deck.size();
    }
}