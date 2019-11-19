import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)

------------------------------------------------------------------------------

--Part a
-- arguments: list of Integers
-- output: Integer 
-- Function Description: The function first finds the sum of each individual list using map and then use 
--                       'product' function to find the product of each element in that list.

m::[[Int]] -> Int
m list =  product (map sum list)


--Part b
-- arguments: a function 'func2' which has single arguements and returns an integer and a list.
-- output: a value thats data type depend on the input data type 
-- Function Description: The function uses maximumBy which returns the value for which func2 gives maximum value
--                       when applied of the list passed as an arguement.

greatest :: (a -> Int) -> [a] -> a
greatest func2 y = maximumBy (compare `on` func2)  y 

--Part c
-- declaration of a custom data type List which can be printed (can use show)
data List a = Empty | Cons a (List a) deriving (Show)  

-- arguments: List of values (data type can be defined at run time)
-- output: a List (custom) having required format. 
-- Function Description: The function uses foldr to convert normal list to Cons List. The Cons acts as 
-- operator which gets applied from the right side of the list starting with empty
toList :: [a]->  List a
toList list = foldr Cons Empty list

-- arguments: List of custom data type
-- output: List of values (data type will be defined at run time)
-- Function Description: The function uses unfoldr to convert Cons list to normal List. The unfoldr
-- unfolds the foldr operation we did above using unwrapElem operation
toHaskelList :: List a -> [a]
toHaskelList a = unfoldr unwrapElem a 


unwrapElem :: (List e) -> Maybe (e, List e)
unwrapElem Empty = Nothing
unwrapElem (Cons el rem) = Just (el,rem)


------------------------------------------------------------------------------