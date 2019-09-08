package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;


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
                e.printStackTrace();
            }

            try
            { 
                TrafficLightSystem.newBarrier.await();
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
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



