alphabet("1").
alphabet("2").
alphabet("3").
alphabet("4").
alphabet("5").
alphabet("6").
alphabet("7").
alphabet("8").
alphabet("9").
alphabet("10").
alphabet("11").
alphabet("12").
alphabet("13").
alphabet("14").
alphabet("15").
alphabet("16").
alphabet("17").
alphabet("18").
alphabet("19").
alphabet("20").
alphabet("21").
alphabet("22").
alphabet("23").
alphabet("24").
alphabet("25").
alphabet("26").

strngIncl(Str, X, Zout, LenOut) :- sub_string(Str, 0, X, LenOut, Zout).
strngAfter(Str, X, Zout, LenOut ) :- sub_string(Str, X, LenOut , _, Zout).
is_empty_string(Str)  :- string_length(Str, Len), Len ==  0.
valid_else_halt(Str, Val) :- (alphabet(Str) -> Val is 1 ; Val is 0).
write_complete(Str, Actstring, Zout) :- write(Str), write(" "), write(Actstring), write(" "), writeln(Zout).

decodeTwo(Str) :- strngIncl(Str, 2, Zout, _), alphabet(Zout). 
decodeOne(Str) :- strngIncl(Str, 1, Zout, _), alphabet(Zout).
getNextString(Str, Zout) :- strngIncl(Str, 1, _, LenOut), strngAfter(Str, 1, Zout, LenOut).

decode(_, Actstring, _, Prev) :- is_empty_string(Actstring), writeln(Prev), !.
decode(Str, Actstring, Prev2Prev, Prev) :- (decodeOne(Actstring) -> W1 is Prev ; W1 is 0), (decodeTwo(Str) -> W2 is Prev2Prev ; W2 is 0), Wfinal is W1+W2, getNextString(Actstring, Zout),decode(Actstring, Zout, Prev, Wfinal).

string_decoder(Str) :- strngIncl(Str, 1, Zout, LenOut), valid_else_halt(Zout, Val), strngAfter(Str, 1, Zoutnew, LenOut), decode(Str, Zoutnew, Val, Val).

% decode_string(Str) :- aggregate_all(count , decode(Str), Count), writeln(Count).
decode_string(Str) :- string_decoder(Str).