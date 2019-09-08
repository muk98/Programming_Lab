package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;

public class InputFromUI extends JFrame implements ActionListener { 
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
