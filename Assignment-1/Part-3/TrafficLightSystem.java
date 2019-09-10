/*
* Author:  B.T.Langulya and Mukul Verma
* Summary: This module contains TrafficLightSystem class which simulates the working of the controller that controls
*          the working of the Three traffic lights and provides the necessary input to each of them at each second
*/ 
package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;


public class TrafficLightSystem implements Runnable{
    /* Time is maintained in this variable */
    public static int time;

    /* Cyclic Barrier used to make sure all threads complete their work at same time */
    public static CyclicBarrier newBarrier = new CyclicBarrier(5);

    /* idTimeMap to store the id of cars with respect to the time of arrival  */
    public static HashMap<Integer,List<Integer>> idTimeMap;
   
    /* idDirMap to store incoming and outgoing directions for each user id */
    public static HashMap<Integer,Pair<Character,Character>> idDirMap;  

    /* this bool is to represent whether input is still being taken or not */ 
    public static Boolean inputBool;

    /* 3 trafficlights and 1 unrestricted Dir class are generated*/
    public static TrafficLight l1;
    public static TrafficLight l2;
    public static TrafficLight l3;
    public static UnrestrictedDir d;

    /* This label is created for diplaying time and arrival of vehicles */
    public static JLabel l;

    /* This is used to store state of previous vehicle if no vehicle arrives this second */
    public static String PreviousStatus;

    /* Different semaphores declared to synchronize different blocks of code */
    public static Semaphore sem1,sem2,sem3,semt,semu;
    public static void main(String[] args){
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
        PreviousStatus="";
        l=new JLabel("");
        sem1=new Semaphore(1);
        sem2=new Semaphore(1);
        sem3=new Semaphore(1);
        semt=new Semaphore(1);
        semu=new Semaphore(1);

        /* The process remains in while loop till we collect all input from the UI */
        inputBool=true;
        takeInputFromUI();
        while(inputBool){
            System.out.print("");
            continue;
        }
        /* Output frame is called to display the time */
        printOutputUI(l);
        
        /* Thread instance of TrafficLightSystem class is created and it starts running */
        TrafficLightSystem controller = new TrafficLightSystem();
        Thread contThread = new Thread(controller);
        contThread.run();
    }  

    public void run(){
        
        /*All TrafficLights and UnrestrictedDir are initialised with appropriate semaphores */
         l1 = new TrafficLight(1,sem1);
         l2 = new TrafficLight(2,sem2);
         l3 = new TrafficLight(3,sem3);
         d=new UnrestrictedDir(semu);

        
        /*Thread instances are created and threads start running */
        Thread t1 = new Thread(l1);
        Thread t2 = new Thread(l2);
        Thread t3 = new Thread(l3);
        Thread t4 = new Thread(d);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        while(true){
            /* To simulate 1 second we make thread sleep for 1000 milli seconds*/
            try
            { 
                Thread.sleep(1000);
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
            }

            /*Will make the thread wait for all other threads to compete their work */
            try
            { 
                TrafficLightSystem.newBarrier.await();
            }  
            catch (Exception e)  
            { 
                e.printStackTrace();
            } 
            
            /*Status of vehicles arriving at this time */
            String status = TrafficLightSystem.waitorpass();

            /*If no vehicles at this time then print same status as last second */
            if(!status.equals("")){
                PreviousStatus = status;
            }
            
            /*Changing the string to html tag to set it in the label */
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+" </br>"+PreviousStatus.replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            
            /* Semaphore is acquired for time. As we dont want time to change before we display the data */
            try{
                semt.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            /* The time and status is diplayed */
            l.setText(ans);
            time++;
            semt.release();
            
            }
        }
    
    /* */
        public static String getDataFromTrafficLight(int id1)
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
        
        public static String getDatafromUD()
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
        uiInp.frame = new JFrame("textfield"); 
        uiInp.frame.setBounds(100, 100, 500, 350);
        uiInp.frame.setResizable(false);
		uiInp.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiInp.frame.getContentPane().setLayout(null);
    
        uiInp.inDirLabel = new JLabel("Incoming Direction(S/W/E)");
		uiInp.inDirLabel.setBounds(65, 31, 200, 20);
		uiInp.frame.getContentPane().add(uiInp.inDirLabel);
		uiInp.inDirText = new JTextField();
		uiInp.inDirText.setBounds(270, 28, 86, 25);
		uiInp.frame.getContentPane().add(uiInp.inDirText);
        uiInp.inDirText.setColumns(10);
        uiInp.outDirLabel = new JLabel("Outgoing Direction(S/W/E)");
		uiInp.outDirLabel.setBounds(65, 75, 200, 20);
		uiInp.frame.getContentPane().add(uiInp.outDirLabel);
		
		uiInp.outDirText = new JTextField();
		uiInp.outDirText.setBounds(270, 75,86, 25);
		uiInp.frame.getContentPane().add(uiInp.outDirText);
        uiInp.outDirText.setColumns(10);


        uiInp.timeLabel = new JLabel("Time(in integer as seconds)");
		uiInp.timeLabel.setBounds(65, 120, 200, 20);
		uiInp.frame.getContentPane().add(uiInp.timeLabel);
		
		uiInp.incomingTimeText = new JTextField();
		uiInp.incomingTimeText.setBounds(270, 120,86, 25);
		uiInp.frame.getContentPane().add(uiInp.incomingTimeText);
        uiInp.incomingTimeText.setColumns(10);

        uiInp.infoLabel = new JLabel("");
		uiInp.infoLabel.setBounds(65, 150, 500, 20);
		uiInp.frame.getContentPane().add(uiInp.infoLabel);

        uiInp.add = new JButton("Add");
		
		uiInp.add.setBounds(65, 170, 100, 23);
        uiInp.frame.getContentPane().add(uiInp.add);
        uiInp.done = new JButton("Done");
        uiInp.done.setBounds(200, 170, 100, 23);
        uiInp.frame.getContentPane().add(uiInp.done);

        uiInp.done.addActionListener(te);
        uiInp.add.addActionListener(te);
		uiInp.frame.show();

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
        System.out.println("oolalaa");
        PrintOutputUI outUI = new PrintOutputUI();
        
        outUI.f = new JFrame("Output UI"); 
        outUI.l1 = l;
        outUI.showStatus = new JButton("Print Status");

        PrintOutputUI actoutUI = new PrintOutputUI();
        outUI.showStatus.addActionListener(actoutUI);

        JPanel p = new JPanel();
        p.add(outUI.l1);
        p.add(outUI.showStatus);
        outUI.f.add(p); 
        outUI.f.setSize(500, 500);
        outUI.f.show();
    }
}



