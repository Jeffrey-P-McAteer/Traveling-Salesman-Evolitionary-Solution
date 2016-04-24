#!/bin/bash

file="cities.txt"

function coords () {
  # usage: coords 2 (get coords on line 2 of $file)
  sed "$1q;d" "$file"
}

function dist () {
  # usage: dist 1 2 3 4
  absFunc="define abs(x) {if (x<0) {return -x}; return x;}"
  echo "$absFunc sqrt(abs(($1 - $3)+($2 - $4)))" | bc -l
}

function length () {
  # usage: length "1 2 3 4"
  len=0
  lastI=null
  for i in $(echo "$1" | tr ' ' '\n'); do
    if [ "$lastI" != "null" ]; then
      len=$(echo "$len + `dist $(coords $i) $(coords $lastI)`" | bc -l | xargs)
    fi
    lastI="$i"
  done
  first=$(echo "$1" | cut -d' ' -f1)
  last=$(echo "$1" | cut -d' ' -f `echo "$1" | tr ' ' '\n' | wc -l | xargs`)
  len=$(echo "$len + `dist $(coords $i) $(coords $lastI)`" | bc -l | xargs)
  echo $len
}

function rand () {
  # usage: rand 120, returns between 0 and 120 not including 120
  echo "`od -A n -N 2 -t u2 /dev/urandom` % $1" | bc
}

function mutate () {
  # usage: mutate "1 2 3 4 5 6 7 8"
  len=$(echo "$1" | tr ' ' '\n' | wc -l | xargs)
  i=$(echo "`rand $(echo $len-1 | bc)` + 1" | bc) # RIP sanity
  firstNum=$(echo "$1" | cut -d' ' -f $i)
  i=$(echo "$i + 1" | bc)
  nextNum=$(echo "$1" | cut -d' ' -f $i)
  echo "$1" | sed "s/$nextNum/$firstNum/" | sed "s/$firstNum/$nextNum/"
}

function optimize () {
  # usage: optimize "1 2 3 4 5 6"
  bestpath="$1"
  for i in $(seq 100); do
    newpath=$(mutate "$bestpath")
    diff=$(echo \($(length "$bestpath") - $(length "$newpath")\) \* 1000 | bc | awk '{printf "%f", $0}' | sed 's/\..*//g')
    if [ $diff -gt 0 ]; then
      bestpath="$newpath"
    fi
  done
  echo "$bestpath"
}

path="1 2 3 4 5 6"
echo "original: $path"
echo length: `length "$path"`
op=$(optimize "$path")
echo "optimized: $op"
echo "length: `length \"$op\"`"

