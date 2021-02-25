/*
 * Copyright (C) 2021 Josh Walters
 * This file is part of crom.
 *
 * crom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * crom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with crom.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.joshwalters.chess.crom;

import java.util.*;

public class MoveNode implements Comparable<MoveNode> {
    private static Map<Long, MoveNode> positionLookup;
    private final String move;
    private final Map<String, MoveNode> children;
    private final Set<String> openingVariation;
    private final Color nextTurnToMove;
    private long count;

    public MoveNode(String move, String openingVariation, Color nextTurnToMove) {
        this.move = move;
        this.children = new HashMap<>();
        this.count = 1;
        this.nextTurnToMove = nextTurnToMove;
        // Include opening variation name
        this.openingVariation = new HashSet<>();
        this.openingVariation.add(openingVariation);

        // Create the position lookup, if null
        if (positionLookup == null) {
            positionLookup = new HashMap<>();
        }
    }

    public Color getNextTurnToMove() {
        return nextTurnToMove;
    }

    private void getMoveText(MoveNode moveNode, int moveNum, boolean isWhitesTurn, StringBuilder stringBuilder, long totalGames, boolean showVariationName) {
        if (moveNode == null) {
            return;
        }

        if (!isWhitesTurn) {
            moveNum++;
        }

        // Sort children moves, based on frequency
        List<MoveNode> sortedChildren = new ArrayList<>(moveNode.getChildren().values());
        Collections.sort(sortedChildren);

        // Print children
        boolean firstChild = true;
        MoveNode mainline = null;

        if (showVariationName && moveNode.openingVariation.size() == 1) {
            showVariationName = false;
            stringBuilder.append(String.format("{[Variation: '%s']} ", moveNode.openingVariation.iterator().next()));
        }

        for (MoveNode line : sortedChildren) {
            if (firstChild) {
                // Special logic for "mainline"
                firstChild = false;
                mainline = line;
                printMove(moveNum, isWhitesTurn, false, mainline, stringBuilder, totalGames);
            } else {
                // Handle variations
                stringBuilder.append("( ");
                printMove(moveNum, isWhitesTurn, true, line, stringBuilder, totalGames);
                getMoveText(line, moveNum, !isWhitesTurn, stringBuilder, totalGames, showVariationName);
                stringBuilder.append(") ");
            }
        }
        getMoveText(mainline, moveNum, !isWhitesTurn, stringBuilder, totalGames, showVariationName);
        return;
    }

    private void printMove(int moveNum, boolean isWhitesTurn, boolean firstInRAV, MoveNode move, StringBuilder stringBuilder, long totalGames) {
        if (isWhitesTurn) {
            stringBuilder.append(String.format("%d.%s ", moveNum, move.getMove()));
        } else {
            if (firstInRAV) {
                stringBuilder.append(String.format("%d...%s ", moveNum, move.getMove()));
            } else {
                stringBuilder.append(String.format("%s ", move.getMove()));
            }
        }
    }

    public StringBuilder getMoveText(boolean showVariationName) {
        StringBuilder stringBuilder = new StringBuilder();
        getMoveText(this, 1, true, stringBuilder, getCount(), showVariationName);
        return stringBuilder;
    }

    public Map<String, MoveNode> getChildren() {
        return children;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getMove() {
        return move;
    }

    public void incrementCount() {
        count++;
    }

    public void addOpeningVariation(String openingVariation) {
        this.openingVariation.add(openingVariation);
    }

    public MoveNode addChild(String move, String openingVariation, long boardHashCode) {
        // Check if new position is in lookup table
        if (positionLookup.containsKey(boardHashCode)) {
            MoveNode result = positionLookup.get(boardHashCode);
            result.incrementCount();
            result.addOpeningVariation(openingVariation);
            return result;
        }

        // Update child, if present
        if (children.containsKey(move)) {
            MoveNode result = children.get(move);
            result.incrementCount();
            result.addOpeningVariation(openingVariation);

            // Add to position lookup
            positionLookup.put(boardHashCode, result);

            return result;
        }

        // Else, create new child
        MoveNode result = new MoveNode(move, openingVariation, (this.nextTurnToMove == Color.white) ? Color.black : Color.white);
        children.put(move, result);
        // Add to position lookup
        positionLookup.put(boardHashCode, result);
        return result;
    }

    @Override
    public int compareTo(MoveNode o) {
        // Have to swap ordering to take largest
        return Long.compare(o.getCount(), this.getCount());
    }
}
