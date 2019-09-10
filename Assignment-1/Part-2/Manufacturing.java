/**
 * Author: Mukul Verma, B.T. Langulya
 * Summary: Main Module which takes input from the user and create necessary instances of packaging, sealing unit,
 *          phasors, etc. for the working of the system.
 */

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

public class Manufacturing{

    public static void main(String[] args){

        /*Create a scanner instance to take input from command line*/
        Scanner input = new Scanner(System.in);
        System.out.println("Input: <No. of bottle type B1, No. of bottle type B2, Time duration for observation (in sec.)>");
        int totalB1 = input.nextInt();
        int totalB2 = input.nextInt();
        int time = input.nextInt();
        input.close();

        /*Create a new lock instance*/
        ReentrantLock rel = new ReentrantLock(); 
        
        PackagingUnitThread pkg=null;
        SealingUnitThread slg=null;

        /*Create a phasor instance for synchronization of both the units*/
        CustomPhaser phsr = new CustomPhaser(1, time,rel);

        /*Create the tray, Godown instances*/
        UnfinishedTray unfinishedTray = new UnfinishedTray(totalB1,totalB2);
        Tray B1trayPackaging = new Tray(1,2);
        Tray B2trayPackaging = new Tray(2, 3);
        Tray traySealing = new Tray(3,2);
        GoDown goDown = new GoDown(); 

        /*Create a thread for packaging and Sealing Unit and start them*/
        pkg  =new PackagingUnitThread(phsr,"Packaging Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        slg =  new SealingUnitThread(phsr,"Sealing Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        
        /*Set the references of packaging and sealing threads in the phasor*/
        phsr.set(pkg,slg);

        /*Start the system and wait for whole processing to be done*/
        while (!phsr.isTerminated()) { 
            phsr.arriveAndAwaitAdvance(); 
        }


        /*Print the result at the end*/
        System.out.println("| Type  |   Status    |   Count");
        System.out.println("|   B1  |   Packaged  |   "+Integer.toString(pkg.b1));
        System.out.println("|   B1  |   Sealed    |   "+Integer.toString(slg.b1));
        System.out.println("|   B1  |   Godown    |   "+Integer.toString(goDown.totalB1));

        System.out.println("|   B2  |   Packaged  |   "+Integer.toString(pkg.b2));
        System.out.println("|   B2  |   Sealed    |   "+Integer.toString(slg.b2));
        System.out.println("|   B2  |   Godown    |   "+Integer.toString(goDown.totalB2));
		System.out.println("The phaser is terminated\n");
    }
}