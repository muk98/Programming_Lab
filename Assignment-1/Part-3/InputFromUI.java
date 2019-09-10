/**
 * Author: Mukul Verma, B.T. Langulya
 * Summary: This module contains the class which takes the input from the UI.
 */

package com;
import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;

public class InputFromUI extends JFrame implements ActionListener { 
    static JFrame frame; 
    
    static JButton done;
    static JButton add;
    
    static JLabel infoLabel; 
    static JLabel inDirLabel; 
    static JLabel outDirLabel; 
    static JLabel timeLabel; 
    static JTextField inDirText;
    static JTextField outDirText;  
    static JTextField incomingTimeText; 
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
        if(s.charAt(0)=='D')
        {
            TrafficLightSystem.inputBool=false;
            frame.dispose();
        }
        else
        {
            if(inDirText.getText().equals("")||(!inDirText.getText().equals("E")&&!inDirText.getText().equals("W")&&!inDirText.getText().equals("S")))
            {
                infoLabel.setText("* incoming direction as E/W/S");
            }
            else if(outDirText.getText().equals("")||(!outDirText.getText().equals("E")&&!outDirText.getText().equals("W")&&!outDirText.getText().equals("S")))
            {
                infoLabel.setText("* outgoing direction as E/W/S");
            }
            else if(incomingTimeText.getText().equals("")  )
            {
                infoLabel.setText("* time as integer");
            }
            else
            {
                
                Integer arrivalTime=Integer.parseInt(incomingTimeText.getText());
                Character inDir = inDirText.getText().charAt(0);
                Character outDir = outDirText.getText().charAt(0);
                if(inDir!=outDir)
                {
                    Pair<Character,Character>temp = new Pair(inDir,outDir);
                    TrafficLightSystem.idDirMap.put(id,temp);
                    if(TrafficLightSystem.idTimeMap.get(arrivalTime)==null){
                        List<Integer>tt =new LinkedList<>();
                        TrafficLightSystem.idTimeMap.put(arrivalTime,tt);
                    }
            
                    TrafficLightSystem.idTimeMap.get(arrivalTime).add(id++);
                    infoLabel.setText("* added");
                }
                else infoLabel.setText("Incorrect Direction Combination");
                incomingTimeText.setText("");
                outDirText.setText("");
                inDirText.setText("");
            }
        }
    }
}
