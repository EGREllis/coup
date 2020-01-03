package net.coup;

import net.coup.engine.Engine;
import net.coup.engine.EngineImpl;
import net.coup.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EngineTest {
    @Test
    public void when_incomeMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Player player1 = board.getPlayers().get("Player1");
        Move move = new Move(player1, player1, Action.INCOME);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 1 : String.format("Expected %1$d coins after income but got %2$d", 1, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_foreignAidMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Player player1 = board.getPlayers().get("Player1");
        Move move = new Move(player1, player1, Action.FOREIGN_AID);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 2 : String.format("Expected %1$d coins after income but got %2$d", 2, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_taxMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Player player1 = board.getPlayers().get("Player1");
        Move move = new Move(player1, player1, Action.TAX);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 3 : String.format("Expected %1$d coins after income but got %2$d", 3, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_stealMoveTaken_then_playerCoinsInceaseAndTargetsDecrease() {
        Board board = newBoard("Player1", "Player2");
        Player player1 = board.getPlayers().get("Player1");
        Move move1 = new Move(player1, player1, Action.TAX);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move1, Collections.EMPTY_MAP);

        assert result.getPlayers().get("Player1").getCoins() == 3 : "Expected 3 coins after successful tax";

        player1 = result.getPlayers().get("Player1");
        Player player2 = result.getPlayers().get("Player2");
        Move move2 = new Move(player2, player1, Action.STEAL);

        result = engine.processTurn(result, move2, Collections.EMPTY_MAP);

        assert result.getPlayers().get("Player1").getCoins() == 1 : "Expected 1 coin after tax and steal";
        assert result.getPlayers().get("Player2").getCoins() == 2 : "Expected 2 coin after successful steal";
    }

    private Board newBoard(String ... names) {
        List<String> players = new ArrayList<>();
        for (String name : names) {
            players.add(name);
        }
        return Board.newGame(players);
    }
}
