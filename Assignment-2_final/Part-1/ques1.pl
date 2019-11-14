%
% Main Function to decode the given string. Checks the base case that is if string starts with 0 then number of ways are zero else count the total ways.
% Arguments: String -> Given String to decode.
%
convert(_string,Count):- string_chars(_string,X),aggregate_all(count,start(X),Count).


%
% Returns true if last digit is not equal to 0 else return false.  
%
checkSingle(X):- X=\=0.

%
% Returns true if last two digits are valid that is if they form the number from 10 to 26 else return false.
% Arguments: X -> first digit , Y -> second digit, of the last two digits.
%
checkDouble(X,_):- X=\=0,X<2,!.
checkDouble(X,Y):-X==2,Y<7.


%
% Returns the length of the List.
% Arguements: N -> length of list
len([],0).
len([_|T],N):- len(T,X),N is X+1.

checkEmpty(X):- len(X,Y),Y=:=0.

%
% Computes the number of ways to decode by considering last two and one digits from the current position.
%
start([X|T]):- atom_number(X,Z),checkSingle(Z),checkEmpty(T),!.
start([X|T]):-  atom_number(X,Z),checkSingle(Z),start(T).
start([X,Y|T]):-  atom_number(X,P),atom_number(Y,Q),checkDouble(P,Q),checkEmpty(T),!.
start([X,Y|T]):-  atom_number(X,P),atom_number(Y,Q),checkDouble(P,Q),start(T).
