package pl.edu.agh.kis.pz1;

import java.util.*;

public class Deck {
    static ArrayList<Card> deck=new ArrayList<>();
    static ArrayList<Card>returned=new ArrayList<>();

    void fill_deck(){
        for(Card.Rank r:Card.Rank.values()){
            for(Card.Suit s:Card.Suit.values()){
                deck.add(new Card(r,s));
            }

        }
    }
    void print_deck(){
        print(Deck.deck);
    }
    void print_returned(){
        print(Deck.returned);
    }
    void print(ArrayList<Card>deck){
        for(int i=0;i<deck.size();i++){
            System.out.println(deck.get(i));
        }
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
    ArrayList<Card> draw_card(int n){
        ArrayList<Card> cardsDrawn=new ArrayList<>();
        for(int i=0;i<n;i++){
            cardsDrawn.add(draw());

        }
        return cardsDrawn;
    }
    void replace(Player player, int ... indexes){
        ArrayList<Integer>cardIndex=new ArrayList<>();
        for(int i:indexes){
            cardIndex.add(i);
        }
        Collections.sort(cardIndex);
        for(int i:cardIndex){
            Card old=player.Deck.get(i);

            player.Deck.set(i,Deck.draw());
            Card newCard=player.Deck.get(i);
            System.out.println("Replaced "+old+" for "+ newCard);
            returned.add(old);
        }

    }
}