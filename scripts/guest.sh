#!/bin/zsh

# expanding `**/*` is zsh's feature
cd guest
rm -rf out
javac main/**/*.java -d out/main

