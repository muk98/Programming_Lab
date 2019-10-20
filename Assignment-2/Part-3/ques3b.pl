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

gates([g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12,g13,g14,g15,g16,g17,g18]).
start([g1,g2,g3,g4]).
end(g17).
restr(g7).
restr(g9).
restr(g18).

diff(X,H,L):- dist(X,H,_),L=H.
diff(_,_,L):- L='na'.

edges(_,[],[]).
edges(X,[H|T],R):- diff(X,H,L),edges(X,T,R_tail),R=[L|R_tail],!.


optimal(_):-  nb_setval(minDistance, 100000),nb_setval(minPath,[]),start(K), \+ startOpt(K),nb_getval(minPath,L),nb_getval(minDistance,X),write("Distance: "),write(X),nl,write(L).

startOpt([]):-fail.
startOpt([H|T]):-optPath(H,[H],0),fail.
startOpt([H|T]):-startOpt(T),fail.

optPath(X,L,Dist):-restr(X),!.
optPath(X,L,Dist):-end(X),append(L,[X],L1),nb_getval(minDistance, Min),Dist < Min, nb_setval(minDistance, Dist), nb_setval(minPath,L),!.
optPath(X,L,Dist):-gates(G),edges(X,G,E1),dfs(E1,L,X,Dist).

dfs([],_,_).
dfs([H|T],L,X,Dist):- H \='na',not(member(H,L)),append(L,[H],L1),dist(X,H,Val),D is Dist + Val,optPath(H,L1,D).
dfs([H|T],L,X,Dist):- dfs(T,L,X,Dist).