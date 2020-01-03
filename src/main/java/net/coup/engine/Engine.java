package net.coup.engine;

import net.coup.model.Board;
import net.coup.model.Move;

import java.util.Map;

public interface Engine {
    Board processTurn(Board board, Move move, Map<String, Agent> agents);
}
