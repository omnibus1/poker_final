package pl.edu.agh.kis.pz1;



import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class Player {
    public String name="Anonymus";
    int balance=300;
    boolean hasMoved=false;
    boolean inGame=false;

    String chosenMove="";
    boolean hasReplaced;

    SocketChannel socket;
    ArrayList<Card> playerDeck=new ArrayList<>();
    Player(String name,int balance){
        this.name=name;
        this.balance=balance;


    }
    Player(){}


    Player(String name,SocketChannel socket){
        this.name=name;
        this.socket=socket;
    }

    void printDeck(){
        int i=0;
        logger(this.name+" ");
        for(Card card:playerDeck){
            logger(Integer.toString(i)+"-"+card+", ");
            i++;
        }
        logger("");
    }
    public String getDeck(){
        int i=0;
        String deckString="";
        logger(this.name+" ");
        for(Card card:playerDeck){
            deckString+=Integer.toString(i)+"-"+card+", ";
            i++;
        }
        deckString+="\n";
        return deckString;

    }

    void addBalance(int amount){
        if(amount>0){
            this.balance+=amount;
        }

    }
    int getBalance(){
        return this.balance;
    }
    void setBalance(int amount){
        if(amount>0){
            this.balance=amount;
        }

    }
    void printCards(ArrayList<Card> cards){
        for(Card c:cards){
            System.out.print(c);
        }
    }
    void drawFromDeck(Deck deck){
        this.playerDeck=deck.drawCard(5);
    }
    String getChosenMove(){
        return this.chosenMove;
    }
    void setChosenMove(String move){
        this.chosenMove=move;
    }
    static void logger(String message){
        System.out.println(message);
    }


}