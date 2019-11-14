import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)

------------------------------------------------------------------------------

--Part a
func::[[Int]] -> Int
func list =  product (map sum list)


--Part b
greatest :: (a -> Int) -> [a] -> a
greatest func2 y = maximumBy (compare `on` func2)  y 

--Part c
data List a = Empty | Cons a (List a) deriving (Show)  

toList :: [a]->  List a
toList a = foldr Cons Empty a

toHaskelList :: List a -> [a]
toHaskelList a = unfoldr unwrapElem a 


unwrapElem :: (List e) -> Maybe (e, List e)
unwrapElem Empty = Nothing
unwrapElem (Cons el rem) = Just (el,rem)


------------------------------------------------------------------------------