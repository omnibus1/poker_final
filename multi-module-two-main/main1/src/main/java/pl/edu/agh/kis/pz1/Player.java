package pl.edu.agh.kis.pz1;



import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;

public class Player {
    public String name="Anonymus";
    public int balance=300;
    boolean hasMoved=false;
    boolean inGame=false;
    boolean wantsToStart=false;
    public String chosenMove="";
    boolean hasReplaced;

    SocketChannel socket;
    ArrayList<Card> Deck=new ArrayList<>();
    Player(String name,int balance){
        this.name=name;
        this.balance=balance;


    }
    Player(){

        this.name="none";
    }
    Player(String name,SocketChannel socket){
        this.name=name;
        this.socket=socket;
    }

    void printDeck(){
        int i=0;
        System.out.print(this.name+" ");
        for(Card card:Deck){
            System.out.print(Integer.toString(i)+"-"+card+", ");
            i++;
        }
        System.out.println("");
    }
    public String getDeck(){
        int i=0;
        String deckString="";
        System.out.print(this.name+" ");
        for(Card card:Deck){
            deckString+=Integer.toString(i)+"-"+card+", ";
            i++;
        }
        deckString+="\n";
        return deckString;

    }

    void addBalance(int amount){
        this.balance+=amount;

    }
    int getBalance(){
        return this.balance;
    }
    void setBalance(int amount){
        this.balance=amount;
    }
    void printCards(ArrayList<Card> cards){
        for(Card c:cards){
            System.out.print(c);
        }
    }
    void drawFromDeck(Deck deck){
        this.Deck=deck.draw_card(5);
    }





}