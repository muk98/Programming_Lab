/**
 * Author: Mukul Verma, Tushara Langulya
 * Summary: This module contains the class which stores the information related to cars which passed 
 *          through the directions which had no restrictions in them (S to W, W to E, E to S)
 *         
 */

package com;
import java.util.concurrent.Semaphore;
import com.Pair;
import java.util.*; 

class UnrestrictedDir implements Runnable{
    LinkedList<Integer>finishList;
    Semaphore sem;
    UnrestrictedDir(Semaphore sem)
    {
        /*Initialising the list of passed cars and semaphore on this list */
        finishList=new LinkedList<>();
        this.sem=sem;
    }

    public void run()
    {
        while(true){
            Integer time=0;
            /**Get the time of the system, lock is required as main thread can increase the time while
             * accessing here.
             */
            try{
                TrafficLightSystem.semt.acquire();
                time = TrafficLightSystem.time;
                TrafficLightSystem.semt.release();
            }
            catch(Exception e){
                e.printStackTrace();
            } 
        if(TrafficLightSystem.idTimeMap.containsKey(time))
        {
            /*Get all id's of the car which arrive at this time */
            
            Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
            while(iterator.hasNext()) {
                Integer carId=iterator.next();

                /* Get the incoming and outgoing direction of the arrived car */
                Pair<Character,Character>  car = TrafficLightSystem.idDirMap.get(carId);
                
                /**Acquire the lock as the main thread can be accessing the its data for printing*/
                try{
                    sem.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                /*Check whether there is any restriction in the cars directions. If no restrictions 
                the car will pass so add it in the finish list */
                if(car.first=='S' && car.second=='W')
                {
                   finishList.add(carId);
                }
                else if(car.first=='E'&&car.second=='S')
                {
                    finishList.add(carId);
                }
                else if(car.first=='W'&&car.second=='E')
                {
                    finishList.add(carId);
                }

                /**Release the lock*/
                sem.release();
            } 
          
        }

        /**call the await method and wait for other threads to join*/
        try
        {
            TrafficLightSystem.newBarrier.await();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /**Small sleep to synchronize the working of threads*/
        try
            { 
                Thread.sleep(20);
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
            } 
            
        }
    }   
}  