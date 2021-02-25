#!/usr/bin/env bash

#
# Copyright (C) 2021 Josh Walters
# This file is part of crom.
#
# crom is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# crom is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with crom.  If not, see <https://www.gnu.org/licenses/>.
#

generate_opening_analysis() {
  OUT_FILE="openings/$1 - $2.pgn"

  # Skip if already exists
  if test -f "$OUT_FILE"; then
    echo "Opening $1 for $2 already exists, skipping."
    return 1
  fi

  echo "Opening prefix: '$1'"
  echo "Color perspective: $2"

  java -jar build/libs/crom-*.jar \
    --game-limit=50000 \
    --half-move-depth=30 \
    --cutoff-threshold=10 \
    --min-rating=2400 \
    "$1" \
    $2 \
    games \
    "$OUT_FILE"
}

generate_both_opening_analysis() {
  generate_opening_analysis "$1" white
  generate_opening_analysis "$1" black
}

# Generate openings folder
mkdir openings

# Generate openings
#generate_both_opening_analysis "Alekhine Defense"
#generate_both_opening_analysis "Amar Gambit"
#generate_both_opening_analysis "Amazon Attack"
#generate_both_opening_analysis "Australian Defense"
#generate_both_opening_analysis "Barnes Defense"
#generate_both_opening_analysis "Benko Gambit"
#generate_both_opening_analysis "Benoni Defense"
#generate_both_opening_analysis "Blackburne Shilling Gambit"
#generate_both_opening_analysis "Blackmar-Diemer Gambit"
#generate_both_opening_analysis "Blackmar Gambit"
#generate_both_opening_analysis "Blumenfeld Countergambit"
#generate_both_opening_analysis "Bogo-Indian Defense"
#generate_both_opening_analysis "Bongcloud Attack"
#generate_both_opening_analysis "Borg Defense"
#generate_both_opening_analysis "Bronstein Gambit"
# generate_both_opening_analysis "Caro-Kann Defense"
#generate_both_opening_analysis "Carr Defense"
#generate_both_opening_analysis "Colle System"
#generate_both_opening_analysis "Creepy Crawly Formation"
#generate_both_opening_analysis "Czech Defense"
#generate_both_opening_analysis "Danish Gambit"
#generate_both_opening_analysis "Duras Gambit"
#generate_both_opening_analysis "Dutch Defense"
#generate_both_opening_analysis "East Indian Defense"
#generate_both_opening_analysis "Elephant Gambit"
#generate_both_opening_analysis "English Defense"
#generate_both_opening_analysis "Englund Gambit"
generate_both_opening_analysis "French Defense"
#generate_both_opening_analysis "Fried Fox Defense"
#generate_both_opening_analysis "Giuoco Piano"
#generate_both_opening_analysis "Goldsmith Defense"
#generate_both_opening_analysis "Grünfeld Defense"
#generate_both_opening_analysis "Guatemala Defense"
#generate_both_opening_analysis "Gunderam Defense"
#generate_both_opening_analysis "Hippopotamus Defense"
#generate_both_opening_analysis "Horwitz Defense"
#generate_both_opening_analysis "Kangaroo Defense"
#generate_both_opening_analysis "King's Gambit"
#generate_both_opening_analysis "King's Indian Attack"
#generate_both_opening_analysis "King's Indian Defense"
#generate_both_opening_analysis "King's Pawn"
#generate_both_opening_analysis "Latvian Gambit"
#generate_both_opening_analysis "Lion Defense"
# generate_both_opening_analysis "London System"
#generate_both_opening_analysis "Marienbad System"
#generate_both_opening_analysis "Mexican Defense"
#generate_both_opening_analysis "Mikenas Defense"
#generate_both_opening_analysis "Modern Defense"
#generate_both_opening_analysis "Montevideo Defense"
#generate_both_opening_analysis "Neo-Grünfeld Defense"
#generate_both_opening_analysis "Nimzo-Indian"
#generate_both_opening_analysis "Nimzo-Larsen Attack"
#generate_both_opening_analysis "Nimzowitsch Defense"
#generate_both_opening_analysis "Nimzowitsch-Larsen Attack"
#generate_both_opening_analysis "Norwegian Defense"
#generate_both_opening_analysis "Old Indian Defense"
#generate_both_opening_analysis "Owen Defense"
#generate_both_opening_analysis "Paleface Attack"
#generate_both_opening_analysis "Philidor Defense"
#generate_both_opening_analysis "Pirc Defense"
#generate_both_opening_analysis "Polish Defense"
#generate_both_opening_analysis "Pterodactyl Defense"
# generate_both_opening_analysis "Queen's Gambit"
#generate_both_opening_analysis "Queen's Indian"
#generate_both_opening_analysis "Queen's Pawn"
#generate_both_opening_analysis "Rapport-Jobava System"
#generate_both_opening_analysis "Rat Defense"
#generate_both_opening_analysis "Richter-Veresov Attack"
#generate_both_opening_analysis "Robatsch Defense"
# generate_both_opening_analysis "Ruy Lopez"
#generate_both_opening_analysis "Scandinavian Defense"
# generate_both_opening_analysis "Semi-Slav Defense"
# generate_both_opening_analysis "Sicilian Defense"
# generate_both_opening_analysis "Slav Defense"
#generate_both_opening_analysis "Slav Indian"
#generate_both_opening_analysis "Sodium Attack"
#generate_both_opening_analysis "St. George Defense"
#generate_both_opening_analysis "Tarrasch Defense"
#generate_both_opening_analysis "Torre Attack"
#generate_both_opening_analysis "Trompowsky Attack"
#generate_both_opening_analysis "Vulture Defense"
#generate_both_opening_analysis "Wade Defense"
#generate_both_opening_analysis "Ware Defense"
#generate_both_opening_analysis "Yusupov-Rubinstein System"
#generate_both_opening_analysis "Zukertort"
