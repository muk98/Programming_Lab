
/**
* Listing all items with their names,
* price and type(starter/main/dessert)
*/
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
item('dessert',30,"Malai Sandwich").
item('dessert',10,"Rasmalai").

/* Maintaining list of starters, main dish and dessert*/
starters(["Corn Tikki","Tomato Soup","Chilli Paneer","Crispy Chicken","Papadi Chaat","Cold Drink"]).
main_dish(["Kadhai Paneer with Butter/Plain Naan","Veg Korma with Butter/Plain Naan","Murgh Lababdar with Butter/Plain Naan","Veg Dum Biryani with Dal Tadka","Steam Rice with Dal Tadka"]).
dessert(["Ice-Cream","Malai Sandwich","Rasmalai"]).





%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Part-A %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


/*Assigns 1 to variables to show that they represent starters/main dish/dessert */
check(1).

/*Assigns 0 to variables to show that they dont represent starters/main dish/dessert */
notCheck(0).

/**
* Each case will check that only one is selected among starters/main dish/dessert
* So only 1 variable will be set to 1 all others to 0.
* 3 different cases so that we can determine which course is selected.
*/
caseX(X,Y,Z):-check(X),notCheck(Y),notCheck(Z).
caseY(X,Y,Z):- notCheck(X),check(Y),notCheck(Z).
caseZ(X,Y,Z):- notCheck(X),notCheck(Y),check(Z).


%%%%%%%%%%%%%%% MAIN FUNCTIONS %%%%%%%%%%%%%%%%%%%%%%%

/* Verifying that only 1 variable is set */
menu('diet',X,Y,Z):- caseX(X,Y,Z).
menu('diet',X,Y,Z):- caseY(X,Y,Z).
menu('diet',X,Y,Z):- caseZ(X,Y,Z).

/* Verifying that all 3 variables are set */
menu('hungry',X,Y,Z):- check(X),check(Y),check(Z).

/**
* Verifying that only (X and Y) or (Y and Z) are set 
* to represent only (Starter and Main Dish) or (Main Dish and Desert) 
*/
menu('not_so_hungry',X,Y,Z):- check(X),check(Y),notCheck(Z).
menu('not_so_hungry',X,Y,Z):- notCheck(X),check(Y),check(Z).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Part-B %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


/**
* Pick  Item from specified course (given by Type) and 
* check if its value is within threshold
* Price will give the price of the item selected
*/
pick(Item,Type,Limit,Price):- item(Type,Cost,Item),Cost=<Limit,Price=Cost.

/**
* Pick  Item from specified course (given by Type) and 
* if its valid print the item. Append the item to List and
* search if further items can also be chosen
*/
selectItems([Item|Remaining],Type,Limit,List):- pick(Item,Type,Limit,Price),write("Items: "),write([Item|List]),nl,append([Item],List,L1),selectItems1(Remaining,Type,Limit-Price,L1).
selectItems([_|Remaining],Type,Limit,List):- selectItems(Remaining,Type,Limit,List).

selectItems1([Item|Remaining],Type,Limit,List):- pick(Item,Type,Limit,Price),append([Item],List,L1),write("Items: "),write(L1),nl,selectItems1(Remaining,Type,Limit-Price,L1).
selectItems1([_|Remaining],Type,Limit,List):- selectItems1(Remaining,Type,Limit,List).

/**
* Pick  Item2 from specified course (given by Type2) and 
* if its valid print the item along with Item1 from Type1. 
* Check further if any items from Type2 can be selected with Item1 from Type1
*/
pick_valid_pairs(Item1,[Item2|_],Type2,Limit):- pick(Item2,Type2,Limit,_),write("Items: "),writeln([Item1,Item2]),fail.
pick_valid_pairs(Item1,[_|Remaining2],Type2,Limit):-pick_valid_pairs(Item1,Remaining2,Type2,Limit).

/**
* Pick  Item1 from specified course (given by Type21) and 
* check items from Type2 to pair with Item1.
* Check for other items from Type1.
*/
pick_not_so_hungry([Item1|_],[Item2|Remaining2],Type1,Type2,Limit):- pick(Item1,Type1,Limit,Price),pick_valid_pairs(Item1,[Item2|Remaining2],Type2,Limit-Price).
pick_not_so_hungry([_|Remaining1],[Item2|Remaining2],Type1,Type2,Limit):-pick_not_so_hungry(Remaining1,[Item2|Remaining2],Type1,Type2,Limit).



%%%%%%%%%%%%%%%%%%% MAIN FUNCTIONS %%%%%%%%%%%%%%%%%%%%%%%

/* Checking which Type is selected and selecting items from that Type */
find_items('diet',X,Y,Z):- caseX(X,Y,Z),starters(T), \+ selectItems(T,'starter',40,[]),!.
find_items('diet',X,Y,Z):- caseY(X,Y,Z),main_dish(T),\+ selectItems(T,'main_dish',40,[]),!.
find_items('diet',X,Y,Z):- caseZ(X,Y,Z),dessert(T), \+ selectItems(T,'dessert',40,[]),!.


/* Checking which 2 Types are selected and selecting items from that Types */
find_items('not_so_hungry',X,Y,Z):- check(X),check(Y),notCheck(Z),starters(Starter),main_dish(Main), \+ pick_not_so_hungry(Starter,Main,'starter','main_dish',80),!.
find_items('not_so_hungry',X,Y,Z):- notCheck(X),check(Y),check(Z),main_dish(Main),dessert(Dessert),\+ pick_not_so_hungry(Main,Dessert,'main_dish','dessert',80),!.

/* Taking 1 item from each Type and printing all combinations possible */
find_items('hungry',X,Y,Z):- menu('hungry',X,Y,Z),item('starter',_,Starter),item('main_dish',_,Main),item('dessert',_,Dessert),write("Items: "),write([Starter,Main,Dessert]),nl,fail.



/**
 * Uncomment below section to print the cases to pick the multiple categories of same items in case of not so hungry.
 */

% print([H1|_],[H2|T2],Type1,Type2,X,L):- pick(H1,Type1,X,B1),printItems([H2|T2],Type2,X-B1,[H1|L]).
% print([H1|T1],[H2|T2],Type1,Type2,X,L):- pick(H1,Type1,X,B1),print([H1|T1],[H2|T2],Type1,Type2,X-B1,[H1|L]).
% print([_|T1],[H2|T2],Type1,Type2,X,L):- print(T1,[H2|T2],Type1,Type2,X,L).

% find_items('not so hungry',X,Y,Z):- check(X),check(Y),notCheck(Z),starters(T1),main_dish(T2),print(T1,T2,'starter','main_dish',80,[]),!.
% find_items('not so hungry',X,Y,Z):- notCheck(X),check(Y),check(Z),main_dish(T1),dessert(T2),print(T1,T2,'main_dish','dessert',80,[]),!.



