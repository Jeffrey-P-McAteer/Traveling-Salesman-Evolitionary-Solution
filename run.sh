#!/bin/bash

if [ "$#" -lt 2 ]; then
  echo "Usage: ./run.sh 100 TSAlgo [TSAlgo1 TSAlgo2....]"
  echo "Where 100 is the name of the test file under ./cities and TSAlgo is the name of a TS algorithm class"
  exit 0
fi

javac *.java

for class in "${@:2}"; do
  allOut=$(java -ea $class "cities/$1.txt")
  pth=$(echo "$allOut" | tail -n 1 | sed 's/.*: //g')
  len=$(echo "$allOut" | head -n 1 | cut -d' ' -f 11)
  python gen_map.py "$class - $len" "$pth" &
  echo "$allOut"
done

rm *.class

