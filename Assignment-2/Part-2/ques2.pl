item('starter',30,"yo").
item('starter',10,"oo").
item('main',40,"my").
item('main',40,"mo").
item('desert',40,"dy").
item('desert',40,"do").
starters(["yo","oo"]).


check(1).
notCheck(0).


caseX:-check(X),notCheck(Y),notCheck(Z).
caseY:- notCheck(X),check(Y),notCheck(Z).
caseZ:- notCheck(X),notCheck(Y),check(Z).
menu('diet',X,Y,Z):- caseX.
menu('diet',X,Y,Z):- caseY.
menu('diet',X,Y,Z):- caseZ.

menu('hungry',X,Y,Z):- check(X),check(Y),check(Z).

menu('not so hungry',X,Y,Z):- check(X),check(Y),notCheck(Z).
menu('not so hungry',X,Y,Z):- notCheck(X),check(Y),check(Z).

pick(H,Type,X,B):- item(Type,A,H),A=<X,B=A.

printItems([H|T],Type,X,L):- pick(H,Type,X,B),write(H),nl,append(L,[H],L1),write(L1),nl,printItems(T,Type,X-B,L1).


find_items('diet',X,Y,Z):- caseX,starters(T),printItems(T,'starter',40,[]). 

find_items('hungry',X,Y,Z,Items):- menu('hungry',X,Y,Z),item('starter',A,B),item('main',C,D),item('desert',E,F),atom_concat(B,", ",K),atom_concat(K,D,I),atom_concat(", ",F,G),atom_concat(I,G,Items).


