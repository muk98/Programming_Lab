package com;

import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

public class Manufacturing{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.println("Input: <No. of bottle type B1, No. of bottle type B2, Time duration for observation (in sec.)>");
        int totalB1 = input.nextInt();
        int totalB2 = input.nextInt();
        int time = input.nextInt();
        input.close();
        ReentrantLock rel = new ReentrantLock(); 
        
        PackagingUnitThread pkg=null;
        SealingUnitThread slg=null;

        CustomPhaser phsr = new CustomPhaser(1, time,rel);

        UnfinishedTray unfinishedTray = new UnfinishedTray(totalB1,totalB2);
        Tray B1trayPackaging = new Tray(1,2);
        Tray B2trayPackaging = new Tray(2, 3);
        Tray traySealing = new Tray(3,2);
        GoDown goDown = new GoDown(); 

        pkg  =new PackagingUnitThread(phsr,"Packaging Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        slg =  new SealingUnitThread(phsr,"Sealing Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        phsr.set(pkg,slg);
        while (!phsr.isTerminated()) { 
            phsr.arriveAndAwaitAdvance(); 
        }

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