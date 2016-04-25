import sys
import matplotlib.pyplot as plt

file = "cities.txt"

if len(sys.argv) < 2 or sys.argv[1] == "":
  print("Usage: python gen_map.py '0, 1, 2, 3'")
  print("where the number array is the path output from a java TS solver")
  sys.exit()

def plotSingle(path):
  coords = []
  with open(file, "r") as ins:
    for line in ins:
      strNums = line.split(" ")
      coords.append([int(strNums[0]), int(strNums[1])])

  # print(path)
  # print(coords)

  coords = [x for (y,x) in sorted(zip(path,coords))]
  coords.append(coords[0])

  # print(coords)

  x = []
  y = []
  for pair in coords:
    x.append(pair[0])
    y.append(pair[1])

  plt.scatter(x, y)
  plt.plot(x, y)

for pathStr in sys.argv[1:]:
  path = []
  for numStr in pathStr.split(", "):
    path.append(int(numStr))
  plotSingle(path)

plt.show()


