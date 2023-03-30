#!/bin/zsh

# expanding `**/*` is zsh's feature
cd libs/custom/main
javac --module java.base -d ../../../standardlibs/classfiles --module-source-path .

