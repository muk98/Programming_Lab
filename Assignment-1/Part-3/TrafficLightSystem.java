import java.util.*; 

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
    boolean status;
   Deque<Pair<Integer,Integer>>  waitlist;
    HashSet<Integer> finishlist;

    TrafficLight(int id)
    {
        status=false;
        this.id=id;
        waitlist = new  ArrayDeque<>(); 
        
        finishlist=new HashSet<>();
    }

    void emptyWait(int time,int carId)
    {
        int tid=((time)/60)%3;
        tid++;
        if(id==tid && waitlist.isEmpty())
        {

            int ans=0;
            if(time%60>54)
            {
                ans+=120+60-time%60;
                waitlist.add(new Pair(carId,ans)); 
            }
            else
                finishlist.add(carId);
                     
        }
        else if(waitlist.isEmpty())
        {
            
            int ans= 60*((time)/60)+60*((id-tid+3)%3)-time;
            if(ans%60>54)
                ans+=120+60-ans%60;
            waitlist.add(new Pair(carId,ans)); 
        }
        else 
        {
            int ans=waitlist.peekLast().second;
            if(ans%60>54)
                ans+=180-ans%60;
            waitlist.add(new Pair(cardId,ans));
        }
  
        
    }

    public void run(){

        int time = TrafficLightSystem.time;
        if(TrafficLightSystem.idTimeMap.containsKey(time))
        {
            
            Iterator iterator = TrafficLightSystem.idTimeMap.get(time).iterator();
            while(iterator.hasNext()) {
                Pair<Integer,Integer> car = TrafficLightSystem.idDirMap.get(iterator.next);
                if(id==1 && car.first=='S' && car.second=='E')
                {
                   emptyWait(time,iterator.next);
                }
                else if(id==2 && car.first=='W'&&car.second=='S')
                {
                    emptyWait(time,iterator.next);
                }
                else if(id==3 && car.first=='E'&&car.second=='W')
                {
                    emptyWait(time,iterator.next);
                }
            }  
        }
        
    }
}

class UnrestrictedDir implements Runnable{
    HashMap<Integer,Integer>finishList;

    UnrestrictedDir()
    {
        finishList=new HashMap<>();
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

    public void main(String[] args){
        File file = new File("input.txt");
        Scanner input = new Scanner(File);
        Integer id=0;
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
        while (input.hasNextLine()){
            String[] line = input.nextLine().split("@",3);
            Integer arrivalTime = Integer.parseInt(line[2]);
            Character inDir = line[0].charAt(0);
            Character outDir = line[1].charAt(0);
            Pair<Character,Character>temp = new Pair(inDir,outDir);
            if(idDirMap.get(arrivalTime)==null){
                map.put(arrivalTime, new List<Integer>());
            }
            idTimeMap.get(arrivalTime).add(id++);
        }
        
        TrafficLightSystem controller = new TrafficLightSystem();

        Thread contThread = new Thread(controller);
        contThread.run();

    }  



    public void run(){
        System.out.printl("System Starts.......");
        TrafficLight l1 = new TrafficLight(1);
        TrafficLight l2 = new TrafficLight(2);
        TrafficLight l3 = new TrafficLight(3);
        UnrestrictedDir d=new UnrestrictedDir();

        
        Thread t1 = new Thread(l1);
        Thread t2 = new Thread(l2);
        Thread t3 = new Thread(l3);

        t1.start();
        t2.start();
        t3.start();
        
        while(1){
            TrafficLight.newBarrier.await();
            time++;
        }
    }

}


