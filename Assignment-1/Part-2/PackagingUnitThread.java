/*
* Author: Mukul Verma
* Summary: This module contains the PackagingUnitClass which simulates the working of the Packaging Unit.
*/

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;


class PackagingUnitThread implements Runnable { 
	Phaser phsr; 
    String name;
    
    /*boolean which shows that packaging unit is taking input for the first time,
    * after that is used to take alternate bottles from B1 and B2 tray
    */ 
    boolean initial;

    /*Variables to store the instances of the bottles,the trays and the Godown*/
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    UnfinishedTray unfinishedTray;
    GoDown goDown;

    /*Variable to store the processing time of the current bottle*/
    int processingTime;

    /*Variable to store the current bottle to be drawn by the Packaging Unit from the Unfinished tray*/
    int currBottleToDraw;

    /*Boolean to show whether the packagin unit is free or not*/
    boolean empty;

    /*Variables to store the count of bottles packaged by the Packaging Thread*/
    int b1;
    int b2;

    /*Variable to store whether the Packaging Unit needs to wait if the sealing Tray is full*/
    boolean pending;

    /*Re-entrant Lock instance*/
    ReentrantLock rel;

    /*Initialize the Packaging Unit*/
    PackagingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown,ReentrantLock rel)
	{ 
        
		phsr = p; 
        this.rel=rel;
        this.name = name; 
        this.currentBottle = bottle;
        this.initial = false;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.unfinishedTray = unfinishedTray;
        this.processingTime = 0;
        this.goDown = goDown;

        /*Initialize the Packaging unit to start with  B1 type of bottles*/
        this.currBottleToDraw = 1;
        this.empty=true;
        this.pending=false;
        b1=0;
        b2=0;

        /*Register the thread with the phasor and start the thread*/
		phsr.register(); 
		new Thread(this).start(); 
	} 

    /*Override the run method*/
	@Override
	public void run() 
	{ 
        /*Check whether the phasor is terminated or not*/
		while (!phsr.isTerminated()) { 
			System.out.println("Thread " + name+ " Beginning Phase "+ phsr.getPhase());
            
            /*Increase processing time of the bottle currently being packaged*/
            this.processingTime++;

            /*If the packaging unit hasnt finished the packaging then continue*/
            if(this.empty==false && this.processingTime<2){
                System.out.println("Packaging Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{

                /*If packaging unit is finished with packaging of the bottle*/
                if(this.processingTime>=2 && this.empty==false){

                    /*set that the packaging is done for that bottle*/
                    if(this.currentBottle.packagingStatus==false){
                        this.currentBottle.packagingStatus =true;

                        /*Increase the count*/
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Packaging DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                    }
                    else{
                        /*else packaging unit is idle*/
                        System.out.println("Packaging unit is IDLE");
                    }
                    /*If sealing is also done then transfer the file to godown*/
                    if(this.currentBottle.sealingStatus == true){
                        if(this.currentBottle.type == 1){
                            rel.lock();
                            this.goDown.totalB1++;
                            rel.unlock();
                        }
                        else {
                            rel.lock();
                            this.goDown.totalB2++;
                            rel.unlock();
                        }
                        this.empty=true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    else {
                        /*Switch on the flag to show that packaging unit is idle and bottles needs to be pushed into the tray
                        *at the end of the second
                        */
                        this.pending=true;
                    }
                 
                }
                /*If packaging unit is processing nothing and is not holding any bottle*/
                else if(this.empty==true){
                    
                    /*If there is nothing in the B1 and B2 queues take bottle from unfinished tray if available*/
                    if(this.B1PackagingTray.q.size()==0 && this.B2PackagingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0 ){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            System.out.println("Bottle drawn by Packaging Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                            
                            /*Decrease the count of the bottle chosen and set the next bottle that will be withdrawn, accordingly*/
                            if(this.currentBottle.type==1) {
                                if(unfinishedTray.totalB1==0)
                                {
                                    rel.lock();
                                    unfinishedTray.totalB2--;
                                    rel.unlock();
                                    this.currentBottle.type=2;
                                    if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                    else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                }
                                else
                                {
                                    rel.lock();
                                    unfinishedTray.totalB1--;
                                    rel.unlock();
                                    if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                    else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                }
                            }
                            else{
                                if(unfinishedTray.totalB2==0)
                                {
                                    rel.lock();
                                    unfinishedTray.totalB1--;
                                    rel.unlock();
                                    this.currentBottle.type=1;
                                    if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                    else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                }
                                else
                                {
                                    rel.lock();
                                    unfinishedTray.totalB2--;
                                    rel.unlock();
                                    if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                    else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                }
                            }
                            /*switch on the flag to show that a bottle is curretly being processed*/
                            this.empty=false;
                        }
                    }
                    /* If any tray i.e. B1 or B2 contains a bottle then take the bottle from there
                    *  B1 has more priority initially and afterthat alternate bottles are chosen if available.
                    */
                    else{
                        /* If both has bottles,Intial state, B1 is taken*/
                        if(this.B1PackagingTray.q.size()>0 && this.B2PackagingTray.q.size()>0){
                            
                            if(this.initial==false){
                                rel.lock();
                                this.currentBottle = this.B1PackagingTray.q.peek();
                                this.B1PackagingTray.q.remove();
                                rel.unlock();
                                this.initial = true;
                            }
                            else{
                                rel.lock();
                                this.currentBottle = this.B2PackagingTray.q.peek();
                                this.B2PackagingTray.q.remove();
                                rel.unlock();
                                this.initial=false;
                            }
                        }
                        /*If B2 is empty take from B1*/
                        else if(this.B1PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B1PackagingTray.q.peek();
                            this.B1PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=true;
                        }
                        /*If B1 is empty take from B2*/
                        else if(this.B2PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B2PackagingTray.q.peek();
                            this.B2PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=false;
                        }
                        /*switch on the flag to show that a bottle is curretly being processed*/
                        this.empty=false;
                        System.out.println("Bottle drawn by Packaging Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
                    }
                    /*Set the processing time to 1 as this second is counted as processing is being done for the bottle*/
                    this.processingTime = 1;
                }
            }

            /*Call the await method to wait from other threads to arrive*/
			phsr.arriveAndAwaitAdvance(); 

            /*Make the thread sleep to provide sometime to do the pending processing at the end of the phase or second*/
			try { 
				Thread.sleep(50); 
			} 
			catch (InterruptedException e) { 
				System.out.println(e); 
			} 
		} 
	} 
} 