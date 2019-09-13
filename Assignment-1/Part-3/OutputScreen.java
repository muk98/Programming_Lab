/**
 * Author: Mukul Verma, B.T. Langulya
 * Summary: This module contains the class that prints the status of a newly arrived car.
 */

package com;
import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;
import com.*;

public class OutputScreen extends JFrame implements ActionListener{

    static JFrame frame; 

    /**Buttons to open and close the status UI*/
    static JButton showStatus;
    static JButton showOutput;
    static JLabel label;
    static JFrame statusFrame;

    /**Intialize the output screen */
    OutputScreen(){  
        statusFrame = null;
    }

    /**
     * Function that is called when any action is performed over UI(example button click)
     * This function sets the required label.
     */
    public void actionPerformed(ActionEvent e){

        /**Get the action performed*/
        String s = e.getActionCommand();

        /**If show output button is pressed*/
        if(s.equals("Show Output")){
            String ans = "<html><head><style>table {border-collapse: collapse;width:70%;}th, td{text-align: left;padding: 8px;} tr:nth-child(even){background-color: blue}th {background-color: #4CAF50;color: white;}";
            ans += "<h2>Time: "+Integer.toString(TrafficLightSystem.time)+"</h2></br></br>"+TrafficLightSystem.waitorpass()+"</body></html>";
            label.setText(ans);
        }
        /**show print status*/
        if(s.equals("Print Status")){
            /**Create a new frame to show the output status.*/
            if(statusFrame!=null)
                statusFrame.dispose();
            statusFrame = TrafficLightSystem.printStatus();
        }
    }
}