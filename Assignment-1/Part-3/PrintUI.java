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
