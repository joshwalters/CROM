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

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "crom",
        mixinStandardHelpOptions = true,
        version = "crom 1.0",
        description = "Chess Repertoire Opening Miner.")
public class Main implements Callable<Integer> {
    @Option(names = {"-r", "--min-rating"}, description = "Minimum player rating for filtering games (default: ${DEFAULT-VALUE})")
    int minRating = 2400;
    @Option(names = {"-g", "--game-limit"}, description = "Maximum limit for games to include (default: ${DEFAULT-VALUE})")
    int gameLimit = 1000;
    @Option(names = {"-d", "--half-move-depth"}, description = "Half-move depth (default: ${DEFAULT-VALUE})")
    int maxHalfmoveDepth = 30;
    @Option(names = {"-c", "--cutoff-threshold"}, description = "Infrequent line threshold for removal. (default: ${DEFAULT-VALUE})")
    long cutoffThreshold = 5;
    @Option(names = "--pray", hidden = true)
    boolean pray = false;
    @Option(names = {"-v", "--verbose"}, description = "Print additional information while processing (default: ${DEFAULT-VALUE})")
    boolean verbose = false;
    @Option(names = "--show-variation", description = "Include opening variation in output (default: ${DEFAULT-VALUE})")
    boolean showVariation = false;
    @Parameters(index = "1", description = "The color used for the opening study.")
    Color openingColor;
    @Parameters(index = "0", description = "The opening name prefix to filter for. Eg: 'French Defense'")
    private String openingName;
    @Parameters(index = "2", description = "The input PGN file of games to mine openings from.")
    private String inputPgnFileOrFolder;
    @Parameters(index = "3", description = "The output PGN file to write to.")
    private String outputPgnFile;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (pray) {
            return prayer();
        }

        if (cutoffThreshold <= 0) {
            System.out.println(String.format("Invalid infrequent line threshold setting: %d", cutoffThreshold));
            return 1;
        }

        System.out.println(String.format("Starting analysis..."));
        MoveNode root = new MoveNode("root", openingName, Color.white);
        root.setCount(0);

        List<File> inputFiles = new ArrayList<>();
        File input = new File(inputPgnFileOrFolder);
        if (input.isFile()) {
            // Process just one file
            inputFiles.add(input);
        } else {
            for (File f : input.listFiles()) {
                if (f.toString().endsWith(".pgn")) {
                    inputFiles.add(f);
                }
            }
        }

        if (inputFiles.isEmpty()) {
            System.out.println("ERROR: No input PGN files found.");
            System.exit(1);
        }

        if (verbose) {
            for (File inputFile : inputFiles) {
                System.out.println(String.format("Added input file: %s", inputFile.getAbsolutePath()));
            }
        }

        for (File inputFile : inputFiles) {
            PgnIterator games = Pgn.getGamesFromPgn(inputFile.getAbsolutePath());
            try {
                Pgn.parseGamesIntoMoveTree(root, games, openingName, minRating, gameLimit, maxHalfmoveDepth);
            } catch (PgnException e) {
                System.out.println(String.format("Error reading input PGN file: %s", e.getCause().getMessage()));
                System.exit(1);
            }
        }

        // Prune the tree to keep only opening information
        Pgn.pruneMoveTree(root, cutoffThreshold, openingColor);

        // Build the game data as PGN
        StringBuilder moveText = root.getMoveText(showVariation);
        Game game = Pgn.getGameFromText(openingName, moveText);

        // Write the PGN file
        Path path = Paths.get(outputPgnFile);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(game.toPgn(false, false));
        } catch (Exception e) {
            System.out.println(String.format("Error writing to file: %s", outputPgnFile));
            return 1;
        }

        System.out.println(String.format("Done, file has been written to: %s", outputPgnFile));
        return 0;
    }

    private int prayer() {
        System.out.println("\t\"Crom, I have never prayed to you before. I have no tongue for it. No");
        System.out.println("\tone, not even you, will remember if we were good men or bad. Why we");
        System.out.println("\tfought, and why we died. All that matters is that today, two stood");
        System.out.println("\tagainst many. Valor pleases you, so grant me this one request. Grant");
        System.out.println("\tme revenge! And if you do not listen, the HELL with you!\"");
        return 42;
    }
}
