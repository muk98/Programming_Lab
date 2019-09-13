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
    public static TrafficLight T1;
    public static TrafficLight T2;
    public static TrafficLight T3;
    public static UnrestrictedDir unrestrictedDir;

    /* This label is created for diplaying time and arrival of vehicles */
    public static JLabel displayLabel;

    /* This is used to store state of previous vehicle if no vehicle arrives this second */
    public static String PreviousStatus;

    /* Different semaphores declared to synchronize different blocks of code */
    public static Semaphore sem1,sem2,sem3,semt,semu;
    public static void main(String[] args){
        idTimeMap =new HashMap<>();
        idDirMap=new HashMap<>();
        PreviousStatus="";
        displayLabel=new JLabel("");

        /*sempahore lock instance for each traffic light and other synchronization*/
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

        /* Output frame is called to display the time and status of input cars*/
        printOutput(displayLabel);
        
        /* Thread instance of TrafficLightSystem class is created and it starts running */
        TrafficLightSystem controller = new TrafficLightSystem();
        Thread contThread = new Thread(controller);
        contThread.run();
    }  

    public void run(){
        
        /*Create Traffic Lights instance with their respective semaphores*/
        T1 = new TrafficLight(1,sem1);
        T2 = new TrafficLight(2,sem2);
        T3 = new TrafficLight(3,sem3);
        unrestrictedDir=new UnrestrictedDir(semu);

        
        /*Thread instances are created*/
        Thread t1 = new Thread(T1);
        Thread t2 = new Thread(T2);
        Thread t3 = new Thread(T3);
        Thread t4 = new Thread(unrestrictedDir);

        /*Start the threads*/
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

            /*Call the await method to wait for other threads to join at the barrier*/
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
            
            /*Change the label of the Status*/
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+" </br>"+PreviousStatus.replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            
            /* Semaphore is acquired for time. As we dont want time to change before we display the data*/
            try{
                semt.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            /* The time and status is diplayed*/
            displayLabel.setText(ans);
            time++;
            semt.release();
            }
        }
    

        /**This functions returns the cars status from each traffic light and returns it as a String in the
        * form of html to show on the output status.
        * @param id1 shows from which traffic light we need to get the data.
        */
        public static String getDataFromTrafficLight(int id1)
        {
            Iterator iterator1;
            Iterator<Integer>  iterator2;

            /*Get the iterator of the data structures that hold the data in the particular traffic Light
            * Aquire the lock as when accessing the list that traffic light thread might change the 
            * the list.
            */
            if(id1==1)
            {
                iterator1= T1.waitList.iterator();
                iterator2= T1.finishList.iterator();
                try{
                    sem1.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }  
            else if(id1==2)
            {
                iterator1= T2.waitList.iterator();
                iterator2= T2.finishList.iterator();
                try{
                    sem2.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else
            {
                iterator1= T3.waitList.iterator();
                iterator2= T3.finishList.iterator();
                try{
                    sem3.acquire();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            String ans="";

            /*Iterate over waiting list*/
            while(iterator1.hasNext()){

                /*Get the id of the car*/
                Pair<Integer,Integer> t=(Pair<Integer,Integer>)iterator1.next();
                int id = t.first;

                /*Get the waiting time and directions of that car*/
                Pair<Character,Character> c =( Pair<Character,Character>) TrafficLightSystem.idDirMap.get(id);
                int waitTime = t.second;
                char inDir = c.first;
                char outDir = c.second;

                /*Store the data in a string in html format*/
                ans+="<tr><th>"+ Integer.toString(id)+"</th><th>" +inDir+ "</th><th>" + outDir +"</th><th>Waiting</th><th>" +  Integer.toString(waitTime)+ "</th></tr>";
            } 

            /*Iterate over List that stores the cars that are already passed*/
            while(iterator2.hasNext()){

                /*Get the id of the car*/
                Integer id = iterator2.next();

                 /*Get the directions of that car*/
                Pair<Character,Character> c =  ( Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                char inDir = c.first;
                char outDir = c.second;

                /*Store the data in a string in html format*/
                ans+= "<tr><th>"+ Integer.toString(id)+ "</th><th>" + inDir+ "</th><th>" + outDir +"</th><th>Passed</th><th>---</th></tr>";
            }  

            /*Release the Locks*/
            if(id1==1)sem1.release();
            else if(id1==2)sem2.release();
            else sem3.release();

            return ans; 
        }
        

        /**  This function returns the data of the cars that were passed unrestricted
        @return the status to be displayed in html format
        
        */
        public static String getDatafromUD(){

            /*Get the iterator of the data structures that hold the data.Aquire the lock as 
            * when accessing the list the thread might change the 
            * the list.
            */
            Iterator<Integer> iterator2= unrestrictedDir.finishList.iterator();
            String ans="";
            try{
                semu.acquire();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            /*Iterate over the list*/
            while(iterator2.hasNext()){

                /*Get the id of the car*/
                Integer id = iterator2.next();

                /*Get the directions of that car*/
                Pair<Character,Character> c=  (Pair<Character,Character>)  TrafficLightSystem.idDirMap.get(id);
                char inDir = c.first;
                char outDir = c.second;

                 /*Store the data in a string in html format*/
                ans+= "<tr><th>" + Integer.toString(id)+"</th><th>"+inDir+"</th><th>"+outDir+"</th><th>Passed</th><th>---</th></tr>";
            }

            /*Release the lock*/
            semu.release();

            return ans; 
        }

    /**
     * This function returns the status of the car that have just arrived at the T-junction. 
     * @return status of the arrived car in html format.
     */
    public static String  waitorpass()
    {
        
        String ans="";
        /**
         * If no car arrived at that time then return null.
         */
        if(!idTimeMap.containsKey(time))
            return ans;

        /**get the list of the cars that has arrived at that time*/    
        Iterator<Integer> it=idTimeMap.get(time).iterator();
        
        /**Lock all the traffic light data as they might change the data while we are accessing here*/
        try{
            sem1.acquire();
            sem2.acquire();
            sem3.acquire();
            semu.acquire();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        /**Check in which list that car was pushed to return the proper status.*/
        while(it.hasNext())
        {
            int id=it.next();
            
            /**If any of the traffic light contains the car id in its finish list then status of car is passed
             * else waiting.
            */
            ans+="Vehicle: "+Integer.toString(id)+" ";
            if(T1.finishList.contains(id) || T2.finishList.contains(id)||T3.finishList.contains(id)||unrestrictedDir.finishList.contains(id))
                ans+="Pass\n";
            else{
                ans+="Wait\n";       
            }          
        }
        /**Release the locks*/
        sem1.release();
        sem2.release();
        sem3.release();
        semu.release();
        
        return ans;
    }

    /**
     * This function creates a frame to take input from the UI.
     */
    public static void takeInputFromUI(){
        
        /*Create an instance of input class*/
        InputFromUI inpUI = new InputFromUI();

        /**Create an action listener for the class*/
        InputFromUI uiActionListen = new InputFromUI();
        
        /**Initialize the frame and set the required fields*/
        inpUI.frame = new JFrame("textfield"); 
        inpUI.frame.setBounds(100, 100, 500, 350);
        inpUI.frame.setResizable(false);
		inpUI.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inpUI.frame.getContentPane().setLayout(null);
    
        /**Set the labels,text boxes to take input and other required fields*/
        inpUI.inDirLabel = new JLabel("Incoming Direction(S/W/E)");
		inpUI.inDirLabel.setBounds(65, 31, 200, 20);
		inpUI.frame.getContentPane().add(inpUI.inDirLabel);
		inpUI.inDirText = new JTextField();
		inpUI.inDirText.setBounds(270, 28, 86, 25);
		inpUI.frame.getContentPane().add(inpUI.inDirText);
        inpUI.inDirText.setColumns(10);
        inpUI.outDirLabel = new JLabel("Outgoing Direction(S/W/E)");
		inpUI.outDirLabel.setBounds(65, 75, 200, 20);
		inpUI.frame.getContentPane().add(inpUI.outDirLabel);
		inpUI.outDirText = new JTextField();
		inpUI.outDirText.setBounds(270, 75,86, 25);
		inpUI.frame.getContentPane().add(inpUI.outDirText);
        inpUI.outDirText.setColumns(10);


        inpUI.timeLabel = new JLabel("Time(in integer as seconds)");
		inpUI.timeLabel.setBounds(65, 120, 200, 20);
		inpUI.frame.getContentPane().add(inpUI.timeLabel);
		
		inpUI.incomingTimeText = new JTextField();
		inpUI.incomingTimeText.setBounds(270, 120,86, 25);
		inpUI.frame.getContentPane().add(inpUI.incomingTimeText);
        inpUI.incomingTimeText.setColumns(10);

        /**Label to show status whether given input is accepted or not*/
        inpUI.infoLabel = new JLabel("");
		inpUI.infoLabel.setBounds(65, 150, 500, 20);
		inpUI.frame.getContentPane().add(inpUI.infoLabel);

        /**Create the buttons to add the input and to tell the system that input is completed*/
        inpUI.add = new JButton("Add");
		inpUI.add.setBounds(65, 170, 100, 23);
        inpUI.frame.getContentPane().add(inpUI.add);
        inpUI.done = new JButton("Done");
        inpUI.done.setBounds(200, 170, 100, 23);
        inpUI.frame.getContentPane().add(inpUI.done);

        /**Add the action listener with the ui*/
        inpUI.done.addActionListener(uiActionListen);
        inpUI.add.addActionListener(uiActionListen);

        /**show the frame on the screen*/
		inpUI.frame.show();

    }

    /**
     * This function create the window that shows the status all the cars that 
     * has entered into the system on the UI.
     * @return Frame on which Data is rendered.
     */
    public static JFrame printStatus(){
        
        /**Create an ui to render the status*/
        PrintStatusUI ui = new PrintStatusUI();

        /**Assign a new frame*/
        ui.frame = new JFrame("Waiting Status");
        
        ui.label = new JLabel("");
        
        // JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // ui.frame.setContentPane(panel);

        /**Add button to close and refresh the window*/
        ui.showStatus = new JButton("Refresh Window");
        ui.endStatus = new JButton("Close Window");
        
        /**Create an action listener*/
        PrintStatusUI actUI = new PrintStatusUI();
        
        /**Add the buttons to the action listener */
        ui.showStatus.addActionListener(actUI);
        ui.endStatus.addActionListener(actUI);

        /**Create a panel over which data is rendered*/
        JPanel panel = new JPanel();

        /**Add the components to the panel*/
        panel.add(ui.label);
        panel.add(ui.endStatus);
        panel.add(ui.showStatus);

        /**Add the panel to the frame*/
        ui.frame.add(panel); 
        ui.frame.setSize(500, 500);
        
        /**show the frame on the screen*/
        ui.frame.show();

        /**Render the intial data*/
        ui.showStatus.doClick();

        return ui.frame;
    }
    
    /**
     * Print the output screen on the screen
     * @param displayLabel label to print on the output screen
     */
    public static void printOutput(JLabel displayLabel){

        /**Create an instance of output window*/
        OutputScreen outUI = new OutputScreen();
        
        /**Create a new frame*/
        outUI.frame = new JFrame("Output UI"); 
        outUI.label = displayLabel;
        outUI.showStatus = new JButton("Print Status");

        /**Create actionListener for the ui*/
        OutputScreen actoutUI = new OutputScreen();

        /**Add the required fields on the actions listener*/
        outUI.showStatus.addActionListener(actoutUI);

        /**Create a new panel to render the data on it*/
        JPanel panel = new JPanel();
        panel.add(outUI.label);
        panel.add(outUI.showStatus);
        outUI.frame.add(panel); 
        outUI.frame.setSize(500, 500);

         /**show the frame on the screen*/
        outUI.frame.show();
    }
}



