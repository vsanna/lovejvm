## how to compile standard libraries

```shell
# move to $JAVA_HOME/lib
cd $JAVA_HOME/lib
javac --module java.base -d path/to/this/project/standardlibs --module-source-path .
```