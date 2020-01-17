package net.coup;

import net.coup.engine.Agent;
import net.coup.engine.Engine;
import net.coup.engine.EngineImpl;
import net.coup.model.*;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Expectation;
import org.jmock.api.Invocation;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class EngineTest {
    @Test
    public void when_incomeMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Move move = new Move("Player1", "Player1", Action.INCOME);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 1 : String.format("Expected %1$d coins after income but got %2$d", 1, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_foreignAidMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Move move = new Move("Player1", "Player1", Action.FOREIGN_AID);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 2 : String.format("Expected %1$d coins after income but got %2$d", 2, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_taxMoveTaken_then_playerCoinsIncrease() {
        Board board = Board.newGame(Collections.singletonList("Player1"));
        Move move = new Move("Player1", "Player1", Action.TAX);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move, Collections.EMPTY_MAP);
        assert result.getPlayers().get("Player1").getCoins() == board.getPlayers().get("Player1").getCoins() + 3 : String.format("Expected %1$d coins after income but got %2$d", 3, result.getPlayers().get("Player1").getCoins());
    }

    @Test
    public void when_stealMoveTaken_then_playerCoinsInceaseAndTargetsDecrease() {
        Board board = newBoard("Player1", "Player2");
        Move move1 = new Move("Player1", "Player1", Action.TAX);

        Engine engine = new EngineImpl();

        Board result = engine.processTurn(board, move1, Collections.EMPTY_MAP);

        assert result.getPlayers().get("Player1").getCoins() == 5 : "Expected 5 coins after successful tax (start with 2)";

        Move move2 = new Move("Player2", "Player1", Action.STEAL);

        result = engine.processTurn(result, move2, Collections.EMPTY_MAP);

        assert result.getPlayers().get("Player1").getCoins() == 3 : "Expected 3 coin after tax and steal";
        assert result.getPlayers().get("Player2").getCoins() == 4 : "Expected 4 coin after successful steal";
    }

    @Test
    public void when_exchangeMoveTaken_then_playerCardsChange() {
        Board board = newBoard("Player1");
        Move move = new Move("Player1", "Player1", Action.EXCHANGE);
        Engine engine = new EngineImpl();
        Agent agent = Mockito.mock(Agent.class);
        List<Card> nextHand = Arrays.asList(Card.ASSASSIN, Card.CAPTAIN);

        Mockito.doReturn(nextHand).when(agent).selectHand(any(Board.class), any(List.class));

        Board result = engine.processTurn(board, move, Collections.singletonMap("Player1", agent));

        Mockito.verify(agent);
        List<Card> actual = result.getPlayers().get("Player1").getOptions(new ArrayList<Card>(2));
        List<Card> expected = Arrays.asList(Card.ASSASSIN, Card.CAPTAIN);
        assert actual.equals(expected) : String.format("Hand: %1$s expected %2$s", actual, expected);
    }

    private Board newBoard(String ... names) {
        List<String> players = new ArrayList<>();
        for (String name : names) {
            players.add(name);
        }
        return Board.newGame(players);
    }
}
