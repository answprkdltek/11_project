package kr.ac.cnu.web.games.blackjack;

import java.util.Map;

/**
 * Created by rokim on 2018. 5. 27..
 */
public class Evaluator {
    private Map<String, Player> playerMap;
    private Dealer dealer;

    public Evaluator(Map<String, Player> playerMap, Dealer dealer) {
        this.playerMap = playerMap;
        this.dealer = dealer;
    }

    public boolean evaluate() {
        if (playerMap.values().stream().anyMatch(player -> player.isPlaying())) {
            return false;
        }

        int dealerResult = dealer.getHand().getCardSum();



        playerMap.forEach((s, player) -> {
            int playerResult = player.getHand().getCardSum();
            if (dealerResult <= 21) {    //딜러가 21이하인 경우
                if (playerResult > 21) {
                    player.lost();
                } else if (playerResult > dealerResult) {
                    player.win();
                } else if (playerResult == dealerResult) {
                    player.tie();
                } else {
                    player.lost();
                }
            }
            else //딜러가 21이상인 경우
            {
                if(playerResult > 21){  //둘다 21 넘었을 경우 둘다 lost
                    player.lost();
                }else{  // 플레이어는 21 이하인 경우 플레이어 win
                    player.win();
                }
            }
        });

        return true;
    }


}
