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

------------------------------------------------------------------------------
 
-- return_state variable stores whether there exists a solution or not
return_state :: IORef Int
{-# NOINLINE return_state #-}
return_state = unsafePerformIO (newIORef 0)


-- function signature
-- output: Int
-- Function Description: The function returns the value of return_state
getval = do
    readIORef return_state

-- function signature
-- arguments: Int
-- Function Description: The function stores the input in return_state
write x =do
    writeIORef return_state x

-- function signature
-- arguments: pos Int, newVal Int, list is List of Int 
-- output: List of Int
-- Function Description: The function replaces the value in the (pos)th column with newVal
replace pos newVal list = 
    take pos list ++ newVal : drop (pos+1) list


-- function signature
-- arguments: list of list of Int, pos Int, val Int, count Int
-- output: Int
-- Function Description: The function returns the count of val in the (pos)th column of all lists 
checkColumn [] pos val count = count 
checkColumn (x:xs) pos val count = if val== (x!!pos) 
                                        then checkColumn xs pos val count+1
                                        else checkColumn xs pos val count 


-- function signature
-- arguments: list of list of Int, row Int, col Int, val Int
-- output: Int
-- Function Description: The function returns 0 if val is  present in the 2*2 submatrix
-- whose first element is represented by row number and column number else returns 1
checkSubMatrix x row col val = if val == ((x!!row)!!col) || val == ((x!!row)!!(col+1)) 
                                        then 0
                                        else if val == ((x!!(row+1))!!col) || val == ((x!!(row+1))!!(col+1))
                                            then 0
                                            else 1

                                    
-- function signature
-- arguments: list of list of Int, row Int, col Int, val Int
-- output: Int
-- Function Description: The function tries to assign val to the element in the list represented by row 
-- number and column number. If on assigning this value we get a valid sudoku we return 1 else we try to 
-- assign the next number. If the number exceeds 4 then no valid sudoku is there so 0 is returned
putValue:: [[Int]] -> Int -> Int -> Int -> IO Int
putValue list row col val = do
    f<-getval
    -- if already found valid sudoku return 1
    if f==1
        then return 1
        else do
            -- if value to be inserted is 5 then it is not valid so return 0
            if val==5
                then return 0
                else do
                    -- checking if val is already present in the row or not. If present then check with the next value
                    if elem val (list!!row) 
                        then  do
                            -- check for valid sudoku by trying the next value
                            let new_val = val+1
                            putValue list row col new_val
                        else do
                            let new_val = val+1
                            -- cnt stores the frequency of val in the (col)th column 
                            let cnt = checkColumn list col val 0
                            -- if cnt is 0 then val is not present in the row
                            if cnt == 0
                                then do
                                    -- startRow and startCol will represent the row and col index of the 
                                    -- starting point of the 2*2 submatrix.flag is 1 if the submatrix doesnt have the val
                                    let startRow = (row `div` 2)*2
                                    let startCol = (col `div` 2)*2
                                    let flag = checkSubMatrix list startRow startCol val 
                                    -- if flag is 0 then val is present in the submatrix so check for valid sudoku 
                                    -- using next value
                                    if flag==0
                                        then  (putValue list row col new_val)
                                        else do 
                                            -- replace the element with val and check for next positions for valid sudoku
                                            let list1= replace row (replace col val (list!!row))  list 
                                            let new_col=col+1
                                            f<-(solve list1 row new_col)
                                            (putValue list row col new_val)
                                -- if cnt is 1 then val is present in the row so check for valid sudoku using next value
                                else  (putValue list row col new_val)


-- function signature
-- arguments: list of Int
-- Function Description: Prints the valid sudoku
printList [] = do 
    putStr ""

printList (x:xs) = do
    print x
    printList xs

-- function signature
-- arguments: list of list of Int, row Int, col Int
-- output: Int
-- Function Description: The function tries to assign val to the element in the list represented by row 
-- number and column number. If on assigning this value we get a valid sudoku we return 1 else we try to 
-- assign the next number. If the number exceeds 4 then no valid sudoku is there so 0 is returned
solve:: [[Int]] -> Int -> Int -> IO Int
solve list row col = do
    f<-getval
    -- if already found valid sudoku return 1
    if f==1
        then return 1
        else do
            -- if row is 4 then we have reached the end so we got valid sudoku. Print the sudoku and write 1 to the 
            -- return_status and then return 1
            if row == 4 
                then do
                    printList list
                    write 1
                    return 1
                                
                else do
                    -- if col is 4 then we have reached the end of this row so we go to the next row from 0th column. 
                    if col == 4
                        then do 
                            let r= row+1
                            solve list r 0
                        else do
                            -- if the elem in this position is not 0 then we procees with the next position
                            let elem = (list!!row)!!col
                            if elem /=0 
                                then do
                                    let c=col+1
                                    solve list row c
                                -- if the elem in this position is  0 then we check to assign numbers from 1 to 4
                                --  in this position
                                else do
                                    putValue list row col 1

-- function signature
-- arguments: list of Int
-- Function Description: The function checks if each list has 4 elements and each element is between 0 and 4
-- if yes then return 0 else return 1                                --  
check [] = 0                   
check (x:xs) = if length x/=4
                    then 1
                    else if length (filter (>4) x) /=0
                        then 1
                        else if length (filter (<0) x) /=0
                            then 1
                            else check xs                                

-- function signature
-- arguments: list of Int
-- Function Description: The function tries to find valid Sudoku with the given 4*4 input. If no valid sudoku present
-- then prints Not Possible.    
sudoku44 list = do
    -- if the 2d list doesnt have 4 lists then input is incorrect
    if length list /= 4
        then putStrLn ("Incorrect Input")
        else do
            -- we check that each list has 4 elements between 0 and 4
            if check list ==1 
                then putStrLn ("Incorrect Input")
                else do
                    -- we initialise the global variable return _status with 0
                    write 0
                    -- call solve function to find valid sudoku
                    flag <- solve list 0 0
                    f<- getval
                    -- if  global variable return _status is 0 then no valid sudoku is present
                    if f==0
                        then putStrLn ("Not possible")
                        else putStrLn ""
    
------------------------------------------------------------------------------
