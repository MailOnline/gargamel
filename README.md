[![Build Status](https://travis-ci.org/MailOnline/gargamel.png?branch=master)](https://travis-ci.org/MailOnline/gargamel)

# gargamel

You have all these little blue issues on github, jira and commits running around and you want to catch them! Gargamel is really enthusiastic to do just that but he never really succeeds, does he?

![alt tag](pics/gargamel.jpg)

## Usage

There are two interfaces for gargamel: either use the lein plugin for clojure projects managed by leiningen or use the CLI interface for all other projects.

### Prerequisites

java runtime environment: basically `java -jar` should work.

Gargamel also does not handle login/auth to github/bitbucket it just shells out so the shell gargamel runs in should be 'authenticated' to access the git repos you want to work with.

### Leiningen plugin

Put `[gargamel "0.5.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
gargamel 0.5.0`.

If you want to generate custom changelog for Release Candidate provide two refs (commits or tags) on the command line:

    $ lein gargamel ba2bf9769cca3d4f4452ae770f6807db52b1e5a0 4adf1fe853d9c082346cd4b029f1c868abb9d663

On the other hand if you want to generate the latest changelog between the two latest release tags:

    $ lein gargamel-lr

### CLI interface

Download `gargamel-latest.tgz` binary from [latest release](https://github.com/MailOnline/gargamel/releases/latest) and untar it in a directory on your PATH. Run

    $ gargamel.sh -h

for help. You can generate custom changelog for Release Candidate providing two refs (tags or commits):

    $ gargamel.sh -f 4adf1fe853d9c082346cd4b029f1c868abb9d663 -t 50848867e66d6462c3af7427dbdf54f6553d12c0 -d /tmp/

Or to generate the latest release notes changelog between the two latest release tags:

    $ gargamel.sh -v -r -d /tmp/

Note that by default gargamel expects release tags in the following format: `release-buildnumber-date_time` matching the pattern `release-\d+-\d+_\d+`. This is however configurable with `-x` or `--releate-tag-pattern`

## Project specific config file

Gargamel looks for a file named `gargamel.edn` in the project's directory. This file needs to be in [edn format](https://github.com/edn-format/edn).

Please see example config files:
- [config file](gargamel.edn) for markdown output, please see **comments** for explanation of the config file format
- [config file](gargamel-html.edn) for html output

Other output formats are supported by the appropriate set of templates and setting the `:output-extension` in `gargamel.edn` to the appropriate extension.

For example markdown output see gargamel's own release notes since [0.5.0 release](https://github.com/MailOnline/gargamel/releases/tag/release-050-20150113_1225).

## Changelog templates

[Mustache](http://mustache.github.io/) templates are used to generate changelog or release notes with Gargamel. Please see example mustache templates
- for [html output](resources),
- for [markdown output](md-templates).

You can use your own custom templates by providing a path to a directory in `gargamel.edn` config file using the `:template-dir` key. Gargamel expects a `changelog.mustache` file as the main template: it can then include other mustache templates mustache style: `{{> other_template}}`.

Please see expected placeholders in the html output template files [here](resources).

## License

Copyright © 2014 MailOnline

Distributed under the [GNU General Public License, version 3](http://opensource.org/licenses/GPL-3.0)
