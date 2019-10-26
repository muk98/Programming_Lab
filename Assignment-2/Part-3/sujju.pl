% connected from g1
distance(g1, g5, 4).
distance(g1, g6, 10).

% connected from g2
distance(g2, g5, 6).
distance(g2, g6, 9).

% connected from g3
distance(g3, g5, 8).
distance(g3, g6, 3).

% connected from g4
distance(g4, g5, 9).
distance(g4, g6, 5).

% connected from g5
distance(g5, g1, 4).
distance(g5, g2, 6).
distance(g5, g3, 8).
distance(g5, g4, 9).
distance(g5, g6, 7).
distance(g5, g7, 3).
distance(g5, g8, 9).
distance(g5, g10, 4).
distance(g5, g11, 6).
distance(g5, g12, 7).

% connected from g6
distance(g6, g1, 10).
distance(g6, g2, 9).
distance(g6, g3, 3).
distance(g6, g4, 5).
distance(g6, g5, 7).
distance(g6, g7, 10).
distance(g6, g8, 2).
distance(g6, g10, 9).
distance(g6, g11, 5).
distance(g6, g12, 3).

% connected from g7
distance(g7, g5, 3).
distance(g7, g6, 10).
distance(g7, g10, 2).
distance(g7, g11, 5).
distance(g7, g12, 7).
distance(g7, g8, 10).

% connected from g8
distance(g8, g5, 9).
distance(g8, g6, 2).
distance(g8, g10, 8).
distance(g8, g11, 4).
distance(g8, g12, 3).
distance(g8, g7, 10).
distance(g8, g9, 3).

% connected from g9
distance(g9, g8, 3).

% connected from g10
distance(g10, g5, 4).
distance(g10, g6, 9).
distance(g10, g8, 8).
distance(g10, g11, 2).
distance(g10, g12, 5).
distance(g10, g7, 2).
distance(g10, g15, 5).

% connected from g11
distance(g11, g5, 6).
distance(g11, g6, 5).
distance(g11, g8, 4).
distance(g11, g10, 2).
distance(g11, g12, 4).
distance(g11, g7, 5).
distance(g11, g15, 4).
distance(g11, g13, 5).

% connected from g12
distance(g12, g5, 7).
distance(g12, g6, 3).
distance(g12, g8, 3).
distance(g12, g10, 5).
distance(g12, g11, 4).
distance(g12, g7, 7).
distance(g12, g14, 8).
distance(g12, g13, 7).

% connected from g13
distance(g13, g12, 7).
distance(g13, g14, 4).
distance(g13, g15, 3).
distance(g13, g11, 5).

% connected from g14
distance(g14, g12, 8).
distance(g14, g13, 4).
distance(g14, g18, 4).
distance(g14, g17, 5).

% connected from g15
distance(g15, g10, 5).
distance(g15, g11, 4).
distance(g15, g13, 3).

% connected from g17
distance(g17, g14, 5).
distance(g17, g18, 8).

% connected from g18
distance(g18, g14, 4).
distance(g18, g17, 8).

free_from_jail(X) :- writeln(X).

dfs(g17, X) :- free_from_jail(X).
dfs(Current, X) :- distance(Current,Next,_), not(member(Next, X)), append(X, [Next], Y), dfs(Next, Y).

entry_point(X) :- not(member(g1, X)), append(X, [g1], Y), dfs(g1, Y).
entry_point(X) :- not(member(g2, X)), append(X, [g2], Y), dfs(g2, Y).
entry_point(X) :- not(member(g3, X)), append(X, [g3], Y), dfs(g3, Y).
entry_point(X) :- not(member(g4, X)), append(X, [g4], Y), dfs(g4, Y).

find_all_paths() :- aggregate_all(count, entry_point([]), Count), writeln(Count).