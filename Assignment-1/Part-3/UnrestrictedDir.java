package com;

import com.Pair;
import java.util.*; 

class UnrestrictedDir implements Runnable{
    LinkedList<Integer>finishlist;

    UnrestrictedDir()
    {
        finishlist=new LinkedList<>();
    }

    public void run()
    {
        while(true){
        int time = TrafficLightSystem.time;
        if(TrafficLightSystem.idTimeMap.containsKey(time))
        {
            
            Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
            while(iterator.hasNext()) {
                Integer carId=iterator.next();
                Pair<Character,Character>  car = TrafficLightSystem.idDirMap.get(carId);
                if(car.first=='S' && car.second=='W')
                {
                   finishlist.add(carId);
                }
                else if(car.first=='E'&&car.second=='S')
                {
                    finishlist.add(carId);
                }
                else if(car.first=='W'&&car.second=='E')
                {
                    finishlist.add(carId);
                }
            }   
        }
        try
        {
            TrafficLightSystem.newBarrier.await();
        }
        catch(Exception e)
        {

        }
        try
            { 
                Thread.sleep(20);
            }  
            catch (Exception e)  
            { 
                
            } 
        }
    }   
}  