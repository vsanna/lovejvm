#!/bin/zsh

# expanding `**/*` is zsh's feature
cd libs/custom
rm -rf out
javac test/**/*.java -d out/test

