package net.coup;

import net.coup.engine.Agent;
import net.coup.engine.Engine;
import net.coup.engine.EngineImpl;
import net.coup.agents.RandomAgent;
import net.coup.model.*;

import java.util.*;

public class App {
    public static void main( String[] args ) {
        List<String> names = Arrays.asList("Random", "RandomLegal");
        Map<String,Agent> agents = new HashMap<>();
        for (String name : names) {
            agents.put(name, new RandomAgent());
        }
        Board board = Board.newGame(names);
        System.out.println("New game:\n"+board);
        Engine engine = new EngineImpl();
        while (!board.isGameOver()) {
            for (String name : names) {
                Player player = board.getPlayers().get(name);
                if (!player.isAlive()) {
                    // This player is dead
                    continue;
                }
                Set<Action> validActions = getValidActions(player);
                List<Move> validMoves = getValidMoves(validActions, board, player);
                Agent myAgent = agents.get(name);
                Move move = myAgent.selectMove(validMoves, board, player);
                System.out.println(String.format("Move: "+move+"\n"));
                board = engine.processTurn(board, move, agents);
                System.out.println(board);
            }
        }
    }

    public static Set<Action> getValidActions(Player player) {
        List<Card> hand = new ArrayList<>(Constants.HAND_SIZE);
        hand = player.getOptions(hand);
        Set<Action> validActions = new HashSet<>();
        for (Action action : Action.values()) {
            if (    action.getCost() <= player.getCoins() &&
                    (action.getImplies() == null || hand.contains(action.getImplies()))) {
                validActions.add(action);
            }
        }
        return validActions;
    }

    public static List<Move> getValidMoves(Set<Action> actions, Board board, Player player) {
        List<Move> moves = new ArrayList<>();
        List<Player> others;
        for (Action action : actions) {
            switch (action) {
                case INCOME:
                case FOREIGN_AID:
                case TAX:
                case EXCHANGE:
                    moves.add(new Move(player.getName(), player.getName(), action));
                    break;
                case STEAL:
                    others = board.getOtherPlayers(player);
                    for (Player other : others) {
                        if (other.getCoins() > 0) {
                            moves.add(new Move(player.getName(), other.getName(), action));
                        }
                    }
                    break;
                case COUP:
                case ASSASSINATE:
                    others = board.getOtherPlayers(player);
                    for (Player other : others) {
                        if (other.getPublicCards().size() < Constants.HAND_SIZE) {
                            moves.add(new Move(player.getName(), other.getName(), action));
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("This should never be executed..."+action);
            }
        }
        return moves;
    }
}
