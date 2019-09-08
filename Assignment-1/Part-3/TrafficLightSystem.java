import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
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
    boolean empty;
    int extra;
    TrafficLight(int id)
    {
        
        this.id=id;
        waitlist = new  LinkedList<>(); 
        
        finishlist=new LinkedList<>();
        empty=false;
        extra=0;
    }

    void emptyWait(Integer time,Integer carId)
    {
        Integer tid=((time)/60)%3;
        tid++;
        System.out.println(Integer.toString(carId));
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
                    try{
                        iterator.remove();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        System.out.println("god save usssss");
                    }
                    empty=true;
                    extra=6;
                }
            }
        }
    }

    public void run(){
        while(true){
        Integer time = TrafficLightSystem.time;
        updateList();
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
        empty=false;
       if(extra>0)extra--;
        try
        {
            TrafficLightSystem.newBarrier.await();
        }
        catch(Exception e)
        {

        }
        try
        { 
            Thread.sleep(50);
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
                Thread.sleep(20);
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
    public static Boolean inputBool;
    public static TrafficLight l1;
    public static TrafficLight l2;
    public static TrafficLight l3;
    public static UnrestrictedDir d;
    public static JButton out;
    public static JLabel l;
    public static void main(String[] args){
        File file = new File("input.txt");
        // Scanner input=new Scanner(System.in);
        // try {
        //     input = new Scanner(file);
        // } catch (Exception e) {
        //     System.out.println(e);
        // }
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
        inputBool=true;
        takeInputFromUI();
        out=new JButton("Show Output");
        l=new JLabel("");

        while(inputBool){
            System.out.print("");
            continue;
        }
        System.out.println("input Done");
        // printStatus();
        printOutputUI(out,l);

        Integer id=0;

    
        // while (input.hasNextLine()){
        //     String[] line = input.nextLine().split("@",3);
        //     Integer arrivalTime = Integer.parseInt(line[2]);
        //     Character inDir = line[0].charAt(0);
        //     Character outDir = line[1].charAt(0);
        //     Pair<Character,Character>temp = new Pair(inDir,outDir);
        //     idDirMap.put(id,temp);

        //     if(idTimeMap.get(arrivalTime)==null){
        //         List<Integer>tt =new LinkedList<>();
        //         idTimeMap.put(arrivalTime,tt);
        //     }
        //     idTimeMap.get(arrivalTime).add(id++);
        // }
        // input.close();
        // System.out.println(idDirMap);
        // System.out.println(idTimeMap);
        
        TrafficLightSystem controller = new TrafficLightSystem();
       
        Thread contThread = new Thread(controller);
        contThread.run();
    }  

    public void run(){
        System.out.println("System Starts.......");
        
         l1 = new TrafficLight(1);
         l2 = new TrafficLight(2);
         l3 = new TrafficLight(3);
         d=new UnrestrictedDir();
        

        
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
            
            // System.out.println(time);
            this.printDetails(l1,l2,l3,d);
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+"</br>"+TrafficLightSystem.waitorpass().replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            l.setText(ans);
            time++;
            }
        }
    
        public static String getAns(int id1)
        {
            Iterator iterator1;
            Iterator<Integer>  iterator2;
            if(id1==1)
            {
                iterator1= l1.waitlist.iterator();
                iterator2= l1.finishlist.iterator();
            }  
            else if(id1==2)
            {
                iterator1= l2.waitlist.iterator();
                iterator2= l2.finishlist.iterator();
            }
            else
            {
                iterator1= l3.waitlist.iterator();
                iterator2= l3.finishlist.iterator();
            }
            
            String ans="";
            ans="Waiting for T"+Integer.toString(id1)+"\n";
            while(iterator1.hasNext()){
                Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator1.next();
                int id = t.first;
                Pair<Character,Character> c= ( Pair<Character,Character>) TrafficLightSystem.idDirMap.get(id);
                int waitTime = t.second;
                char inDir = c.first;
                char outDir = c.second;
                ans=ans+Integer.toString(id)+" "+Integer.toString(waitTime)+" "+inDir+" "+outDir+"\n";
            } 
            ans+="Finished T"+Integer.toString(id1)+"\n";

            while(iterator2.hasNext()){
                Integer id = iterator2.next();
                Pair<Character,Character> c=  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                char inDir = c.first;
                char outDir = c.second;
               ans=ans+(Integer.toString(id)+" "+inDir+" "+outDir)+"\n";
            }  
            return ans; 
        }
        
        public static String get()
        {

           Iterator<Integer> iterator2= d.finishlist.iterator();
            
            String ans="";

            while(iterator2.hasNext()){
                Integer id = iterator2.next();
                Pair<Character,Character> c=  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                
                
                char inDir = c.first;
                char outDir = c.second;
               ans=ans+(Integer.toString(id)+" "+inDir+" "+outDir)+"\n";
            }
            return ans; 
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

    public static String  waitorpass()
    {
        
        String ans="";
        if(!idTimeMap.containsKey(time))
            return ans;
        Iterator<Integer> it=idTimeMap.get(time).iterator();
        while(it.hasNext())
        {
            int id=it.next();
            ans+="Vehicle: "+Integer.toString(id)+" ";
            if(l1.finishlist.contains(id) || l2.finishlist.contains(id)||l3.finishlist.contains(id))
                ans+="Pass\n";
            else
            {
                ans+="Wait\n";
                    
            }
          
                      
        }
        return ans;
    }

  public static void takeInputFromUI(){
        
        InputFromUI uiInp = new InputFromUI();

        uiInp.f = new JFrame("textfield"); 

		// create a label to display text 
		uiInp.l = new JLabel("Choose incoming direction"); 

		// create a new button 
        uiInp.N = new JButton("N"); 
        uiInp.S = new JButton("S"); 
        uiInp. E = new JButton("E"); 
        uiInp.W = new JButton("W"); 
        uiInp.submit=new JButton("submit");
        uiInp.done = new JButton("Done"); 
        uiInp.add=new JButton("Add");
		// create a object of the text class 
		InputFromUI te = new InputFromUI(); 

		// addActionListener to button 
        uiInp.N.addActionListener(te);
        uiInp.S.addActionListener(te);
        uiInp.E.addActionListener(te);
        uiInp.W.addActionListener(te); 
        uiInp.done.addActionListener(te);
        uiInp.submit.addActionListener(te);
        uiInp.add.addActionListener(te);
		// create a object of JTextField with 16 columns 
		uiInp.t = new JTextField(16); 
        JPanel p;
		// create a panel to add buttons and textfield 
	    p = new JPanel(); 
       
        p.add(uiInp.N);
        p.add(uiInp.S);
        p.add(uiInp.E);
        p.add(uiInp.W);
        p.add(uiInp.l);
        p.add(uiInp.t);
        p.add(uiInp.submit);
        p.add(uiInp.add);
        p.add(uiInp.done);
		// add panel to frame 
		uiInp.f.add(p); 

		// set the size of frame 
		uiInp.f.setSize(300, 300); 

		uiInp.f.show(); 
    }


    public static void printStatus(){
        PrintUI ui = new PrintUI();
        
        ui.f = new JFrame("Waiting Status");
        
        ui.l1 = new JLabel("");
        
        ui.showStatus = new JButton("Print Status");
        ui.endStatus=new JButton("End");
        
        PrintUI actUI = new PrintUI();
        
        ui.showStatus.addActionListener(actUI);
        ui.endStatus.addActionListener(actUI);
        JPanel p = new JPanel();
        p.add(ui.l1);
        p.add(ui.endStatus);
        p.add(ui.showStatus);
        ui.f.add(p); 
        ui.f.setSize(500, 500);
        ui.f.show();

    }
    

    public static void printOutputUI(JButton showOutput,JLabel l){
        PrintOutputUI outUI = new PrintOutputUI();
        
        outUI.f = new JFrame("Output UI"); 
        outUI.l1 = l;
        outUI.showStatus = new JButton("Print Status");
        outUI.showOutput = showOutput;
        //showOutput = outUI.showOutput;

        PrintOutputUI actoutUI = new PrintOutputUI();
        outUI.showStatus.addActionListener(actoutUI);
        outUI.showOutput.addActionListener(actoutUI);

        JPanel p = new JPanel();
        p.add(outUI.l1);
        p.add(outUI.showStatus);
        outUI.f.add(p); 
        outUI.f.setSize(500, 500);
        outUI.f.show();
    }
}


class PrintOutputUI extends JFrame implements ActionListener{

    static JFrame f; 
    static JButton showStatus;
    static JButton showOutput;
    static JLabel l1;

    PrintOutputUI(){
        
    }

    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();
        if(s.equals("Show Output")){
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+"</br>"+TrafficLightSystem.waitorpass().replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            l1.setText(ans);
        }
        if(s.equals("Print Status")){
            TrafficLightSystem.printStatus();
        }

    }


}



class InputFromUI extends JFrame implements ActionListener { 
	// JTextField 
	static JTextField t; 

	// JFrame 
	static JFrame f; 

	// JButton 
	static JButton N; 
    static JButton S;
    static JButton E; 
    static JButton W;
    static JButton done;
    static JButton add;
    static JButton submit;
	// label to display text 
	static JLabel l; 

    Character inDir;
    Character outDir;
    Integer arrivaTime;
    Integer id;
    InputFromUI(){
        id=0;
    }

    public void actionPerformed(ActionEvent e) 
	{      
        String s = e.getActionCommand(); 
        if((l.getText()=="Choose incoming direction")||(l.getText()=="Please select incoming direction!!!!"))
        {
           inDir=s.charAt(0); 
           if(inDir=='D'||inDir=='s'||inDir=='A')
           {
               l.setText("Please select incoming direction!!!!");
           }
           else
            l.setText("Choose outgoing direction");
        }
        else if((l.getText()=="Choose outgoing direction")||(l.getText()=="Please select outgoing direction!!!!"))
        {
            outDir=s.charAt(0);
            if(outDir=='D'||outDir=='s'||outDir=='A')
            {
                l.setText("Please select outgoing direction!!!!");
            }
            else
            l.setText("Enter start time");
        }
        else if((l.getText()=="Enter start time")||(l.getText()=="Please Specify time and click Enter"))
        {
            if(s.charAt(0)!='s')
            {
                l.setText("Please Specify time and click Enter");
            }
            else{
                Integer arrivalTime=Integer.parseInt(t.getText());
                Character inDir = this.inDir;
                Character outDir = this.outDir;
                Pair<Character,Character>temp = new Pair(inDir,outDir);
                TrafficLightSystem.idDirMap.put(id,temp);
                if(TrafficLightSystem.idTimeMap.get(arrivalTime)==null){
                    List<Integer>tt =new LinkedList<>();
                    TrafficLightSystem.idTimeMap.put(arrivalTime,tt);
                }
                TrafficLightSystem.idTimeMap.get(arrivalTime).add(id++);
                l.setText("Done or Wanna Add?");
            } 	
        } 
        else
        {
            if(s.charAt(0)=='A')
                l.setText("Choose incoming direction");
            else if(s.charAt(0)=='D')
            {
                TrafficLightSystem.inputBool=false;
                f.dispose();
            }
            else
            {
                l.setText("Please choose Add or Done!!");
            }
        }
    }
}


class PrintUI extends JFrame implements ActionListener{
    static JFrame f; 
    static JTextField t;
    static JButton showStatus;
    static JButton endStatus;
    static JLabel l1;

    PrintUI(){
        
    }

    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();
        if(s.equals("Print Status")){
            Integer time = TrafficLightSystem.time;
            int tid=((time)/60)%3;
            tid++;
            int remTime = 60-time%60;
            String ans="";
            ans=ans+("<html>|   Traffic Light   |   Status    |   Time    |<br/>");
            if(tid==1){
                ans=ans+("|        T1         |    Green    |   " + Integer.toString(remTime) +"  |<br/>");
            }
            else{
                ans=ans+("|        T1         |    Red      |    --     |<br/>");
            }
            
            if(tid==2){
                ans=ans+("|        T2         |    Green    |" + Integer.toString(remTime) +"  |<br/>");
            }
            else{
                ans+=("|        T2         |    Red      |    --     |<br/>");
            }
            if(tid==3){
                ans+=("|        T3         |    Green    |" + Integer.toString(remTime) +"  |<br/>");
            }
            else{
                ans+=("|        T3         |    Red      |    --     |<br/>" );
            } 
            ans+=(TrafficLightSystem.getAns(1).replaceAll(">", "&gt;").replaceAll("\n", "<br/>"));
            ans+=(TrafficLightSystem.getAns(2).replaceAll(">", "&gt;").replaceAll("\n", "<br/>") );
           ans+=(TrafficLightSystem.getAns(3).replaceAll(">", "&gt;").replaceAll("\n", "<br/>"));
           ans+=(TrafficLightSystem.get().replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>");
            l1.setText(ans);
        }
        else 
        {
            f.dispose();
        }
    }
}

