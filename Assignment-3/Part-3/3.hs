import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)
import System.Random
import Control.Monad.State
import Data.IORef
import System.IO.Unsafe
-- team = ["BS","CM","CH", "CV","CS","DS","EE","HU","MA","ME","PH","ST"]

------------------------------------------------------------------------------

-- team variable (IO reference) which stores the fixtures of all teams between date 1 to 3.
team :: IORef [[[Char]]]

-- Create a new reference of the variable and initialize it with NULL value.
{-# NOINLINE team #-}
team = unsafePerformIO (newIORef [])

-- arguments: None
-- output: list of list of strings      
-- Function Description: The function reads the value stored in team variable using "readIORef".
getval = do
    readIORef team

-- arguments: IO reference
-- output: None
-- Function Description: The function stores the value into the IO variable (team) using "writeIORef".
write x =do
    writeIORef team x


-- arguments: list
-- output: list
-- Function Description: This function shuffles the list in a random order
shuffle x = 
    if length x <= 2 then 
    return x 
    else do
        -- generates a random index in the range 0 to len-1.
        i <- System.Random.randomRIO (0, length(x)-1)
        
        -- removes the (i)th element from the list and recurse on the list left.
        r <- shuffle (take i x ++ drop (i+1) x)

        -- append the removes (i)th in beginning of the list
        return (x!!i : r)

-- arguments: list
-- output: None
-- Function Description: This function print the list in formatted way       
printRow x = do
    let temp = x!!0 ++ " vs "++ x!!1 ++ "  " ++ x!!2 ++ "-11   " ++ x!!3 
    putStrLn temp
    
printAllFixtures [] = do 
    putStrLn "" 

-- arguments: list of list 
-- output: None
-- Function Description: This function print the each fixture list.    
printAllFixtures (x:xs) = do
    printRow x 
    printAllFixtures xs



-- arguments: list of the teams, slot and date on which schedule is to be fixed 
-- output: None
-- Function Description: This function recursively creates the schedule for each slot and date.
-- If the list of teams become empty, that means the all teams has been paired and all the fixtures are
-- set. Hence print the final fixture.
setFixture [] slot date = do
    temp <-getval    
    printAllFixtures temp
    

setFixture list slot date = do    

    --selects two teams from the list of teams
    let top_elements = take 2 list

    -- checks the current date, there are only 2 slots hence if slot number>2 then we need to 
    -- increase the date.
    let curr_date = if slot>2   
                        then date+1
                        else date
    
    -- update the slot accordingly
    let curr_slot = if slot>2 
                        then 1
                        else slot 
    
    -- set the time according the slot.
    let curr_time =if curr_slot==1
                        then ["09:30"]
                        else  ["19:30"]

    -- create the new fixture                    
    let newFixture = top_elements ++ [show curr_date] ++ curr_time

    -- update the fixture list by appending the new fixture into the previous list.
    prevFixtureList <- getval
    let newFixtureList = prevFixtureList ++ [newFixture]

    -- update the memory location storing the fixture list
    write newFixtureList

    -- drop both the teams as they are already being taken care.
    let newList = drop 2 list
    let nextSlot = curr_slot+1

    -- recursively call the setFixture for the rest teams.
    setFixture newList nextSlot curr_date
    

-- arguments: list of teams                       
-- output: None 
-- Function Description: This function checks if the number of teams are valid and if that is the
-- case then call the setFixture to set the fixtures after shuffling the list.     
printFixtures list = do
    let len = length list
    if len `mod` 2==1 then putStrLn "Invalid number of teams"
        else do
            write []
            tt <-shuffle list
            let t2 = take 12 tt
            let startDate=1
            let slot=1 
            setFixture t2 slot startDate



-- arguments: option -> team name whose fixture needs to be printed, list of all the fixtures                   
-- output: None 
-- Function Description: This function iterates over the fixtures list and matches team name and prints the
--                       fixture if team name is matched.
getMatch [] option = do
    -- if fixture is not found , print team not found.
    putStrLn "Team Not Scheduled/Found in the given time 1-3"
    
getMatch (head:tail) option = do
    --check if the fixture contains the fixture of the given team
    if elem option head
        then printRow head
        else getMatch tail option


-- arguments: option -> team name whose fixture needs to be printed.                    
-- output: None 
-- Function Description: This function calls the getMatch to print the given team's fixture.
printMatch option = do
    fixtureList <- getval 
    getMatch fixtureList option            

-- arguments: list of teams  and option showing, whether we need to set the new fixtures or print some schedule                     
-- output: None 
-- Function Description: This function set the new fixture or print the fixture of some team given 
--                       based on the option arguement.
fixture option list = do
    if option ==  "all" then printFixtures list
    else printMatch option



--Part b

-- arguments: date and time for which fixture is requested                
-- output: None 
-- Function Description: This function matches the date and time of the next fixture, with the date and
--                       time is given. If matches print the fixture.

printNextMatch day time [] =do
    -- If fixture is not found
    putStrLn "No match is scheduled next"          

printNextMatch day time (x:xs) = do
    -- if day and time matches, print the fixture else iterate on next fixture.
    if day == x!!2 && time == x!!3 then
        printRow x
    else printNextMatch day time xs


-- arguments: date and time for which fixture is requested                
-- output: None 
-- Function Description: This function check if the date and time are valid or not.  
check_valid_input:: Int -> [Char] -> Int
check_valid_input day time = 
    if (day>=1 && day<=31) && time >= "00:00" && time <= "23:59"
        then 0
        else 1


-- arguments: date and time for which fixture is requested                
-- output: None 
-- Function Description: This function first transforms the given input into desired form and then calls
--                       "printNextMatch" to print the fixture.
nextMatch day time = do
    fixtureList <- getval
    let new_time = if length time == 4 
        then '0':time
        else time
    let flag = (check_valid_input day new_time)

    -- If input is correct then find the time and date of next fixture which can 
    -- be there according to given input.
    if flag ==1 
        then putStrLn "Incorrect date/time"
        else do

            -- If time in the argument is less than or equal to 9.30 am or greater to 7.30 pm, 
            -- then next match will be there at 9.30 am. hence make it to 9.30 am 
            -- else the match will be at 7.30 pm , hence make it to 7.30 pm
            let curr_time = if new_time<="09:30"  
                            then "09:30"
                            else if new_time>"19:30"
                                then "09:30"
                                else "19:30"

            -- change the date accordingly.                    
            let curr_day = if new_time<="09:30"
                            then  day
                            else if new_time>"19:30"
                                then day+1
                                else day 
            let final_day = show curr_day
            printNextMatch final_day curr_time  fixtureList            

            
------------------------------------------------------------------------------
