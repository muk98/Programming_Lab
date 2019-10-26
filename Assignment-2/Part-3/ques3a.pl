dist(g1,g5, 4).
dist(g2,g5,6).
dist(g3,g5,8).
dist(g4,g5,9).
dist(g1,g6,10).
dist(g2,g6,9).
dist(g3,g6,3).
dist(g4,g6,5).
dist(g5,g7,3).
dist(g5,g10,4).
dist(g5,g11,6).
dist(g5,g12,7).
dist(g5,g6,7).
dist(g5,g8,9).
dist(g6,g8,2).
dist(g6,g12,3).
dist(g6,g11,5).
dist(g6,g10,9).
dist(g6,g7,10).
dist(g7,g10,2).
dist(g7,g11,5).
dist(g7,g12,7).
dist(g7,g8,10).
dist(g8,g9,3).
dist(g8,g12,3).
dist(g8,g11,4).
dist(g8,g10,8).
dist(g10,g15,5).
dist(g10,g11,2).
dist(g10,g12,5).
dist(g11,g15,4).
dist(g11,g13,5).
dist(g11,g12,4).
dist(g12,g13,7).
dist(g12,g14,8).
dist(g15,g13,3).
dist(g13,g14,4).
dist(g14,g17,5).
dist(g14,g18,4).
dist(g17,g18,8).
dist(g5 ,g1, 4).
dist(g5, g2,6).
dist(g5, g3,8).
dist(g5, g4,9).
dist(g6, g1,10).
dist(g6, g2,9).
dist(g6, g3,3).
dist(g6,g4,5).
dist(g7, g5,3).
dist(g10, g5,4).
dist(g11, g5,6).
dist(g12, g5,7).
dist(g6, g5,7).
dist(g8, g5,9).
dist(g8,g6,2).
dist(g12, g6,3).
dist(g11, g6,5).
dist(g10, g6,9).
dist(g7, g6,10).
dist(g10, g7,2).
dist(g11, g7,5).
dist(g12,g7,7).
dist(g8, g7,10).
dist(g9, g8,3).
dist(g12, g8,3).
dist(g11, g8,4).
dist(g10, g8,8).
dist(g15, g10,5).
dist(g11,g10,2).
dist(g12, g10,5).
dist(g15, g11,4).
dist(g13, g11,5).
dist(g12, g11,4).
dist(g13, g12,7).
dist(g14,g12,8).
dist(g13, g15,3).
dist(g14, g13,4).
dist(g17, g14,5).
dist(g18, g14,4).
dist(g18, g17,8).

gates([g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12,g13,g14,g15,g16,g17,g18]).

start([g1,g2,g3,g4]).
end(g17).
restr(g7).
restr(g9).
restr(g18).

check(T) :- \+ T==[].

valid([A,B|T]):- start(K),member(A,K),dist(A,B,_), valid1([B|T]).
valid1([A,B|T]):- dist(A,B,_),check(T),valid1([B|T]),!.
valid1([A,B|T]):- dist(A,B,_),\+ check(T),end(B).

diff(X,H,L):- dist(X,H,_),L=H.
diff(_,_,L):- L='na'.

edges(_,[],[]).
edges(X,[H|T],R):- diff(X,H,L),edges(X,T,R_tail),R=[L|R_tail],!.

validPaths(_):- start(K),aggregate_all(count,start_path(K),Count),write(Count).

start_path([H|_]):- path(H,[H]).
start_path([_|T]):- start_path(T).

path(X,L):- X=='g17',write("Path: "),write(L),nl,!.
path(X,L):- gates(G),edges(X,G,E1),dfs(E1,L).

dfs([],_,_).
dfs([H|_],L):- H \='na',not(member(H,L)),append(L,[H],L1),path(H,L1).
dfs([_|T],L):- dfs(T,L).


optimal(_):-  nb_setval(minDistance, 100000),nb_setval(minPath,[]),start(K), \+ startOpt(K),nb_getval(minPath,L),nb_getval(minDistance,X),write("Distance: "),write(X),nl,write(L).

startOpt([]):-fail.
startOpt([H|_]):-optPath(H,[H],0),fail.
startOpt([_|T]):-startOpt(T),fail.

optPath(X,_,_):-restr(X),!.
optPath(X,L,Dist):-end(X),nb_getval(minDistance, Min),Dist < Min, nb_setval(minDistance, Dist), nb_setval(minPath,L),!.
optPath(X,L,Dist):-gates(G),edges(X,G,E1),dfs(E1,L,X,Dist).

dfs([],_,_,_).
dfs([H|_],L,X,Dist):- H \='na',not(member(H,L)),append(L,[H],L1),dist(X,H,Val),D is Dist + Val,optPath(H,L1,D).
dfs([_|T],L,X,Dist):- dfs(T,L,X,Dist).
