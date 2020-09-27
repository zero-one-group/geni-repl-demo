## Geni REPL Demo

This repository contains the script and data for the [Geni](https://github.com/zero-one-group/geni) REPL demo in the Scicloj event on the 26th of September 2020 (coming soon: link to the demo recording).

The script does some data cleaning, trains a collaborative-filtering model and recommends items for every member based on the descriptor word of their previous transactions.

Running the script:

1. [Install the Geni CLI](https://github.com/zero-one-group/geni#install-geni).
2. Start a REPL session with `geni`, then copy and paste the content of `final.clj` to the REPL.

Note that running `geni` the first time should download a ~230MB uberjar. This may take several minutes if you have a slow internet connection.
