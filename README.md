# gargamel

You have all these little blue issues on github, jira and commits running around and you want to catch them! Gargamel is really enthusiastic to do just that but he never really succeeds, does he?

![alt tag](pics/gargamel.jpg)

## Usage

FIXME: Use this for user-level plugins:

Put `[gargamel "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
gargamel 0.1.0-SNAPSHOT`.

If you want to generate custom changelog for Release Candidate provide to refs (commits or tags on the command line:

    $ lein gargamel ba2bf9769cca3d4f4452ae770f6807db52b1e5a0 4adf1fe853d9c082346cd4b029f1c868abb9d663

On the other hand if you want to generate the latest changelog between the two latest release tags:

    $lein gargamel-lr

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
