![CROM Logo](./assets/crom_logo.png)

# CROM - Chess Repertoire Opening Miner

CROM generates chess opening repertoires from a PGN database file.

These opening repertoires can be used to help in the study or memorization of opening lines in chess. For example, if
you wanted to study the [French Defense](https://en.wikipedia.org/wiki/French_Defence), you would have to manually
create a PGN containing the various lines of the defense. CROM can automate much of this process, and generate an
opening repertoire PGN for you automatically. This repertoire may not be perfect, but it should provide a start.

These opening repertoire PGN files can also be used for spaced repetition memorization
on [listudy](https://listudy.org/en).

## Usage

Download a release jar, or build with:

```bash
./gradlew clean build jar
```

The jar can be found in `./build/libs/crom-*.jar`

### Print help

```
java -jar crom-1.0.jar -h
```

### Example usage

Here is an example command:

```bash
java -jar crom-1.0.jar \
  --game-limit=50000 \
  --half-move-depth=30 \
  --cutoff-threshold=5 \
  --min-rating=2400 \
  "French Defense" \
  black \
  input.pgn \
  french-defense-black.pgn
```

This does the following:

- It will search for up to 50,000 games (`game-limit`).
- It will limit to the first 30 half-moves for each selected game (`half-move-depth`).
- Positions that occur less than 5 times (`cutoff-threshold`), will not be included in the output.
- It will limit games where both players had a rating above 2400 (`min-rating`).
- Only games that have an opening tag that starts with `French Defense` will be selected.
- The opening will be generated from blacks perspective (only the "best" move for black will be selected).
- It will read games from `input.pgn`.
- It will write the resulting opening analysis to `french-defense-black.pgn`.

## Free PGN game collections with opening annotations

The following are collections of games in PGN format that can be used out-of-the-box with CROM:

- [https://database.lichess.org/](https://database.lichess.org/)
    - Monthly dump of the games played on Lichess.
    - The games are annotated with the openings and are ready for use with CROM.
- [https://database.nikonoel.fr/](https://database.nikonoel.fr/)
    - Monthly dump of the games played on Lichess, filtering out all games played by under 2400 rated players.
    - The games are annotated with the openings and are ready for use with CROM.

# Example Repertoires

A few openings have already been generated and included in this repo, they are in the `openings` folder.

These opening studies have also been uploaded to [listudy](https://listudy.org/en/profile/CROM), where you can use
spaced repetition to memorize the opening lines.

# Create your own

Make a folder `games`, and put the input PGNs you want to use. The games
from [https://database.nikonoel.fr/](https://database.nikonoel.fr/) are recomended.

Then modify `generate.sh` to include the opening you want to generate for, then run `generate.sh`. You must have first
built the project JAR with:

```bash
./gradlew clean build jar
```

# License

CROM is licensed under GPL version 3.

See `LICENSE.txt` for details.

CROM uses the following libraries:

- https://github.com/bhlangonijr/chesslib
- https://commons.apache.org/
- https://picocli.info/