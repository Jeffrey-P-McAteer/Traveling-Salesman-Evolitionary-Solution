#!/bin/bash

if [ "$#" -lt 2 ]; then
  echo "Usage: ./run.sh 100 TSAlgo [TSAlgo1 TSAlgo2....]"
  echo "Where 100 is the name of the test file under ./cities and TSAlgo is the name of a TS algorithm class"
  exit 0
fi

javac *.java

for class in "${@:2}"; do
  if [ "$1" -lt 50 ]; then
    java $class "cities/$1.txt"
  else
    java $class "cities/$1.txt" false # do not print path
  fi
done

rm *.class

