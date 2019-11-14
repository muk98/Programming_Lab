import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)
import Data.Map (Map)
import Control.Monad (join)
import qualified Data.Map as Map
import Data.Map (fromListWith, toList)
import Data.IORef
import System.IO.Unsafe
-- team = ["BS","CM","CH", "CV","CS","DS","EE","HU","MA","ME","PH","ST"]

return_state :: IORef Int
{-# NOINLINE return_state #-}
return_state = unsafePerformIO (newIORef 0)

getval = do
    readIORef return_state

write x =do
    writeIORef return_state x

replace pos newVal list = 
    take pos list ++ newVal : drop (pos+1) list


checkColumn [] pos val count = count 
checkColumn (x:xs) pos val count = if val== (x!!pos) 
                                        then checkColumn xs pos val count+1
                                        else checkColumn xs pos val count 



-- checkSubMatrix [] row col val = 

checkSubMatrix x row col val = if val == ((x!!row)!!col) || val == ((x!!row)!!(col+1)) 
                                        then 0
                                        else if val == ((x!!(row+1))!!col) || val == ((x!!(row+1))!!(col+1))
                                            then 0
                                            else 1

                                    

putValue:: [[Int]] -> Int -> Int -> Int -> IO Int

putValue list row col val = do
    if val==5
        then return 0
        else do
            if elem val (list!!row) 
                then  do
                    let new_val = val+1
                    putValue list row col new_val
                else do
                    let new_val = val+1
                    let cnt = checkColumn list col val 0
                    if cnt == 0
                        then do
                            let startRow = (row `div` 2)*2
                            let startCol = (col `div` 2)*2
                            let flag = checkSubMatrix list startRow startCol val 
                            if flag==0
                                then  (putValue list row col new_val)
                                else do 
                                    let list1= replace row (replace col val (list!!row))  list 
                                    let new_col=col+1
                                    f<-(solve list1 row new_col)
                                    (putValue list row col new_val)
                        else  (putValue list row col new_val) 


printList [] = do 
    putStr ""

printList (x:xs) = do
    print x
    printList xs


solve:: [[Int]] -> Int -> Int -> IO Int

solve list row col = do
    if row == 4 
        then do
            f<-getval
            if f==0
                then do
                    printList list
                    write 1
                    return 1
                else return 1            
        else do
            if col == 4
                then do 
                    let r= row+1
                    solve list r 0
                else do
                    let elem = (list!!row)!!col
                    if elem /=0 
                        then do
                            let c=col+1
                            solve list row c
                        else do
                            putValue list row col 1

                                    

sudoku44 list = do
    -- print list
    write 0
    flag <- solve list 0 0
    f<- getval
    if f==0
        then putStrLn ("Not possible")
        else putStrLn ""
    


