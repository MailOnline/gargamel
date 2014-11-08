# gargamel

You have all these little blue issues on github, jira and commits running around and you want to catch them! Gargamel is really enthusiastic to do just that but he never really succeeds, does he?

![alt tag](pics/gargamel.jpg)

## Usage

There are two interfaces for gargamel: either use the lein plugin for clojure projects managed by leiningen or use the CLI interface for all other projects.

Note that currently gargamel expects release tags in the following format: `release-buildnumber-date_time` matching the pattern `release-\d+-\d+_\d+`.


### Leiningen plugin

Put `[gargamel "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
gargamel 0.1.0-SNAPSHOT`.

If you want to generate custom changelog for Release Candidate provide two refs (commits or tags) on the command line:

    $ lein gargamel ba2bf9769cca3d4f4452ae770f6807db52b1e5a0 4adf1fe853d9c082346cd4b029f1c868abb9d663

On the other hand if you want to generate the latest changelog between the two latest release tags:

    $ lein gargamel-lr

### CLI interface

To install clone the github project and put gargamel/bin on your PATH. Run

    $ gargamel.sh -h

for built in help. You can generate custom changelog for Release Candidate provide two refs (tags or commits):

    $ gargamel.sh -f 4adf1fe853d9c082346cd4b029f1c868abb9d663 -t 50848867e66d6462c3af7427dbdf54f6553d12c0 -p clj_fe -d /tmp/

Or to generate the latest releaase notes changelog between the two latest releaase tags:

    $ gargamel.sh -v -r -p clj_fe -d /tmp/

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
