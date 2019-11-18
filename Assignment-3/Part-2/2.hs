import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)
import Data.Map (Map)
import Control.Monad (join)
import qualified Data.Map as Map
import Data.Map (fromListWith, toList)


continuousSubString = filter (not . null) . concatMap inits . tails

countEm count []  = count
countEm count (x : xs) =    if elem x xs 
                            then countEm (count + length (filter (==x) xs)) xs 
                            else countEm count xs
                        


func list = do
    if length list /=2
        then putStrLn("Incorrect Input")
        else do
            let newList = join list
            let allSubString = continuousSubString newList 
            let sortedList = map sort allSubString
            let cnt = countEm 0 sortedList
            print cnt
    
