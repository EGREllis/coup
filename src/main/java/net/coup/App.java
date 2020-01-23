package net.coup;

import net.coup.agents.RandomLegalAgent;
import net.coup.agents.RationalLegalAgent;
import net.coup.engine.Agent;
import net.coup.agents.RandomAgent;
import net.coup.model.*;

import java.util.*;

public class App {
    public static void main( String[] args ) {
        List<Card> values = new ArrayList<>(Card.values().length);
        values.addAll(Arrays.asList(Card.ASSASSIN, Card.CONTESSA, Card.CAPTAIN, Card.DUKE, Card.AMBASSADOR));
        Map<String,Agent> agents = new HashMap<>();
        agents.put("Random", new RandomAgent());
        agents.put("RandomLegalAgent", new RandomLegalAgent());
        agents.put("RationalLegalAgent", new RationalLegalAgent(values));
        List<String> names = new ArrayList<>();
        names.addAll(agents.keySet());
        Board board = Board.newGame(names);

        Map<String,Integer> tally = new HashMap<>();
        int max = 1000000;
        for (int i = 0; i < max; i++) {
            Game game = new Game(board, agents);
            Board result;
            try {
                result = game.call();
                for (Player player : result.getPlayers().values()) {
                    if (player.getPublicCards().size() < 2) {
                        if (tally.containsKey(player.getName())) {
                            int count = tally.get(player.getName());
                            tally.put(player.getName(), count+1);
                        } else {
                            tally.put(player.getName(), 1);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (i % 10000 == 0) {
                System.out.println(String.format("Finished game %1$d of %2$d", i, max));
            }
        }
        System.out.println("Agent:\tWinnings");
        for (Map.Entry<String,Integer> entry : tally.entrySet()) {
            System.out.println(String.format("%1$s\t%2$d", entry.getKey(), entry.getValue()));
        }
    }
}
