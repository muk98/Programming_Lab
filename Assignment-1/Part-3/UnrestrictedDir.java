package com;
import java.util.concurrent.Semaphore;
import com.Pair;
import java.util.*; 

class UnrestrictedDir implements Runnable{
    LinkedList<Integer>finishlist;
    Semaphore sem;
    UnrestrictedDir(Semaphore sem)
    {
        finishlist=new LinkedList<>();
        this.sem=sem;
    }

    public void run()
    {
        while(true){
            Integer time=0;
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
            
            Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
            while(iterator.hasNext()) {
                Integer carId=iterator.next();
                System.out.println("sdsdsd");
                System.out.println(carId);
                Pair<Character,Character>  car = TrafficLightSystem.idDirMap.get(carId);
                try{
                    sem.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
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
                sem.release();
            } 
          
        }

        try
        {
            TrafficLightSystem.newBarrier.await();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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