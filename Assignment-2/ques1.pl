%
% Returns true if last digit is not equal to 0 else return false.  
%
check_last_digit(X):- X=\=0.

%
% Returns true if last two digits are valid that is if they form the number from 10 to 26 else return false.
% Arguments: X -> first digit , Y -> second digit, of the last two digits.
%
check_last_two_digits(X,_):- X=\=0,X<2,!.
check_last_two_digits(X,Y):-X==2,Y<7.


%
% Checks if the last digit is not 0,and returns the number of ways to decode depending whether last digit is zero or not.If last digit is zero then
% it cannot be considered as a single digit to decode hence number of ways to decode using only the current zero is 0 else it is equal to number of ways
% till last digit.
% Arguments: List -> list of characters of given string, Pos -> current indx which we are considering, PrevCount -> number of ways till last digit that is Pos-1, 
%            W1 -> Variable to store number of ways till current position.
%
countLastOne(List,Pos,PrevCount,W1):- Indx1 is Pos-1,nth0(Indx1,List,Char),atom_number(Char,X),(check_last_digit(X)) -> W1 is PrevCount; W1 is 0.


%
% Checks if whether two digits combined are valid for decoding or not and returns the number of ways to decode depending on those two digits.If two digits are
% valid then they can be considered as an alphabet and hence number of ways to decode using two digits is equal to number of ways
% till pos-2, else it is equal to 0.
% Arguments: List -> list of characters of given string, Pos -> current indx which we are considering, Prev2Count -> number of ways till two digits that is Pos-2, 
%            W2 -> Variable to store number of ways till current position.
%
countLastTwo(List,Pos,Prev2Count,W2):- Indx1 is Pos-2,nth0(Indx1,List,Char1),atom_number(Char1,X1),Indx2 is Pos-2,nth0(Indx2,List,Char2),atom_number(Char2,X2),(check_last_two_digits(X1,X2)) -> W2 is Prev2Count; W2 is 0.

%
% Returns the length of the List.
% Arguements: N -> length of list.
len([],0).
len([_|T],N):- len(T,X),N is X+1.

%
% Compares the length of the List and the current postion. Returns true if position is less than the length else true.
% Arguments: List -> list of characters of given string, Pos -> current indx which we are considering.
%
checkDone(List,Pos):- len(List,N),Pos>N.

%
% Computes the number of ways to decode by considering last two and one digits from the current position.
% Arguments: List -> list of characters of given string, Pos -> current indx which we are considering, PrevCount -> number of ways till last digit that is Pos-1, 
% Prev2Count -> number of ways till two digits that is Pos-2.
%
start(List,PrevCount,_,Pos) :- checkDone(List,Pos),write(PrevCount),!.
start(List,PrevCount,Prev2Count,Pos):- countLastOne(List,Pos,PrevCount,W1),countLastTwo(List,Pos,Prev2Count,W2),W is W1+W2,NewPos is Pos+1,start(List,W,PrevCount,NewPos).

%
% Main Function to decode the given string. Checks the base case that is if string starts with 0 then number of ways are zero else count the total ways.
% Arguments: String -> Given String to decode.
%
decode(String):- string_chars(String,List),nth0(0,List,Char),atom_number(Char,X),check_last_digit(X) -> start(List,1,1,2) ; write(0).

