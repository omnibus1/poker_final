package pl.edu.agh.kis.pz1;

public class Card implements Comparable<Card> {
    Rank rank;
    Suit suit;

    Card(Rank rank,Suit suit){
        this.rank=rank;
        this.suit=suit;
    }

    String suitToString(){
        return switch (suit) {
            case KIER -> "\u2665";
            case PIK -> "\u2660";
            case KARO -> "\u2666";
            case TREFL -> "\u2663";
        };
    }
    @Override
    public String toString(){
        return this.rank+" "+suitToString();
    }

    @Override
    public int compareTo(Card o) {
        final int rank_comparisont=Integer.compare(this.rank.getRankValue(),o.rank.getRankValue());
        if(rank_comparisont!=0){
            return rank_comparisont;
        }
        return Integer.compare(this.suit.getSuitValue(),o.suit.getSuitValue());
    }

    public Rank getRank() {
        return this.rank;
    }

    public Suit getSuit(){
        return this.suit;
    }

    enum Rank {
        TWO(2),THREE(3),FOUR(4),FIVE(5),SIX(6),SEVEN(7),EIGHT(8),NINE(9),TEN(10),JACK(11),QUEEN(12),KING(13),ACE(14);
        final int rankValue;
        int getRankValue(){
            return this.rankValue;
        }
        Rank(int rankValue) {
            this.rankValue=rankValue;
        }
    }
    enum Suit {
        KIER(1),PIK(2),KARO(3),TREFL(4);
        final int suitValue;
        int getSuitValue(){
            return this.suitValue;
        }
        Suit(int suitValue) {
            this.suitValue=suitValue;
        }
    }
    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null|| this.getClass()!=o.getClass()){
            return false;
        }
        Card other=(Card)o;
        return (this.rank==other.rank && this.suit==other.suit);
    }






}