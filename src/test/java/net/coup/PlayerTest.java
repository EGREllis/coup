package net.coup;

import net.coup.model.Card;
import net.coup.model.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerTest {
    @Test
    public void when_coinsSet_then_getCoinsReturnsNewValue() {
        List<Card> privateCards = new ArrayList<>();
        List<Card> publicCards = new ArrayList<>();
        Player player = new Player(privateCards, publicCards, "Fred", 0);
        Player player2 = player.setCoins(1);
        assert player2.getCoins() == 1;
    }
}
