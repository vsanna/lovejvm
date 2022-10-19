## how to compile guest java files
```shell
cd guest/
javac main/java/**/*.java -d out/main
javac test/java/**/*.java -d out/test
```



## how to compile standard libraries

```shell
# move to $JAVA_HOME/lib
cd $JAVA_HOME/lib
javac --module java.base -d path/to/project/standardlibs --module-source-path .

cd libs/custom/java
# customlibs/javaの中身をcustomlibs/java.baseに上書き
javac --module java.base -d ../../../standardlibs/classfiles --module-source-path .                        

cd libs/original
javac --module java.base -d ../../standardlibs/original --module-source-path .                        

```

## todo
- package reorg
- 命名
- unit test
- refactor
  - RawThread
  - resolver
  - bootstraploader
  - heapmanagerのrefactor
  - methodareaのrefactor

System.outがnull
setOutで値が入る
ただしそれはinitPhase1で呼ばれるものの、initPhase1がどこで呼ばれるかは不明