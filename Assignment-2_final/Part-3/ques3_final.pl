/**
* dist is adjancency list contaning both vertices of edge and weight of edge.
* As undirected we have 2 entries per edge
*/
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

/* List of all gates(vertices) in the given problem */
gates([g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12,g13,g14,g15,g16,g17,g18]).

/* Start is list of all starting points */
start([g1,g2,g3,g4]).

/* End is list of all ending points */
end(g17).

/* restr specifies all restricted points */
restr(g7).
restr(g9).
restr(g18).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Part-B %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

/* To check if list is empty or not */
check(T) :- \+ T==[].


%%%%%%%%%%%%%%%%%%%%% MAIN FUNCTION %%%%%%%%%%%%%%%%%%%%%%%%%%%%
/**
* We verify first Gate is one of the Start gates for valid path.
* Then for every adjacent Gates we check whether they are directly connected.
* Lastly we check the last gate is end gate or not
*/

valid([Gate1,Gate2|Remaining]):-  start(Start),member(Gate1,Start),dist(Gate1,Gate2,_), valid1([Gate2|Remaining]).
valid1([Gate1,Gate2|Remaining]):- dist(Gate1,Gate2,_),check(Remaining),valid1([Gate2|Remaining]),!.
valid1([Gate1,Gate2|Remaining]):- dist(Gate1,Gate2,_),\+ check(Remaining),end(Gate2).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Part-A %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

/** 
* We check if edge exists between Gate1 and Gate2.
* If exists return Gate2 else return 'no'.
*/
edge_exists(Gate1,Gate2,Return):- dist(Gate1,Gate2,_),Return=Gate2.
edge_exists(_,_,Return):- Return='no'.

/** 
* We are calculating a list of all gates which are 
* directly connected to Gate. List contains gate names 
* it is connected to and no for gates which are not connected. 
*/
edges(_,[],[]).
edges(Gate,[Gate1|Remaining],List):- edge_exists(Gate,Gate1,Value),edges(Gate,Remaining,List_tail),List=[Value|List_tail],!.


%%%%%%%%%%%%%% MAIN FUNCTION %%%%%%%%%%%%%%%%%%%%%%%%

/* Start traversing from each start point*/
printPaths(_):- start(Start),aggregate_all(count,start_path(Start),Count),write(Count).

start_path([Gate|_]):- path(Gate,[Gate]).
start_path([_|Remaining]):- start_path(Remaining).

/** 
* If end is reached print the Path and return.
* Get  all gates directly connected to the gate and call dfs.
*/
path(Gate,List):- end(Gate),write("Path: "),write(List),nl,!.


/**
* Uncomment below to add the paths over the assumption that the prisoner 
* can return after visiting restricted gates.
*/

% path(Gate,List):- restr(Gate),length(List,Len),Pos is Len-2,nth0(Pos,List,Elem),append(List,[Elem],L1),path(Elem,L1).


path(Gate,List):- gates(Gates),edges(Gate,Gates,EdgeList),dfs(EdgeList,List).


/** 
* If edge is not no means its a valid edge.  
* Check if gate is already visited in the path. If yes ignore this path
* If No traverse further along this gate.
*/
dfs([],_,_).
dfs([Edge|_],List):- Edge \='no',not(member(Edge,List)),append(List,[Edge],L1),path(Edge,L1).
dfs([_|Remaining],List):- dfs(Remaining,List).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Part-B %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%% MAIN FUNCTION %%%%%%%%%%%%%

/* Setting intially minPath and minDist to NULL and 100000(MAX) resp.*/
optimal(_):-  nb_setval(minDistance, 100000),nb_setval(minPath,[]),start(Start), \+ startOpt(Start),nb_getval(minPath,Path),nb_getval(minDistance,Dist),write("Distance: "),write(Dist),nl,write(Path).

/* Start searching for optimal paths from each start gate*/
startOpt([]):-fail.
startOpt([Gate|_]):-optPath(Gate,[Gate],0),fail.
startOpt([_|Remaining]):-startOpt(Remaining),fail.

/**
* As once we get to restricted we have to again come back 
* better to go directly for smallest path.If reach end gate
* Compare distance we get in the path with the global value and set
* variables accordingly. If not end gate then get all edges and iterate further
*/
optPath(Gate,_,_):-restr(Gate),!.
optPath(Gate,List,Dist):-end(Gate),nb_getval(minDistance, Min),Dist < Min, nb_setval(minDistance, Dist), nb_setval(minPath,List),!.
optPath(Gate,List,Dist):-gates(Gates),edges(Gate,Gates,EdgeList),dfs(EdgeList,List,Gate,Dist).

/** 
* If edge is not no means its a valid edge.  
* Check if gate is already visited in the path. If yes ignore this path
* If No update distance of path and traverse further along this gate.
*/
dfs([],_,_,_).
dfs([Edge|_],List,Gate,Dist):- Edge \='no',not(member(Edge,List)),append(List,[Edge],L1),dist(Gate,Edge,Val),Value is Dist + Val,optPath(Edge,L1,Value).
dfs([_|Remaining],List,Gate,Dist):- dfs(Remaining,List,Gate,Dist).
