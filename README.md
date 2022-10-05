## how to compile standard libraries

```shell
# move to $JAVA_HOME/lib
cd $JAVA_HOME/lib
javac --module java.base -d path/to/this/project/standardlibs --module-source-path .
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