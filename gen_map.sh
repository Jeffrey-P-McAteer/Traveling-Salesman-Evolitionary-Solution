#!/bin/bash

file='cities.txt'

if [ "$@" = "" ]; then
  echo "Usage: ./gen_map.sh [1, 2, 3]"
  echo "where the array is the path output of a java TSP solution"
  exit 0
fi



