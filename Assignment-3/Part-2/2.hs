import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)
import Data.Map (Map)
import Control.Monad (join)
import qualified Data.Map as Map
import Data.Map (fromListWith, toList)

------------------------------------------------------------------------------


-- arguments: a string
-- output: list of strings 
-- Function Description: The function first finds all the suffixes of the given string using "tails" and then
--                       uses inits to get all the prefixes of all the suffixes generated before, which
--                       basically gives the list of all the substrings. This list also includes the [] strings
--                       which can eliminated by filter method.                                 
subString list = filter (not.null) (concatMap inits(tails (list)))


-- arguments: a count integer and a list of strings
-- output: an integer value 
-- Function Description: The function iterates over each element and counts the number of times if comes in the 
--                       list at an index greater that its own index. This gives us the number of anagram pairs.
--                       After list becomes empty that is iteration is done, the count stores the total count of
--                       anagram pairs.    
countElem count []  = count

-- The function uses filter (==x) to return the list of string that is same as x, and then uses length to
-- to get the count. 
countElem count (x : xs) =  if elem x xs 
                            then countElem (count + length (filter (==x) xs)) xs 
                            else countElem count xs

                            
-- arguments: list of lists of characters
-- output: None 
-- Function Description: The function first checks if the input is valid or not by checking its length,
--                       and then first add both the list in the input (from list of lists) and finds all
--                       the substrings and sort each string so that anagrams can easily be compared.
--                       The function then calls "countElem" function to get the count of anagram pairs.                           
func :: [[Char]] -> IO ()
func list = do
    if length list /=2
        then putStrLn("Incorrect Input")
        else do
            let newList = join list   
            
            -- get all list of al the sub strings
            let allSubString = subString newList 

            -- sort each individual substring and then store it in the list, using map
            let sortedList = map sort allSubString
            let cnt = countElem 0 sortedList
            print cnt
    

------------------------------------------------------------------------------
