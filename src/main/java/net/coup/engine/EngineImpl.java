package net.coup.engine;

import net.coup.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.coup.model.Constants.EMPTY_STRING;
import static net.coup.model.Constants.HAND_SIZE;

public class EngineImpl implements Engine {
    @Override
    public boolean isValid(Board board, Move move) {
        boolean valid = false;
        switch(move.getAction()) {
            case INCOME:
            case FOREIGN_AID:
            case EXCHANGE:
            case TAX:
                valid = true;
                break;
            case COUP:
            case ASSASSINATE:
            case STEAL:
                Player source = board.getPlayers().get(move.getSource());
                Player target = board.getPlayers().get(move.getTarget());
                valid = source.getCoins() >= move.getAction().getCost() &&
                        source.isAlive() && target.isAlive();
                break;
        }
        return valid;
    }

    @Override
    public Board processTurn(Board board, Move move, Map<String, Agent> agents) {
        // Process politics
        if (move.isChallengable()) {
            String challenger = getChallengers(board, move, agents);
            if (challenger.length() > 0) {
                Player source = board.getPlayers().get(move.getSource());
                List<Card> hand = source.getOptions(new ArrayList<Card>(2));
                if (hand.contains(move.getAction().getImplies())) {
                    board = loseCard(board, board.getPlayers().get(challenger), agents.get(challenger));
                } else {
                    board = loseCard(board, board.getPlayers().get(move.getSource()), agents.get(move.getSource()));
                    return board;
                }
            }
        }
        if (move.isBlockable()) {
            String blocker = getBlockers(board, move, agents);
            if (blocker.length() > 0) {
                if (challengeBlock(blocker, board, move, agents)) {
                    List<Card> hand = board.getPlayers().get(blocker).getOptions(new ArrayList<Card>(2));
                    boolean validBlock = false;
                    for (Card blockingCard : move.getAction().getBlocks()) {
                        if (hand.contains(blockingCard)) {
                            validBlock = true;
                            break;
                        }
                    }
                    if (validBlock) {
                        board = loseCard(board, board.getPlayers().get(move.getSource()), agents.get(move.getSource()));
                        return board;
                    } else {
                        board = loseCard(board, board.getPlayers().get(blocker), agents.get(blocker));
                    }
                } else {
                    return board;
                }
            }
        }
        // Process move
        switch(move.getAction()) {
            case INCOME:
                board = processIncome(board, move, agents);
                break;
            case FOREIGN_AID:
                board = processForeignAid(board, move, agents);
                break;
            case TAX:
                board = processTax(board, move,agents);
                break;
            case EXCHANGE:
                board = processExchange(board, move, agents);
                break;
            case COUP:
                board = processCoup(board, move, agents);
                break;
            case STEAL:
                board = processSteal(board, move, agents);
                break;
            case ASSASSINATE:
                board = processAssassinate(board, move, agents);
                break;
            default:
                throw new IllegalStateException("This should never be executed!");
        }
        return board;
    }

    private Board processIncome(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        source = source.setCoins(source.getCoins() + move.getAction().getIncome());
        return board.replacePlayer(source);
    }

    private Board processForeignAid(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        source = source.setCoins(source.getCoins() + Constants.FOREIGN_AID_AMOUNT);
        return board.replacePlayer(source);
    }

    private Board processTax(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        source = source.setCoins(source.getCoins() + Constants.TAX_AMOUNT);
        return board.replacePlayer(source);
    }

    private Board processExchange(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        int initialBoardSize = board.getCards().size();
        List<Card> newCards = board.getExchangeCards();
        List<Card> options = new ArrayList<>(newCards.size()+HAND_SIZE);
        options.addAll(newCards);
        options = source.getOptions(options);
        Agent agent = agents.get(move.getSource());
        List<Card> newHand = agent.selectHand(board, options);
        options.removeAll(newHand);
        board.returnExchangeCards(newCards); //TODO: Make an immutable version
        source = source.setHand(newHand);
        Board result = board.replacePlayer(source);
        assert initialBoardSize == result.getCards().size() : String.format("Board size should not change (Previous %1$d; Actual %2$d)", initialBoardSize, result.getCards().size());
        return result;
    }

    private Board processCoup(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        source = source.setCoins(source.getCoins() - Constants.COUP_COIN_COST);
        board = board.replacePlayer(source);
        Player target = board.getPlayers().get(move.getTarget());
        Agent agent = agents.get(move.getTarget());
        return loseCard(board, target, agent);
    }

    private Board processSteal(Board board, Move move, Map<String, Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        Player target = board.getPlayers().get(move.getTarget());
        int amount = Math.min(target.getCoins(), Constants.STEAL_AMOUNT);
        source = source.setCoins(source.getCoins() + amount);
        target = target.setCoins(target.getCoins() - amount);
        board = board.replacePlayer(source);
        return board.replacePlayer(target);
    }

    private Board processAssassinate(Board board, Move move, Map<String,Agent> agents) {
        Player source = board.getPlayers().get(move.getSource());
        source = source.setCoins(source.getCoins() - Constants.ASSASSINATION_COIN_COST);
        board = board.replacePlayer(source);
        Agent targetAgent = agents.get(move.getTarget());
        Player target = board.getPlayers().get(move.getTarget());
        return loseCard(board, target, targetAgent);
    }

    private String getChallengers(Board board, Move move, Map<String, Agent> agents) {
        //TODO: Check challenges in player order
        for (Map.Entry<String,Agent> entry : agents.entrySet()) {
            if (entry.getKey().equals(move.getSource())) {
                // Never challenge oneself!
                continue;
            }
            Agent agent = entry.getValue();
            if (agent.challengeMove(board, move)) {
                return entry.getKey();
            }
        }
        return EMPTY_STRING;
    }

    private String getBlockers(Board board, Move move, Map<String,Agent> agents) {
        //TODO: Check blocks in player order
        for (Map.Entry<String, Agent> entry : agents.entrySet()) {
            if (entry.getKey().equals(move.getSource())) {
                // Never block oneself!
                continue;
            }
            Agent agent = entry.getValue();
            if (agent.blockMove(board, move)) {
                return entry.getKey();
            }
        }
        return EMPTY_STRING;
    }

    private boolean challengeBlock(String blocker, Board board, Move move, Map<String, Agent> agents) {
        Agent myself = agents.get(move.getSource());
        return myself.challengeBlock(blocker, board, move);
    }

    private Board loseCard(Board board, Player player, Agent agent) {
        Card sacrafice = agent.selectCardToSacrafice(board, player);
        Player replacement = player.removeCardFromHand(sacrafice);
        return board.replacePlayer(replacement);
    }
}
