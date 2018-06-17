package kr.ac.cnu.web.games.blackjack;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rokim on 2018. 5. 26..
 */
public class Hand {
    private Deck deck;
    @Getter
    private List<Card> cardList = new ArrayList<>();

    public Hand(Deck deck) {
        this.deck = deck;
    }

    public Card drawCard() {
        Card card = deck.drawCard();
        cardList.add(card);
        return card;
    }

    public int getCardSum() {
        int sum = 0;

        for (Card card : cardList) {
            if(card.getRank() == 1) {
                if(sum <= 10) {
                    sum += 11;
                }
                else {
                    sum += 1;
                }
            }
            else if(card.getRank() > 10 && card.getRank() != 1) {
                sum += 10;
            }
            else {
                sum += card.getRank();
            }
        }
        return sum;
    }

    public void reset() {
        cardList.clear();
    }
}
