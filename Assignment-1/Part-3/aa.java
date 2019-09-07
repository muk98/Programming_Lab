import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner;
import javax.swing.*; 

 class Pair<A, B> {
    public A first;
    public B second;

     Pair(A first, B second) {
        
        this.first = first;
        this.second = second;
    }
 }
 
    
class TrafficLight implements Runnable{
    int id;
    LinkedList<Pair<Integer,Integer>>  waitlist;
    LinkedList<Integer> finishlist;

    TrafficLight(int id)
    {
        
        this.id=id;
        waitlist = new  LinkedList<>(); 
        
        finishlist=new LinkedList<>();
    }

    void emptyWait(Integer time,Integer carId)
    {
        Integer tid=((time)/60)%3;
        tid++;
        System.out.println(Integer.toString(carId));
        if(id==tid && waitlist.isEmpty())
        {
            Integer ans=0;
            if(time%60>54)
            {
                ans+=120+60-time%60+1;
                Pair<Integer,Integer> t =new Pair(carId,ans);
                waitlist.add(t); 
            }
            else{
                
               finishlist.add(carId);
            }        
        }
        else if(waitlist.isEmpty())
        {
            
            Integer ans = 60*((time)/60)+60*((id-tid+3)%3)-time+1;
            Pair<Integer,Integer> t =new Pair(carId,ans);
            waitlist.add(t); 
        }
        else 
        {
            Integer ans = waitlist.getLast().second;
            
            Pair<Integer,Integer> t =new Pair(carId,ans);
            waitlist.add(t);
        }
    }

    void updateList(){
        if(!waitlist.isEmpty())
        {
            Iterator<Pair<Integer,Integer>> iterator=waitlist.iterator();
            while(iterator.hasNext())
            {
                Pair<Integer,Integer> t = iterator.next();
                t.second--;
                if(t.second<=0)
                {
                    finishlist.add(t.first);
                    waitlist.remove(t);
                }
            }
        }
    }

    public void run(){
        while(true){
        Integer time = TrafficLightSystem.time;
        if(TrafficLightSystem.idTimeMap.containsKey(time))
        {
          
            Iterator<Integer> iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
            while(iterator.hasNext()) {
                Integer id = iterator.next();
                // System.out.println(id);
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
        updateList();
        try
        {
            TrafficLightSystem.newBarrier.await();
        }
        catch(Exception e)
        {

        }
        try
        { 
            Thread.sleep(10);
        }  
        catch (Exception e)  
        { 
            
        }         
    }
    }
}

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
                Thread.sleep(10);
            }  
            catch (Exception e)  
            { 
                
            } 
    }
}
    
}   


class InputFormat{
    char inDir;
    char outDir;
    Integer time;

    InputFormat(char inDir,char outDir,Integer time){
        this.inDir = inDir;
        this.outDir = outDir;
        this.time = time;
    }
}


public class TrafficLightSystem implements Runnable{
    public static int time;
    public static CyclicBarrier newBarrier = new CyclicBarrier(5);
    public static HashMap<Integer,List<Integer>> idTimeMap;
    public static HashMap<Integer,Pair<Character,Character>> idDirMap;    
    
    public static void main(String[] args){
        File file = new File("input.txt");
        Scanner input=new Scanner(System.in);
        try {
            input = new Scanner(file);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        

        Integer id=0;
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
            
        while (input.hasNextLine()){
            String[] line = input.nextLine().split("@",3);
            Integer arrivalTime = Integer.parseInt(line[2]);
            Character inDir = line[0].charAt(0);
            Character outDir = line[1].charAt(0);
            Pair<Character,Character>temp = new Pair(inDir,outDir);
            idDirMap.put(id,temp);

            if(idTimeMap.get(arrivalTime)==null){
                List<Integer>tt =new LinkedList<>();
                idTimeMap.put(arrivalTime,tt);
            }
            idTimeMap.get(arrivalTime).add(id++);
        }
        input.close();
        // System.out.println(idDirMap);
        // System.out.println(idTimeMap);
        
        TrafficLightSystem controller = new TrafficLightSystem();
       
        Thread contThread = new Thread(controller);
        contThread.run();
    }  

    public void run(){
        System.out.println("System Starts.......");
        
        TrafficLight l1 = new TrafficLight(1);
        TrafficLight l2 = new TrafficLight(2);
        TrafficLight l3 = new TrafficLight(3);
        UnrestrictedDir d=new UnrestrictedDir();
        

        
        Thread t1 = new Thread(l1);
        Thread t2 = new Thread(l2);
        Thread t3 = new Thread(l3);
        Thread t4 = new Thread(d);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        while(true){
            try
            { 
                Thread.sleep(1000);
            }  
            catch (Exception e)  
            { 
                
            } 
            try
            { 
                TrafficLightSystem.newBarrier.await();
            }  
            catch (Exception e)  
            { 
            
            } 
            
            System.out.println(time);
            time++;
            this.printDetails(l1,l2,l3,d);
            }
        }
    

    public void printDetails(TrafficLight l1,TrafficLight l2,TrafficLight l3,UnrestrictedDir d){
        int tid=((time)/60)%3;
        tid++;
        int remTime = 60-time%60;
        System.out.println("|   Traffic Light   |   Status    |   Time    |");
        if(tid==1){
            System.out.println("|        T1         |    Green    |   " + Integer.toString(remTime) +"  |");
        }
        else{
            System.out.println("|        T1         |    Red      |    --     |");
        }
        
        if(tid==2){
            System.out.println("|        T2         |    Green    |" + Integer.toString(remTime) +"  |");
        }
        else{
            System.out.println("|        T2         |    Red      |    --     |");
        }
        if(tid==3){
            System.out.println("|        T3         |    Green    |" + Integer.toString(remTime) +"  |");
        }
        else{
            System.out.println("|        T3         |    Red      |    --     |" );
        }
        
    
        
        Iterator iterator1= l1.waitlist.iterator();
        
        while(iterator1.hasNext()){
            System.out.println("T1 Waiting..");
            Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator1.next();
            int id = t.first;
            Pair<Character,Character> c= ( Pair<Character,Character>) TrafficLightSystem.idDirMap.get(id);
            int waitTime = t.second;
            char inDir = c.first;
            char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+Integer.toString(waitTime)+" "+inDir+" "+outDir);
        } 

        
        
        Iterator<Integer> iterator2= l1.finishlist.iterator();

        while(iterator2.hasNext()){
            System.out.println("T1 finished..");
            Integer id = iterator2.next();
            Pair<Character,Character> c=  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
             
             
             char inDir = c.first;
             char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+inDir+" "+outDir);
        }

        Iterator iterator3= l2.waitlist.iterator();

        while(iterator3.hasNext()){
            System.out.println("T2 Waiting..");
            Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator3.next();
            int id = t.first;
           Pair<Character,Character> c= TrafficLightSystem.idDirMap.get(id);
            
            int waitTime = t.second;
            char inDir = c.first;
            char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+Integer.toString(waitTime)+" "+inDir+" "+outDir);
        } 

        
        
        Iterator<Integer>  iterator4= l2.finishlist.iterator();

        while(iterator4.hasNext()){
            System.out.println("T2 finished..");
            Integer id = iterator4.next();
            Pair<Character,Character> c=  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
             
             
             char inDir = c.first;
             char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+inDir+" "+outDir);
        }

        Iterator iterator5= l3.waitlist.iterator();

        
        while(iterator5.hasNext()){
            System.out.println("T3 waiting..");
            Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator5.next();
            int id = t.first;
            Pair<Character,Character> c= ( Pair<Character,Character>) TrafficLightSystem.idDirMap.get(id);

            int waitTime = t.second;
            char inDir = c.first;
            char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+Integer.toString(waitTime)+" "+inDir+" "+outDir);
        } 

        
        Iterator<Integer> iterator6= l3.finishlist.iterator();
        while(iterator6.hasNext()){
            System.out.println("T3 finished..");
            Integer id = iterator6.next();            
            Pair<Character,Character> c= ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
            char inDir = c.first;
            char outDir = c.second;
            System.out.println(Integer.toString(id)+" "+inDir+" "+outDir);
        }

    }

}


