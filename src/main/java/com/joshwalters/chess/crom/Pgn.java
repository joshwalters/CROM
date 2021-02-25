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

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Event;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameFactory;
import com.github.bhlangonijr.chesslib.game.GenericPlayer;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import com.github.bhlangonijr.chesslib.util.LargeFile;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Pgn {
    static void pruneMoveTree(MoveNode moveNode, long cutoffThreshold, Color openingColor) {
        if (moveNode == null || moveNode.getChildren() == null || moveNode.getChildren().isEmpty()) {
            return;
        }

        // Only keep the "best" (most frequent) move for the given opening color
        if (moveNode.getNextTurnToMove() == openingColor) {
            List<MoveNode> sortedChildren = new ArrayList<>(moveNode.getChildren().values());
            Collections.sort(sortedChildren);
            MoveNode keptMove = sortedChildren.get(0);

            moveNode.getChildren().clear();
            moveNode.getChildren().put(keptMove.getMove(), keptMove);
        } else {
            // Remove infrequent lines
            moveNode.getChildren().entrySet().removeIf(entry -> entry.getValue().getCount() < cutoffThreshold);
        }

        // Perform for all children
        for (String moveName : moveNode.getChildren().keySet()) {
            pruneMoveTree(moveNode.getChildren().get(moveName), cutoffThreshold, openingColor);
        }
    }

    static PgnIterator getGamesFromPgn(String pgnFilename) {
        PgnIterator games = null;
        try {
            games = new PgnIterator(new LargeFile(pgnFilename));
        } catch (FileNotFoundException e) {
            System.out.println(String.format("ERROR: File not found '{}'", pgnFilename));
            System.exit(1);
        } catch (Exception e) {
            System.out.println("ERROR: Unknown exception.");
            e.printStackTrace();
            System.exit(1);
        }
        return games;
    }

    static Game getGameFromText(String openingName, StringBuilder moveText) {
        Event event = GameFactory.newEvent(String.format("%s Opening", openingName));
        event.setSite("CROM: Chess Repertoire Opening Miner, https://github.com/joshwalters/crom");
        event.setStartDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
        Game game = new Game(null, GameFactory.newRound(event, 1));
        GenericPlayer whitePlayer = new GenericPlayer();
        whitePlayer.setName("White");
        GenericPlayer blackPlayer = new GenericPlayer();
        blackPlayer.setName("Black");
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setPlyCount("-");
        game.setMoveText(moveText);
        return game;
    }

    static void parseGamesIntoMoveTree(MoveNode root, PgnIterator games, String openingPrefix, int minStrength, int gameLimit, int maxDepth) {
        // Build the move tree from all matching games
        int lastUpdateTime = LocalDateTime.now().toLocalTime().toSecondOfDay();
        int secondsBeforeUpdate = 3;

        for (Game game : games) {
            if (root.getCount() >= gameLimit) {
                break;
            }

            int currentSeconds = LocalDateTime.now().toLocalTime().toSecondOfDay();
            if (currentSeconds - lastUpdateTime >= secondsBeforeUpdate) {
                double percentComplete = (double) root.getCount() / gameLimit * 100;
                System.out.println(String.format("Processed %d of %d games, %.0f%% complete...", root.getCount(), gameLimit, percentComplete));
                lastUpdateTime = currentSeconds;
            }

            if (game.getOpening().toLowerCase(Locale.ROOT).startsWith(openingPrefix.toLowerCase(Locale.ROOT))) {
                if (game.getHalfMoves().size() > maxDepth) {
                    if (game.getBlackPlayer().getElo() >= minStrength && game.getWhitePlayer().getElo() >= minStrength) {
                        root.incrementCount();
                        root.addOpeningVariation(game.getOpening());

                        MoveNode curNode = root;
                        int depth = 0;
                        Board board = new Board();
                        for (Move move : game.getCurrentMoveList()) {
                            board.doMove(move);
                            if (depth > maxDepth) {
                                break;
                            }
                            depth++;
                            curNode = curNode.addChild(move.getSan(), game.getOpening(), board.getZobristKey());
                        }
                    }
                }
            }
        }
    }
}
