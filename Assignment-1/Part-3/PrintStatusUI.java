/**
 * Author: Mukul Verma and B.T. Langulya
 * Summary: This module contains the class which prints Status of all the cars on the UI. 
 */

package com;
import java.util.*; 
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;
import com.InputFromUI;
import com.*;


class PrintStatusUI extends JFrame implements ActionListener{

    static JFrame frame; 
    static JTextField textField;

    /**Buttons to show and remove the window from the screen*/
    static JButton showStatus;
    static JButton endStatus;

    /**Label that stores the status of the car*/
    static JLabel label;

    /**Initialize the UI*/
    PrintStatusUI(){
        
    }
    
    /**
     * Function that is called when any action is performed over UI(example button click)
     * This function sets the required label.
     */
    public void actionPerformed(ActionEvent e){

        /**Get the action performed*/
        String s = e.getActionCommand();

        /**If button to refresh the status the window is pressed*/
        if(s.equals("Print Status") || s.equals("Refresh Window")){

            /**Get the current time*/
            Integer time = TrafficLightSystem.time;

            /**Calculate the tid of the current light that is on*/
            int tid=((time)/60)%3;
            tid++;
            /**Remaining time of the traffic light*/
            int remTime = 60-time%60;
            String ans="";

            /**Create the required label in the html format*/
            ans += "<html><head><style>#name1{color:#484f4f;}table {border-collapse: collapse;width:70%;}th, td{text-align: left;padding: 8px;} tr:nth-child(even){background-color: blue}th {background-color: #618685;color: white;}";
            ans+="</style></head><body><h1 id=\"name1\">Traffic Light Status</h1><br/>";

            ans=ans+("<div class=\"traffic\"><table><tr><th>Traffic Light</th><th>Status</th><th>Time</th></tr>");
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
                ans+=("<tr><th>T3</th><th>Green</th><th>" + Integer.toString(remTime) +"</table></th></tr>");
            }
            else{
                ans+=("<tr><th>T3</th><th>Red</th><th>---</th></tr></table><br/><br/></table></div>" );
            }
            ans+= "<h1 id=\"name1\">Current Traffic Status</h1><br/><div overflow-y: auto;><table><tr><th>Vehicle</th><th>Source</th><th>Destination</th><th>Status</th><th>Remaining Time</th></tr>";
            
            /**Get the status of each from all the traffic lights*/
            ans+=(TrafficLightSystem.getDataFromTrafficLight(1));
            ans+=(TrafficLightSystem.getDataFromTrafficLight(2));
            ans+=(TrafficLightSystem.getDataFromTrafficLight(3));
            ans+=(TrafficLightSystem.getDatafromUD()+ "</table></div></body></html>");
            
            /**Set the lable */
            label.setText(ans);
        }
        /**dispose the frame*/
        else 
        {
            frame.dispose();
        }
    }
}
