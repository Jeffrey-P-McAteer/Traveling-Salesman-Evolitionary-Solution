import sys
import matplotlib
import matplotlib.pyplot as plt

matplotlib.rcParams['toolbar'] = 'None'

if len(sys.argv) < 3 or sys.argv[1] == "" or sys.argv[2] == "":
  print("Usage: python gen_map.py 4 '0, 1, 2, 3'")
  print("where 4 is the name of a file of city data under ./cities")
  print("and the number array is the path output from a java TS solver")
  sys.exit()

file = "cities/"+sys.argv[1]+".txt"

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

for pathStr in sys.argv[2:]:
  path = []
  for numStr in pathStr.split(", "):
    path.append(int(numStr))
  plotSingle(path)

plt.show()


