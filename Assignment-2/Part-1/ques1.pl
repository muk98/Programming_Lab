convert(_string):- string_chars(_string,X),start(X).

checkSingle(X):- X=\=0.
checkDouble(X,Y):- X=\=0,X<2,!.
checkDouble(X,Y):-X==2,Y<7.

len([],0).
len([_|T],N):- len(T,X),N is X+1.

checkEmpty(X):- len(X,Y),Y=:=0.


start([X|T]):- atom_number(X,Z),checkSingle(Z),checkEmpty(T),!.
start([X|T]):-  atom_number(X,Z),checkSingle(Z),start(T).


start([X,Y|T]):-  atom_number(X,P),atom_number(Y,Q),checkDouble(P,Q),checkEmpty(T),!.
start([X,Y|T]):-  atom_number(X,P),atom_number(Y,Q),checkDouble(P,Q),start(T).



% start([X,Y|T]) :-


