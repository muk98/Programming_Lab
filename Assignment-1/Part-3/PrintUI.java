package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;
import com.InputFromUI;
import com.PrintOutputUI;
import com.TrafficLightSystem;


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
        if(s.equals("Print Status") || s.equals("Refresh Window")){
            Integer time = TrafficLightSystem.time;
            int tid=((time)/60)%3;
            tid++;
            int remTime = 60-time%60;
            String ans="";
            ans+="<html><head><style>table, th, td {border: 1px solid black;}</style></head><body><h3>Traffic Light Status</h3><br/>";

            ans=ans+("<table><tr><th>Traffic Light</th><th>Status</th><th>Time</th></tr>");
            if(tid==1){
                ans=ans+("<tr><th>T1</th><th>Green</th><th>" + Integer.toString(remTime) +"</th></tr>");
            }
            else{
                ans=ans+("<tr><th>T1</th><th>Red</th><th>---</th></tr>");
            }
            
            if(tid==2){
                ans=ans+("<tr><th>T2</th><th>Green</th><th>" + Integer.toString(remTime) +"</th></tr>");
            }
            else{
                ans+=("<tr><th>T2</th><th>Red</th><th>---</th></tr>");
            }
            if(tid==3){
                ans+=("<tr><th>T3</th><th>Green</th><th>" + Integer.toString(remTime) +"</th></tr>");
            }
            else{
                ans+=("<tr><th>T3</th><th>Red</th><th>---</th></tr></table><br/><br/>" );
            }
            ans+= "<h3>Current Traffic Status</h3><br/><table><tr><th>Vehicle</th><th>Source</th><th>Destination</th><th>Status</th><th>Remaining Time</th></tr>";
            ans+=(TrafficLightSystem.getAns(1));
            ans+=(TrafficLightSystem.getAns(2));
            ans+=(TrafficLightSystem.getAns(3));
            ans+=(TrafficLightSystem.get()+ "</table></body></html>");
            l1.setText(ans);
        }
        else 
        {
            f.dispose();
        }
    }
}
