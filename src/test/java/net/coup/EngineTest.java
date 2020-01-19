package net.coup;

import net.coup.engine.Agent;
import net.coup.engine.Engine;
import net.coup.engine.EngineImpl;
import net.coup.model.*;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

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
        int initialBoardSize = board.getCards().size();

        Mockito.doReturn(nextHand).when(agent).selectHand(any(Board.class), any(List.class));

        Board result = engine.processTurn(board, move, Collections.singletonMap("Player1", agent));

        Mockito.verify(agent).selectHand(any(Board.class), any(List.class));
        List<Card> actual = result.getPlayers().get("Player1").getOptions(new ArrayList<Card>(2));
        List<Card> expected = Arrays.asList(Card.ASSASSIN, Card.CAPTAIN);
        assert actual.equals(expected) : String.format("Hand: %1$s expected %2$s", actual, expected);
        assert initialBoardSize == result.getCards().size() : String.format("Court size should not change as a result of an exchange.");
    }

    @Test
    public void when_exchangeMoveTaken_given_playerHasOnlyOnePrivateCard_then_playerStillHasOnlyOneCard() {
        Board board = newBoard("Player1", "Player2");
        Move tax = new Move("Player2", "Player2", Action.TAX);
        Move assassinate = new Move("Player1", "Player1", Action.ASSASSINATE);
        Move exchange = new Move("Player1", "Player1", Action.EXCHANGE);
        Agent agent = Mockito.mock(Agent.class);
        Map<String,Agent> agents = Collections.singletonMap("Player1", agent);
        List<Card> hand = board.getPlayers().get("Player1").getOptions(new ArrayList<Card>(2));
        Card sacrafice = hand.get(0);
        Mockito.doReturn(sacrafice).when(agent).selectCardToSacrafice(any(Board.class), any(Player.class));
        Mockito.doReturn(Collections.singletonList(Card.CAPTAIN)).when(agent).selectHand(any(Board.class), any(List.class));

        Engine engine = new EngineImpl();

        Board funded = engine.processTurn(board, tax, agents);
        Board assassinated = engine.processTurn(funded, assassinate, agents);
        int preExchangeCourtSize = assassinated.getCards().size();
        Board exchanged = engine.processTurn(assassinated, exchange, agents);
        int postExchangeCourtSize = exchanged.getCards().size();

        assert preExchangeCourtSize == postExchangeCourtSize : String.format("Expected pre-exchange and post-exchange court size to match but (Pre: %1$d Post: %2$d)", preExchangeCourtSize, postExchangeCourtSize);
        assert exchanged.getPlayers().get("Player1").getPublicCards().contains(sacrafice) : "Expect the sacraficed card to remain unchanged";
        assert exchanged.getPlayers().get("Player1").getOptions(new ArrayList<Card>(2)).size() == 1 : String.format("Expected exchange to retain exactly one private card (actual : %1$d)", exchanged.getPlayers().get("Player1").getOptions(new ArrayList<Card>(2)).size());
        Mockito.verify(agent).selectCardToSacrafice(any(Board.class), any(Player.class));
    }

    @Test
    public void when_assassinated_then_playerCardBecomePublic() {
        Board board = newBoard("Player1", "Player2");
        Move move1 = new Move("Player1", "Player2", Action.INCOME);
        Move move2 = new Move("Player1", "Player2", Action.ASSASSINATE);
        Agent agent2 = Mockito.mock(Agent.class);
        Map<String, Agent> agents = new HashMap<>();
        agents.put("Player2", agent2);
        Engine engine = new EngineImpl();

        List<Card> targetsHand = board.getPlayers().get("Player2").getOptions(new ArrayList<Card>(2));
        Card sacrafice = targetsHand.get(0);

        Mockito.doReturn(sacrafice).when(agent2).selectCardToSacrafice(any(Board.class), any(Player.class));

        Board postFunding = engine.processTurn(board, move1, agents);
        int preAssassinPublicCards = postFunding.getPlayers().get("Player2").getPublicCards().size();
        Board postAssassin = engine.processTurn(postFunding, move2, agents);
        int postAssassinPublicCards = postAssassin.getPlayers().get("Player2").getPublicCards().size();

        assert preAssassinPublicCards == postAssassinPublicCards - 1 : "Expect a player to lose 1 card after being assassinated";
        assert postAssassin.getPlayers().get("Player1").getCoins() == 0 : String.format("Ensure the assassinating player paid for it (expected %1$d actual %2$d)", 0, postAssassin.getPlayers().get("Player1").getCoins());
        assert postAssassin.getPlayers().get("Player2").getPublicCards().contains(sacrafice) : "Expect the assassinated players card to become public";
        Mockito.verify(agent2).selectCardToSacrafice(any(Board.class), any(Player.class));
    }

    @Test
    public void when_coup_then_playerCardBecomesPublic() {
        Board board = newBoard("Player1", "Player2");
        Move taxMove = new Move("Player1", "Player1", Action.TAX);
        Move coupMove = new Move("Player1", "Player2", Action.COUP);
        Agent agent2 = Mockito.mock(Agent.class);
        Map<String, Agent> agents = Collections.singletonMap("Player2", agent2);

        List<Card> hand = board.getPlayers().get("Player2").getOptions(new ArrayList<Card>(2));
        Card sacrafice = hand.get(0);
        Mockito.doReturn(sacrafice).when(agent2).selectCardToSacrafice(any(Board.class), any(Player.class));

        Engine engine = new EngineImpl();
        Board postTax1 = engine.processTurn(board, taxMove, agents);
        Board postTax2 = engine.processTurn(postTax1, taxMove, agents);
        Board postCoup = engine.processTurn(postTax2, coupMove, agents);

        assert postCoup.getPlayers().get("Player1").getCoins() == 1 : String.format("Expected player 1 to have 1 gold after starting with 2, taking two tax moves and paying one coup move");
        Mockito.verify(agent2).selectCardToSacrafice(any(Board.class), any(Player.class));
        assert postCoup.getPlayers().get("Player2").getPublicCards().contains(sacrafice) : "Expected sacraficed card to become a public card for the target player";
        assert postCoup.getPlayers().get("Player2").getOptions(new ArrayList<Card>(2)).size() == 1 : String.format("Expected 1 card to be kept private, but actual private card count is %1$d", postCoup.getPlayers().get("Player2").getOptions(new ArrayList<Card>(2)).size());
    }

    private Board newBoard(String ... names) {
        List<String> players = new ArrayList<>();
        for (String name : names) {
            players.add(name);
        }
        return Board.newGame(players);
    }
}
