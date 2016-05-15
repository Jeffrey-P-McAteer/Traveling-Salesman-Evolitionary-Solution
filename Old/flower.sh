#!/bin/bash

javac *.java
for n in `seq -w 1 200`; do
  echo $n | python gen_coords.py > flower/$n
  out=$(java RocketBoostedEvolution flower/$n)
  pth=$(echo "$out" | tail -n 1 | sed 's/.*: //g')
  python gen_map.py "offset=$n" "$pth" &
  #echo "$out"
done
rm *.class

