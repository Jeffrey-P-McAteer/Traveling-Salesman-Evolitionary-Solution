offset = int(raw_input())

outer_bot_left = [0, 0]
outer_bot_right = [400, 0]
outer_top_left = [0, 400]
outer_top_right = [400, 400]

inner_left = [0, 200]
inner_right = [400, 200]
inner_top = [200, 400]
inner_bot = [200, 0]

centermost = [200, 200]

x,y = (0,1)
inner_left[x] = inner_left[x] + offset
inner_right[x] = inner_right[x] - offset

inner_top[y] = inner_top[y] - offset
inner_bot[y] = inner_bot[y] + offset

print str(int(outer_bot_left[x]))+" "+str(int(outer_bot_left[y]))
print str(int(outer_bot_right[x]))+" "+str(int(outer_bot_right[y]))
print str(int(outer_top_left[x]))+" "+str(int(outer_top_left[y]))
print str(int(outer_top_right[x]))+" "+str(int(outer_top_right[y]))

print str(int(inner_left[x]))+" "+str(int(inner_left[y]))
print str(int(inner_right[x]))+" "+str(int(inner_right[y]))
print str(int(inner_top[x]))+" "+str(int(inner_top[y]))
print str(int(inner_bot[x]))+" "+str(int(inner_bot[y]))

print str(int(centermost[x]))+" "+str(int(centermost[y]))
