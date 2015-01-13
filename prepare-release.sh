#!/bin/bash

lein do clean, test, uberjar
cp target/gargamel-*-standalone.jar bin/gargamel.jar

cd bin
tar czvf ../target/gargamel-latest.tgz *.*
