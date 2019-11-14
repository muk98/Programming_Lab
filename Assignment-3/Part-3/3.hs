import Data.List
import System.IO 
import Data.List (maximumBy)
import Data.Function (on)
import System.Random
import Control.Monad.State
import Data.IORef
import System.IO.Unsafe
-- team = ["BS","CM","CH", "CV","CS","DS","EE","HU","MA","ME","PH","ST"]

team :: IORef [[[Char]]]
{-# NOINLINE team #-}
team = unsafePerformIO (newIORef [])

getval = do
    readIORef team

write x =do
    writeIORef team x

    
shuffle x = 
    if length x < 2 then 
    return x 
    else do
        i <- System.Random.randomRIO (0, length(x)-1)
        r <- shuffle (take i x ++ drop (i+1) x)
        return (x!!i : r)

printRow x = do
    let temp = x!!0 ++ " vs "++ x!!1 ++ "  " ++ x!!2 ++ "-11   " ++ x!!3 
    putStrLn temp

printAllFixtures [] = do 
    putStrLn "" 

printAllFixtures (x:xs) = do
    printRow x 
    printAllFixtures xs


setFixture [] slot date = do
    temp <-getval
    printAllFixtures temp
    
           

setFixture list slot date = do    
    let top_elements = take 2 list
    let curr_date = if slot>2   
                        then date+1
                        else date
    let curr_slot = if slot>2 
                        then 1
                        else slot 
    let curr_time =if curr_slot==1
                        then ["09:30"]
                        else  ["19:30"]
    let newFixture = top_elements ++ [show curr_date] ++ curr_time
    prevFixtureList <- getval
    let newFixtureList = prevFixtureList ++ [newFixture]
    write newFixtureList
    let newList = drop 2 list
    let nextSlot = curr_slot+1
    setFixture newList nextSlot curr_date
        
 
    
printFixtures list = do
    let len = length list
    if len `mod` 2==1 then putStrLn "Invalid number of teams"
        else do
            write []
            tt <- shuffle list
            let startDate=1
            let slot=1 
            setFixture tt slot startDate

getMatch [] option = do
    putStrLn "Team Not Found"

getMatch (head:tail) option = do
    if elem option head
        then printRow head
        else getMatch tail option

printMatch option = do
    fixtureList <- getval 
    getMatch fixtureList option

fixture option list = do
    if option ==  "all" then printFixtures list
    else printMatch option 

printNextMatch day time [] =do
    putStrLn "No match is scheduled next"


printNextMatch day time (x:xs) = do
    if day == x!!2 && time == x!!3 then
        printRow x
    else printNextMatch day time xs


nextMatch day time = do
    fixtureList <- getval 
    let curr_time = if time<="09:30" 
                    then "09:30"
                    else if time>"19:30"
                        then "09:30"
                        else "19:30"
    let curr_day = if time<="09:30"
                    then  day
                    else if time>"19:30"
                        then day+1
                        else day 
    let final_day = show curr_day
    printNextMatch final_day curr_time  fixtureList            
       