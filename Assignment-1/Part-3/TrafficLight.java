package com;
import java.util.concurrent.Semaphore;
import java.util.*;
import com.Pair;
import com.*;

class TrafficLight implements Runnable{
    int id;
    LinkedList<Pair<Integer,Integer>>  waitlist;
    LinkedList<Integer> finishlist;
    boolean empty;
    int extra;
    Semaphore sem;
    TrafficLight(int id,Semaphore sem)
    {
        this.id=id;
        waitlist = new  LinkedList<>(); 
        
        finishlist=new LinkedList<>();
        empty=false;
        extra=0;
        this.sem=sem;
    }

    void emptyWait(Integer time,Integer carId)
    {   
        Integer tid=((time)/60)%3;
        tid++;
        try{
            sem.acquire();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(id==tid && waitlist.isEmpty())
        {
            
            Integer ans=0;
            if(empty)
            {
                ans+=6;
            }
            if((ans+time)%60>54)
            {
                ans+=120+60-time%60;
                Pair<Integer,Integer> t =new Pair(carId,ans);
                waitlist.add(t); 
            }
            else if(ans==0){
                empty=true;
                if(extra>0)
                {
                    Pair<Integer,Integer> t =new Pair(carId,extra);
                    waitlist.add(t);
                }
                else
                {
                    finishlist.add(carId);
                    extra=6;
                }
            } 
            else
            {
                Pair<Integer,Integer> t =new Pair(carId,ans);
                waitlist.add(t); 
            }       
        }
        else if(waitlist.isEmpty())
        {
            Integer ans = 60*((time)/60)+60*((id-tid+3)%3)-time;
            Pair<Integer,Integer> t =new Pair(carId,ans);
            waitlist.add(t); 
        }
        else 
        {
            Integer ans = waitlist.getLast().second+6;
            if((time+ans)%60>54)
            {
                ans+=120+60-(time+ans)%60;
                Pair<Integer,Integer> t =new Pair(carId,ans);
                waitlist.add(t); 
            }
            else
            {
                Pair<Integer,Integer> t =new Pair(carId,ans);
                waitlist.add(t);
            }
        }
        sem.release();
    }

    void updateList(){
        if(!waitlist.isEmpty())
        {
            
            try{
                sem.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            Iterator<Pair<Integer,Integer>> iterator=waitlist.iterator();
            while(iterator.hasNext())
            {
                Pair<Integer,Integer> t = iterator.next();
                t.second--;
                if(t.second<=0)
                {
                    finishlist.add(t.first);
                    try{
                        iterator.remove();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    empty=true;
                    extra=6;
                }
            }
            sem.release();
        }
    }

    public void run(){
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
            updateList();
            if(TrafficLightSystem.idTimeMap.containsKey(time))
            {
            
                Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
                while(iterator.hasNext()) {
                    Integer id = iterator.next();
                    Pair<Character,Character> car = TrafficLightSystem.idDirMap.get(id);
                    if(this.id==1 && car.first=='S' && car.second=='E')
                    {
                        emptyWait(time,id);
                    }
                    else if(this.id==2 && car.first=='W' && car.second=='S')
                    {
                        emptyWait(time,id);
                    }
                    else if(this.id==3 && car.first=='E' && car.second=='W')
                    {
                        emptyWait(time,id);
                    }
                }   
            }
            empty=false;
            if(extra>0)extra--;
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
                Thread.sleep(50);
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
            }       
        }
    }
}