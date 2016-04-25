#!/bin/bash

echo -e "cities \t steps \t  final path length"

# reset city data
cat 'cities - 10000.txt' > cities.txt

for len in $(seq 10000 -200 100); do
  cat cities.txt | head -n $len > cities.txt.tmp
  cat cities.txt.tmp > cities.txt
  rm cities.txt.tmp
  steps=$(java TravSalesMergeIterative | tail -n 1 | cut -d' ' -f2)
  length=$(java TravSalesMergeIterative | tail -n 2 | head -n 1 | cut -d':' -f2)
  echo -e "$len \t $steps \t $length"
done

