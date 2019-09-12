/**
 * Author: Mukul Verma, Tushara Langulya
 * Summary: This module contains the class which stores the information related to a traffic Light
 *          that is the lists to store the waiting and passed cars through that traffic Light.
 */

package com;
import java.util.concurrent.Semaphore;
import java.util.*;
import com.Pair;
import com.*;

class TrafficLight implements Runnable{

    /**Variable to store the id of the traffic light */
    int id;

    /**Lists that store the waiting cars, first value store the car id and the second stores the waiting time*/
    LinkedList<Pair<Integer,Integer>>  waitList;

    /**Lists that store the car ids that are already passed*/
    LinkedList<Integer> finishList;

    /**Boolean to tell whether there a car is already passed in the same sec when other car has arrived*/
    boolean empty;

    /**Extra waiting time produced due to passing of a car (maximum 6 sec) */
    int extraTime;

    /**Semaphore to lock the resources*/
    Semaphore sem;

    /**
     * Initialize the traffic light
     * @param id car if
     * @param sem semaphore lock used by the thread
     */
    TrafficLight(int id,Semaphore sem)
    {
        this.id=id;
        waitList = new  LinkedList<>(); 
        finishList=new LinkedList<>();
        empty=false;
        extraTime=0;
        this.sem=sem;
    }

    /**
     * This function calculated the waiting Time of the arrived car
     * @param time current time of the system
     * @param carId id of the car
     */
    void calculateWaitingTime(Integer time,Integer carId)
    {   
        /**Get the id of the traffic light that is currently on */
        Integer tid=((time)/60)%3;

        /**Acquire the lock as the main thread can be accessing the its data for printing*/
        tid++;
        try{
            sem.acquire();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        /**Check whether traffic light on which car has arrived and the light that is on are same or not
         * and if such is case whether queue is empty or not.
        */
        if(id==tid && waitList.isEmpty())
        {
            /**Calculate the waiting time*/
            Integer waitingTime=0;

            /**If another car has already passed in the same second, if such is a case add 6 sec as extra
             * wait for the car arriving after that.
            */
            if(empty){
                waitingTime+=6;
            }

            /**Check whether sufficient time is left for car to pass, if not calculate the new waiting time*/
            if((waitingTime+time)%60>54)
            {
                waitingTime+=120+60-time%60;
                Pair<Integer,Integer> t =new Pair(carId,waitingTime);
                waitList.add(t); 
            }
            /**If no car has passed in that second and sufficient time is there to pass*/
            else if(waitingTime==0){

                /**Make the empty boolean to show that a car is already has been passed*/
                empty=true;

                /**Check whether extra wait is there or not,if yes that would be the new waiting time else 0 
                 * that is car can pass.
                */
                if(extraTime>0)
                {   
                    Pair<Integer,Integer> t =new Pair(carId,extraTime);
                    waitList.add(t);
                }
                else
                {
                    finishList.add(carId);
                    extraTime=6;
                }
            }
            /**Else push the waiting time as calculated*/ 
            else
            {
                Pair<Integer,Integer> t =new Pair(carId,waitingTime);
                waitList.add(t); 
            }       
        }
        /**If both the lights are different and waiting list is empty for the traffic light on which car has arrived*/
        else if(waitList.isEmpty())
        {

            /**Calculate the waiting time*/
            Integer waitingTime = 60*((time)/60)+60*((id-tid+3)%3)-time;
            Pair<Integer,Integer> t =new Pair(carId,waitingTime);
            waitList.add(t); 
        }
        else 
        {
            /**Else the waiting time will be the waiting time of the last car+6 and further sufficient time
             * left condition is checked.
            */
            Integer waitingTime = waitList.getLast().second+6;
            if((time+waitingTime)%60>54)
            {
                waitingTime+=120+60-(time+waitingTime)%60;
                Pair<Integer,Integer> t =new Pair(carId,waitingTime);
                waitList.add(t); 
            }
            else
            {
                Pair<Integer,Integer> t =new Pair(carId,waitingTime);
                waitList.add(t);
            }
        }

        /**Release the lock*/
        sem.release();
    }

    /**
     * This function removes the cars whose waiting time becomes zero and also decrease the waiting time
     * of each car by one.
     */
    void updateWaitingList(){

        /**Update when there is car waiting */
        if(!waitList.isEmpty())
        {
            
             /**Acquire the lock as the main thread can be accessing the its data for printing*/
            try{
                sem.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            /**Iterate over the waiting List*/
            Iterator<Pair<Integer,Integer>> iterator=waitList.iterator();
            while(iterator.hasNext())
            {
                Pair<Integer,Integer> t = iterator.next();
             
                /**Decrease the waiting time of the car by 1 sec */
                t.second--;
                
                /**If the waiting time becomes 0, remove it from the waiting list and push it in the
                 * finish list.
                 */
                if(t.second<=0)
                {
                    finishList.add(t.first);
                    try{
                        iterator.remove();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    /**Make the empty flag to show that car has left in that sec*/
                    empty=true;

                    /**Set the extra waiting time for cars coming next */
                    extraTime=6;
                }
            }
            /**Release the lock*/
            sem.release();
        }
    }

    /**
     * Override the run function of the thread runnable interface
     */
    @Override
    public void run(){
        while(true){

            /**Get the time of the system, lock is required as main thread can increase the time while
             * accessing here.
             */
            Integer time=0;
            try{
                TrafficLightSystem.semt.acquire();
                time = TrafficLightSystem.time;
                TrafficLightSystem.semt.release();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
            /**First update the waiting list */
            updateWaitingList();

            /**If new cars are scheduled to arrive at the currrent time, add and calculate the waiting time*/
            if(TrafficLightSystem.idTimeMap.containsKey(time))
            {
                Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();

                /**Get the cars and add into the list according to the directions*/
                while(iterator.hasNext()) {
                    Integer id = iterator.next();
                    Pair<Character,Character> car = TrafficLightSystem.idDirMap.get(id);

                    /**Calculate the waiting time accorting the if */
                    if(this.id==1 && car.first=='S' && car.second=='E')
                    {
                        calculateWaitingTime(time,id);
                    }
                    else if(this.id==2 && car.first=='W' && car.second=='S')
                    {
                        calculateWaitingTime(time,id);
                    }
                    else if(this.id==3 && car.first=='E' && car.second=='W')
                    {
                        calculateWaitingTime(time,id);
                    }
                }   
            }
            /**Make the empty flag false as further waiting time calculations can be done using the last waiting car
             * waiting time.
             */
            empty=false;
            /**Reduce the extra waiting time*/
            if(extraTime>0)extraTime--;

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
                Thread.sleep(50);
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
            }       
        }
    }
}