package com;

import java.util.*; 
import java.util.concurrent.CyclicBarrier;

import java.io.File;
import java.util.Scanner; 
import java.awt.event.*; 
import javax.swing.*;

public class InputFromUI extends JFrame implements ActionListener { 
	// JTextField 
	

	// JFrame 
	static JFrame f; 

	// JButton 
	// static JButton N; 
    // static JButton S;
    // static JButton E; 
    // static JButton W;
    static JButton done;
    static JButton add;
    // static JButton submit;
	// label to display text 
    static JLabel l; 
    static JLabel i; 
    static JLabel o; 
    static JLabel ti; 
    static JLabel e;
    static JTextField in;
    static JTextField out;  
    static JTextField t; 
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
            f.dispose();
        }
        else
        {
            if(in.getText().equals("")||(!in.getText().equals("E")&&!in.getText().equals("W")&&!in.getText().equals("S")))
            {
                l.setText("* incoming direction as E/W/S");
            }
            else if(out.getText().equals("")||(!out.getText().equals("E")&&!out.getText().equals("W")&&!out.getText().equals("S")))
            {
                l.setText("* outgoing direction as E/W/S");
            }
            else if(t.getText().equals("")  )
            {
                l.setText("* time as integer");
            }
            else
            {
                Integer arrivalTime=Integer.parseInt(t.getText());
                Character inDir = in.getText().charAt(0);
                Character outDir = out.getText().charAt(0);
                Pair<Character,Character>temp = new Pair(inDir,outDir);
                TrafficLightSystem.idDirMap.put(id,temp);
                if(TrafficLightSystem.idTimeMap.get(arrivalTime)==null){
                    List<Integer>tt =new LinkedList<>();
                    TrafficLightSystem.idTimeMap.put(arrivalTime,tt);
                }
                System.out.println("sdfsfssss"+Integer.toString(arrivalTime));
                TrafficLightSystem.idTimeMap.get(arrivalTime).add(id++);  
                System.out.print(TrafficLightSystem.idTimeMap);

                l.setText("* added");
                t.setText("");
                out.setText("");
                in.setText("");
            }
        }
    }
}
