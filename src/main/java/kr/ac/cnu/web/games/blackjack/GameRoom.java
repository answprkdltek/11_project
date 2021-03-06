package kr.ac.cnu.web.games.blackjack;

import kr.ac.cnu.web.exceptions.NotEnoughBalanceException;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rokim on 2018. 5. 26..
 */
public class GameRoom {
    @Getter
    private final String roomId;
    @Getter
    private final Dealer dealer;
    @Getter
    private final Map<String, Player> playerList;
    @Getter
    private final Deck deck;
    @Getter
    private boolean isFinished;
    private final Evaluator evaluator;

    public GameRoom(Deck deck) {
        this.roomId = UUID.randomUUID().toString();
        this.deck = deck;
        this.dealer = new Dealer(new Hand(deck));
        this.playerList = new HashMap<>();
        this.evaluator = new Evaluator(playerList, dealer);
        this.isFinished = true;
    }

    public void addPlayer(String playerName, long seedMoney) {
        Player player = new Player(seedMoney, new Hand(deck));

        playerList.put(playerName, player);
    }

    public void removePlayer(String playerName) {
        playerList.remove(playerName);
    }

    public void reset() {
        dealer.reset();
        playerList.forEach((s, player) -> player.reset());
    }

    public void bet(String name, long bet) {
        Player player = playerList.get(name);

        player.placeBet(bet);
    }

    public void deal() {
        this.isFinished = false;
        dealer.deal();
        playerList.forEach((s, player) -> player.deal());
    }

    public Card hit(String name) {
        Player player = playerList.get(name);

        Card card = player.hitCard();
        if (player.getHand().getCardSum() > 21) {
            player.stand();
            playDealer();
        }

        return card;
    }

    public void stand(String name) {
        Player player = playerList.get(name);

        player.stand();
    }

    public Card doubleDown(String name) {
        Player player = playerList.get(name);

        try {
            long additionalBet = player.getCurrentBet();
            player.placeBet(additionalBet);
        }
        catch (NotEnoughBalanceException e) {
            long allIn = player.getBalance();
            player.placeBet(allIn);
        }

        Card card = player.hitCard();
        player.stand();
        return card;
    }

    public void playDealer() {
        dealer.play();
        evaluator.evaluate();
        this.isFinished = true;
        if(deck.getCardList().size() <= 10){
            deck.getCardList().clear();  //기존카드삭제
            deck.createCards(1);  //덱 재생성
            Collections.shuffle(deck.getCardList()); //덱 셔플
        }
    }
}
