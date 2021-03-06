item('starter',30,"Corn Tikki").
item('starter',20,"Tomato Soup").
item('starter',40,"Chilli Paneer").
item('starter',40,"Crispy Chicken").
item('starter',20,"Papadi Chaat").
item('starter',20,"Cold Drink").

item('main_dish',50,"Kadhai Paneer with Butter/Plain Naan").
item('main_dish',40,"Veg Korma with Butter/Plain Naan").
item('main_dish',50,"Murgh Lababdar with Butter/Plain Naan").
item('main_dish',50,"Veg Dum Biryani with Dal Tadka").
item('main_dish',40,"Steam Rice with Dal Tadka").

item('dessert',20,"Ice-Cream").
item('dessert',10,"Malai Sandwich").
item('dessert',10,"Rasmalai").
starters(["Corn Tikki","Tomato Soup","Chilli Paneer","Crispy Chicken","Papadi Chaat","Cold Drink"]).
main_dish(["Kadhai Paneer with Butter/Plain Naan","Veg Korma with Butter/Plain Naan","Murgh Lababdar with Butter/Plain Naan","Veg Dum Biryani with Dal Tadka","Steam Rice with Dal Tadka"]).
dessert(["Ice-Cream","Malai Sandwich","Rasmalai"]).

check(1).
notCheck(0).


caseX(X,Y,Z):-check(X),notCheck(Y),notCheck(Z).
caseY(X,Y,Z):- notCheck(X),check(Y),notCheck(Z).
caseZ(X,Y,Z):- notCheck(X),notCheck(Y),check(Z).

menu('diet',X,Y,Z):- caseX(X,Y,Z).
menu('diet',X,Y,Z):- caseY(X,Y,Z).
menu('diet',X,Y,Z):- caseZ(X,Y,Z).

menu('hungry',X,Y,Z):- check(X),check(Y),check(Z).

menu('not_so_hungry',X,Y,Z):- check(X),check(Y),notCheck(Z).
menu('not_so_hungry',X,Y,Z):- notCheck(X),check(Y),check(Z).



pick(H,Type,X,B):- item(Type,A,H),A=<X,B=A.

printItems([H|T],Type,X,L):- pick(H,Type,X,B),write("Items: "),write([H|L]),nl,append([H],L,L1),printItems1(T,Type,X-B,L1).
printItems([_|T],Type,X,L):- printItems(T,Type,X,L).

printItems1([H|T],Type,X,L):- pick(H,Type,X,B),append([H],L,L1),write("Items: "),write(L1),nl,printItems1(T,Type,X-B,L1).
printItems1([_|T],Type,X,L):- printItems1(T,Type,X,L).

print([H1|_],[H2|T2],Type1,Type2,X,L):- pick(H1,Type1,X,B1),printItems([H2|T2],Type2,X-B1,[H1|L]).
print([H1|T1],[H2|T2],Type1,Type2,X,L):- pick(H1,Type1,X,B1),print([H1|T1],[H2|T2],Type1,Type2,X-B1,[H1|L]).
print([_|T1],[H2|T2],Type1,Type2,X,L):- print(T1,[H2|T2],Type1,Type2,X,L).


pick_valid_pairs(H1,[H2|_],Type2,X):- pick(H2,Type2,X,_),write("Items: "),writeln([H1,H2]),fail.
pick_valid_pairs(H1,[_|T2],Type2,X):- pick_valid_pairs(H1,T2,Type2,X).

pick_not_so_hungry([H1|_],[H2|T2],Type1,Type2,X):- pick(H1,Type1,X,B1),pick_valid_pairs(H1,[H2|T2],Type2,X-B1).
pick_not_so_hungry([_|T1],[H2|T2],Type1,Type2,X):-pick_not_so_hungry(T1,[H2|T2],Type1,Type2,X).

find_items('diet',X,Y,Z):- caseX(X,Y,Z),starters(T), printItems(T,'starter',40,[]),!.
find_items('diet',X,Y,Z):- caseY(X,Y,Z),main_dish(T),printItems(T,'main_dish',40,[]),!.
find_items('diet',X,Y,Z):- caseZ(X,Y,Z),dessert(T),  printItems(T,'dessert',40,[]),!.

find_items('hungry',X,Y,Z):- menu('hungry',X,Y,Z),item('starter',_,B),item('main_dish',_,D),item('dessert',_,F),write("Items: "),write([B,D,F]),nl,fail.



% find_items('not so hungry',X,Y,Z):- check(X),check(Y),notCheck(Z),starters(T1),main_dish(T2),print(T1,T2,'starter','main_dish',80,[]),!.
% find_items('not so hungry',X,Y,Z):- notCheck(X),check(Y),check(Z),main_dish(T1),dessert(T2),print(T1,T2,'main_dish','dessert',80,[]),!.



find_items('not_so_hungry',X,Y,Z):- check(X),check(Y),notCheck(Z),starters(T1),main_dish(T2),\+ pick_not_so_hungry(T1,T2,'starter','main_dish',80),!.
find_items('not_so_hungry',X,Y,Z):- notCheck(X),check(Y),check(Z),main_dish(T1),dessert(T2),\+ pick_not_so_hungry(T1,T2,'main_dish','dessert',80),!.