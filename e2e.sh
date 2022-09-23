#! /bin/bash

./gradlew clean build

java -cp build/classes

filename=$(gdate +%Y-%m-%d-%H-%m-%S).log

touch $filename

for entrypoint in guest/out/Add.class guest/out/ForLoop.class guest/out/Recursive.class guest/out/Recursive2.class
do
  echo "Running EP = $entrypoint" >> $filename
  java -cp build/classes/java/main/ dev.ishikawa.lovejvm.LoveJVM $entrypoint >> $filename
  echo "Finished EP = $entrypoint" >> $filename
  # or java -cp build/libs/lovejvm-1.0-SNAPSHOT.jar dev.ishikawa.lovejvm.LoveJVM guest/out/Add.class
done

