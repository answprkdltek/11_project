package kr.ac.cnu.web.service;

import kr.ac.cnu.web.exceptions.NoLoginException;
import kr.ac.cnu.web.exceptions.NoUserException;
import kr.ac.cnu.web.games.blackjack.Deck;
import kr.ac.cnu.web.games.blackjack.GameRoom;
import kr.ac.cnu.web.games.blackjack.Player;
import kr.ac.cnu.web.model.User;
import kr.ac.cnu.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rokim on 2018. 5. 26..
 */
@Service
public class BlackjackService {
    private final int DECK_NUMBER = 1;
    private final int MIN_BET = 3000;
    private final Map<String, GameRoom> gameRoomMap = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    public GameRoom createGameRoom(String name) {
        User user = this.getUserFromSession(name);
        Deck deck = new Deck(DECK_NUMBER);

        GameRoom gameRoom = new GameRoom(deck);
        gameRoom.addPlayer(user.getName(), user.getAccount());

        gameRoomMap.put(gameRoom.getRoomId(), gameRoom);

        return gameRoom;
    }

    public GameRoom joinGameRoom(String roomId, String name) {
        // multi player Game 이 아니므로 필요가 없다.
        return null;
    }

    public void leaveGameRoom(String roomId, String name) {
        User user = this.getUserFromSession(name);
        gameRoomMap.get(roomId).removePlayer(user.getName());
    }

    public GameRoom getGameRoom(String roomId) {
        return gameRoomMap.get(roomId);
    }

    public GameRoom bet(String roomId, String name, long bet) {
        GameRoom gameRoom = gameRoomMap.get(roomId);
        User user = this.getUserFromSession(name);

        if (bet < MIN_BET) {
            if(bet != user.getAccount()){
                return gameRoom;
            }
        }

        gameRoom.reset();
        gameRoom.bet(user.getName(), bet);
        gameRoom.deal();

        this.updateUser(user, gameRoom);

        return gameRoom;
    }

    public GameRoom hit(String roomId, String name) {
        GameRoom gameRoom = gameRoomMap.get(roomId);
        User user = this.getUserFromSession(name);

        gameRoom.hit(user.getName());

        this.updateUser(user, gameRoom);

        return gameRoom;
    }

    public GameRoom stand(String roomId, String name) {
        GameRoom gameRoom = gameRoomMap.get(roomId);
        User user = this.getUserFromSession(name);

        gameRoom.stand(user.getName());
        gameRoom.playDealer();

        this.updateUser(user, gameRoom);

        return gameRoom;
    }

    public GameRoom doubleDown(String roomId, String name) {
        GameRoom gameRoom = gameRoomMap.get(roomId);
        User user = this.getUserFromSession(name);

        gameRoom.doubleDown(user.getName());
        gameRoom.playDealer();

        this.updateUser(user, gameRoom);

        return gameRoom;
    }

    public User login(String name) {
        return userRepository.findById(name).orElseThrow(() -> new NoLoginException());
    }

    private User getUserFromSession(String name) {
        return userRepository.findById(name).orElseThrow(() -> new NoUserException());
    }

    private void updateUser(User user, GameRoom gameRoom) {
        Player player = gameRoom.getPlayerList().get(user.getName());
        user.setAccount(player.getBalance());

        userRepository.save(user);
    }

    public List<User> getTopNUsers(int nRecords) {
        List<User> users = userRepository.findAllByOrderByAccountDesc();

        if ((nRecords > 0) && (nRecords < users.size())) {
            return users.subList(0, nRecords);
        }
        else {
            return users;
        }
    }
}
