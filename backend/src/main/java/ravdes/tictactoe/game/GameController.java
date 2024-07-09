package ravdes.tictactoe.game;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.game.dto.GameConnectRequest;
import ravdes.tictactoe.game.dto.GameInfoRequest;
import ravdes.tictactoe.game.entities.Game;
import ravdes.tictactoe.game.entities.GameMove;
import ravdes.tictactoe.game.entities.Player;

@RestController
@RequestMapping("/game")

public class GameController {
	private final GameService gameService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
		this.gameService = gameService;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@PostMapping("/create")
	public Game initializeGame(@RequestBody Player player) {
		return gameService.createNewGame(player);
	}

	@PostMapping("/connect")
	public Game connectToExistingGame(@RequestBody GameConnectRequest request) {
		Game game = gameService.connectToGame(request.player(), request.gameId());
		simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
		return game;
	}

	@PostMapping("/makeMove")
	public Game makeMoveOnBoard(@RequestBody GameMove request) {
		Game game = gameService.makeMove(request);
		simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
		return game;

	}

	@PostMapping("/info")
	public void gameInfo(@RequestBody GameInfoRequest request) {
		Game game =  gameService.getGameInfo(request.gameId());
		simpMessagingTemplate.convertAndSend("/topic/game-progress/" + request.gameId(), game);
	}

}
