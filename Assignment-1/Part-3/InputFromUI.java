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
    
    /**Buttons to add or finish the input*/
    static JButton done;
    static JButton add;
    
    /**Info label to show the input related information*/
    static JLabel infoLabel; 

    /**Labels and textFields to get the directions*/
    static JLabel inDirLabel; 
    static JLabel outDirLabel; 
    static JLabel timeLabel; 
    static JTextField inDirText;
    static JTextField outDirText;  

    /**text Field to get the time of arrival*/
    static JTextField incomingTimeText; 

    /**Variables to hold directions,time and id of the cars*/
    Character inDir;
    Character outDir;
    Integer arrivaTime;
    Integer id;
    
    /**Intialize the ui*/
    InputFromUI(){
        id=0;
    }

    /**
     * Function that is called when any action is performed over UI(example button click)
     * This function get the input from the user from UI.
     */
    public void actionPerformed(ActionEvent e) {
        
        /**Get the action performed*/      
        String s = e.getActionCommand(); 

        /**Check whether input is finished or not*/
        if(s.charAt(0)=='D')
        {
            /**Set the bool to false to start the functioning the system*/
            TrafficLightSystem.inputBool=false;

            /**Dispose the frame*/
            frame.dispose();
        }
        else
        {
            /**Check if given input is invalid*/
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
            /**If not then save the input*/
            else
            {
                /**Get the input*/
                Integer arrivalTime=Integer.parseInt(incomingTimeText.getText());
                Character inDir = inDirText.getText().charAt(0);
                Character outDir = outDirText.getText().charAt(0);
                
                if(inDir!=outDir)
                {
                    Pair<Character,Character>temp = new Pair(inDir,outDir);

                    /**Put the id and directions mapping in a map*/
                    TrafficLightSystem.idDirMap.put(id,temp);

                    /**If there is not car arrived at the provided input then initialize that map with that time*/
                    if(TrafficLightSystem.idTimeMap.get(arrivalTime)==null){
                        List<Integer>tempList =new LinkedList<>();
                        TrafficLightSystem.idTimeMap.put(arrivalTime,tempList);
                    }
                    /**Insert the car in the list of the cars arrived at the provided time*/
                    TrafficLightSystem.idTimeMap.get(arrivalTime).add(id++);
                    infoLabel.setText("* added");
                }
                /**Print the error*/
                else infoLabel.setText("Incorrect Direction Combination");

                /**Reset the Text Fields for next input*/
                incomingTimeText.setText("");
                outDirText.setText("");
                inDirText.setText("");
            }
        }
    }
}
