package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
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
    public static JLabel l;
    public static String PreviousStatus;
    public static Semaphore sem1,sem2,sem3,semt,semu;
    public static void main(String[] args){
        File file = new File("input.txt");
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
        PreviousStatus="";
        inputBool=true;
        takeInputFromUI();
        l=new JLabel("");
        sem1=new Semaphore(1);
        sem2=new Semaphore(1);
        sem3=new Semaphore(1);
        semt=new Semaphore(1);
        semu=new Semaphore(1);

        while(inputBool){
            System.out.print("");
            continue;
        }
        System.out.println("input Done");
        // printStatus();
        printOutputUI(l);

        Integer id=0;
        
        TrafficLightSystem controller = new TrafficLightSystem();
       
        Thread contThread = new Thread(controller);
        contThread.run();
    }  

    public void run(){
        System.out.println("System Starts.......");
        
         l1 = new TrafficLight(1,sem1);
         l2 = new TrafficLight(2,sem2);
         l3 = new TrafficLight(3,sem3);
         d=new UnrestrictedDir(semu);

        
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
            // this.printDetails(l1,l2,l3,d);
            
            String status = TrafficLightSystem.waitorpass();
            if(!status.equals("")){
                PreviousStatus = status;
            }
            
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+" </br>"+PreviousStatus.replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            
            try{
                semt.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            l.setText(ans);
            time++;
            semt.release();
            
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
                try{
                    sem1.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }  
            else if(id1==2)
            {
                iterator1= l2.waitlist.iterator();
                iterator2= l2.finishlist.iterator();
                try{
                    sem2.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else
            {
                iterator1= l3.waitlist.iterator();
                iterator2= l3.finishlist.iterator();
                try{
                    sem3.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            String ans="";
            // ans = 
            // ans+="Waiting for T"+Integer.toString(id1)+"\n";

            while(iterator1.hasNext()){
                Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator1.next();
                int id = t.first;
                Pair<Character,Character> c= ( Pair<Character,Character>) TrafficLightSystem.idDirMap.get(id);
                int waitTime = t.second;
                char inDir = c.first;
                char outDir = c.second;
                ans+="<tr><th>"+ Integer.toString(id)+"</th><th>" +inDir+ "</th><th>" + outDir +"</th><th>Waiting</th><th>" +  Integer.toString(waitTime)+ "</th></tr>";
            } 
            // ans+="Finished T"+Integer.toString(id1)+"\n";

            while(iterator2.hasNext()){
                Integer id = iterator2.next();
                Pair<Character,Character> c=  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                char inDir = c.first;
                char outDir = c.second;
               ans+= "<tr><th>"+ Integer.toString(id)+ "</th><th>" + inDir+ "</th><th>" + outDir +"</th><th>Passed</th><th>---</th></tr>";
            }  
            if(id1==1)sem1.release();
            else if(id1==2)sem2.release();
            else sem3.release();
            return ans; 
        }
        
        public static String get()
        {


            Iterator<Integer> iterator2= d.finishlist.iterator();
            
            String ans="";
            try{
                semu.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            while(iterator2.hasNext()){
                Integer id = iterator2.next();
                Pair<Character,Character> c=  (Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                char inDir = c.first;
                char outDir = c.second;
                ans+= "<tr><th>" + Integer.toString(id)+"</th><th>"+inDir+"</th><th>"+outDir+"</th><th>Passed</th><th>---</th></tr>";
            }
            semu.release();
            System.out.println(ans);
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
        
        try{
            sem1.acquire();
            sem2.acquire();
            sem3.acquire();
            semu.acquire();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        while(it.hasNext())
        {
            int id=it.next();
            ans+="Vehicle: "+Integer.toString(id)+" ";
            if(l1.finishlist.contains(id) || l2.finishlist.contains(id)||l3.finishlist.contains(id)||d.finishlist.contains(id))
                ans+="Pass\n";
            else
            {
                ans+="Wait\n";
                    
            }          
        }
        sem1.release();
        sem2.release();
        sem3.release();
        semu.release();
        
        return ans;
    }

    public static void takeInputFromUI(){
        
        InputFromUI uiInp = new InputFromUI();
        InputFromUI te = new InputFromUI(); 
        uiInp.f = new JFrame("textfield"); 
        uiInp.f.setBounds(100, 100, 500, 350);
        uiInp.f.setResizable(false);
		uiInp.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiInp.f.getContentPane().setLayout(null);
    
        uiInp.i = new JLabel("Incoming Direction(S/W/E)");
		uiInp.i.setBounds(65, 31, 200, 20);
		uiInp.f.getContentPane().add(uiInp.i);
		uiInp.in = new JTextField();
		uiInp.in.setBounds(270, 28, 86, 25);
		uiInp.f.getContentPane().add(uiInp.in);
        uiInp.in.setColumns(10);
        uiInp.o = new JLabel("Outgoing Direction(S/W/E)");
		uiInp.o.setBounds(65, 75, 200, 20);
		uiInp.f.getContentPane().add(uiInp.o);
		
		uiInp.out = new JTextField();
		uiInp.out.setBounds(270, 75,86, 25);
		uiInp.f.getContentPane().add(uiInp.out);
        uiInp.out.setColumns(10);


        
        uiInp.ti = new JLabel("Time(in integer as seconds)");
		uiInp.ti.setBounds(65, 120, 200, 20);
		uiInp.f.getContentPane().add(uiInp.ti);
		
		uiInp.t = new JTextField();
		uiInp.t.setBounds(270, 120,86, 25);
		uiInp.f.getContentPane().add(uiInp.t);
        uiInp.t.setColumns(10);

        uiInp.l = new JLabel("");
		uiInp.l.setBounds(65, 150, 500, 20);
		uiInp.f.getContentPane().add(uiInp.l);

        uiInp.add = new JButton("Add");
		
		uiInp.add.setBounds(65, 170, 100, 23);
        uiInp.f.getContentPane().add(uiInp.add);
        uiInp.done = new JButton("Done");
        uiInp.done.setBounds(200, 170, 100, 23);
        uiInp.f.getContentPane().add(uiInp.done);

        uiInp.done.addActionListener(te);
        uiInp.add.addActionListener(te);
		uiInp.f.show();

    }

    public static JFrame printStatus(){
        PrintUI ui = new PrintUI();
        
        ui.f = new JFrame("Waiting Status");
        
        ui.l1 = new JLabel("");
        
        ui.showStatus = new JButton("Refresh Window");
        ui.endStatus = new JButton("Close Window");
        
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
        ui.showStatus.doClick();
        return ui.f;
    }
    
    public static void printOutputUI(JLabel l){
        PrintOutputUI outUI = new PrintOutputUI();
        
        outUI.f = new JFrame("Output UI"); 
        outUI.l1 = l;
        outUI.showStatus = new JButton("Print Status");
        // outUI.showOutput = new JButton("Print Status");
        //showOutput = outUI.showOutput;

        PrintOutputUI actoutUI = new PrintOutputUI();
        outUI.showStatus.addActionListener(actoutUI);
        // outUI.showOutput.addActionListener(actoutUI);

        JPanel p = new JPanel();
        p.add(outUI.l1);
        p.add(outUI.showStatus);
        outUI.f.add(p); 
        outUI.f.setSize(500, 500);
        outUI.f.show();
    }
}



