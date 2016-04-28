import sys
import matplotlib
import matplotlib.pyplot as plt

matplotlib.rcParams['toolbar'] = 'None'

if len(sys.argv) < 3 or sys.argv[1] == "" or sys.argv[2] == "":
  print("Usage: python gen_map.py 'title' '(0, 0) (1, 0)'")
  print("where the number array is the path coordinate output from a java TS solver")
  sys.exit()

coords = []
for line in sys.argv[2].split(") ("):
  strNums = line.split(", ")
  if len(strNums) > 1:
    strNums[0] = ''.join([c for c in strNums[0] if c in '1234567890.'])
    strNums[1] = ''.join([c for c in strNums[1] if c in '1234567890.'])
    coords.append([int(strNums[0]), int(strNums[1])])
#coords = [x for (y,x) in sorted(zip(path,coords))]

x = []
y = []
for pair in coords:
  x.append(pair[0])
  y.append(pair[1])

plt.scatter(x, y)
plt.plot(x, y)

plt.suptitle(sys.argv[1])

plt.show()


