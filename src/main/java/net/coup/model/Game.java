package net.coup.model;

import net.coup.engine.Agent;
import net.coup.engine.Engine;
import net.coup.engine.EngineImpl;

import java.util.*;
import java.util.concurrent.Callable;

public class Game implements Callable<Board> {
    private Board board;
    private Map<String,Agent> agents;

    public Game(Board board, Map<String,Agent> agents) {
        this.board = board;
        this.agents = agents;
    }

    @Override
    public Board call() throws Exception {
        Engine engine = new EngineImpl();
        while (!board.isGameOver()) {
            for (String name : board.getPlayers().keySet()) {
                Player player = board.getPlayers().get(name);
                if (!player.isAlive()) {
                    // Dead players take no actions...
                    continue;
                }
                Set<Action> allActions = getAllActions(player);
                List<Move> moves = getMoves(allActions, board, player);
                Agent myAgent = agents.get(name);
                Move move = myAgent.selectMove(moves, board, player);
                board = engine.processTurn(board, move, agents);
            }
        }
        return board;
    }

    public Set<Action> getAllActions(Player player) {
        List<Card> hand = player.getOptions(new ArrayList<Card>(Constants.HAND_SIZE));
        Set<Action> validActions = new HashSet<>();
        for (Action action : Action.values()) {
            if (    action.getCost() <= player.getCoins() &&
                    (action.getImplies() == null || hand.contains(action.getImplies()))) {
                validActions.add(action);
            }
        }
        return validActions;
    }

    public List<Move> getMoves(Set<Action> actions, Board board, Player player) {
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
                    others = board.getLiveOpponentsOf(player.getName());
                    for (Player other : others) {
                        if (other.getCoins() > 0) {
                            moves.add(new Move(player.getName(), other.getName(), action));
                        }
                    }
                    break;
                case COUP:
                case ASSASSINATE:
                    if (player.getCoins() < action.getCost()) {
                        break;
                    }
                    others = board.getLiveOpponentsOf(player.getName());
                    for (Player other : others) {
                        if (other.isAlive()) {
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
