package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;
import com.*;

public class PrintOutputUI extends JFrame implements ActionListener{

    static JFrame f; 
    static JButton showStatus;
    static JButton showOutput;
    static JLabel l1;
    static JFrame statusFrame;

    PrintOutputUI(){  
        statusFrame = null;
    }
    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();
        if(s.equals("Show Output")){
            String ans = "<html>Time: "+Integer.toString(TrafficLightSystem.time)+"</br>"+TrafficLightSystem.waitorpass().replaceAll(">", "&gt;").replaceAll("\n", "<br/>")+"</html>";
            l1.setText(ans);
        }
        if(s.equals("Print Status")){
            if(statusFrame!=null)
                statusFrame.dispose();
            statusFrame = TrafficLightSystem.printStatus();
        }
    }
}