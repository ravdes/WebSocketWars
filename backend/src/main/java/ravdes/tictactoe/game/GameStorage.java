package ravdes.tictactoe.game;

import ravdes.tictactoe.game.entities.Game;
import java.util.HashMap;
import java.util.Map;

public class GameStorage {
	private static final Map<String, Game> games = new HashMap<>();
	private static GameStorage instance;

	private GameStorage() {

	}

	public static synchronized GameStorage getInstance() {
		if (instance == null) {
			instance = new GameStorage();
		}
		return instance;
	}

	public Map<String, Game> getGames() {
		return games;
	}

	public void setGame(Game game) {
		games.put(game.getGameId(), game);
	}

	public boolean doesGameExist(String gameId) {
		return games.containsKey(gameId);
	}

}
