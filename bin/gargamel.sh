#!/bin/bash

script_base="`dirname \"$0\"`"

java -jar $script_base/gargamel.jar "$@"
