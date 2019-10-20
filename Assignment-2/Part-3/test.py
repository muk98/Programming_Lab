f = open('ans.txt','r')
dic = {}
for lines in f.readlines():
    if lines not in dic:
        dic[lines] =1
    else: 
        dic[lines]+=1

for x,val in dic.items():
    if val>1:
        print(x)