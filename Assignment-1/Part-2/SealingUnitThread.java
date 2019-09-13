/*
 * Author: Mukul Verma
 * Summary:  This module contains the SealingUnit Class which simulates the working of the Sealing Unit. 
 */

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

class SealingUnitThread implements Runnable { 
	Phaser phsr; 
    String name; 

    /*Variables to store the instances of the bottles,the trays and the Godown*/
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    UnfinishedTray unfinishedTray;
    GoDown goDown;

     /*Variable to store the processing time of the current bottle*/
    int processingTime;

    /*Boolean to show whether the Sealing unit is free or not*/
    boolean empty;

    /*Variables to store the count of bottles packaged by the Packaging Thread*/
    int b1;
    int b2;
    
    /*Variable to store the current bottle to be drawn by the Packaging Unit from the Unfinished tray*/
    int currBottleToDraw;

    /*Variable to store whether the Packaging Unit needs to wait if the sealing Tray is full*/
    boolean pending;

    /*Re-entrant Lock instance*/
    ReentrantLock rel;

    /*Initialize the Sealing Unit*/
    SealingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown,ReentrantLock rel)
	{ 
		phsr = p; 
        this.name = name; 
        this.currentBottle = bottle;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.unfinishedTray = unfinishedTray;
        this.processingTime = 0;
        this.goDown = goDown;

        /*Initialize the sealing unit to start with  B2 type of bottles*/
        this.currBottleToDraw =  2;
        this.empty=true;
        this.pending=false;
        this.rel=rel;
        b1 = 0;
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
			System.out.println("Thread " + name + " Beginning Phase "+ phsr.getPhase()); 
            
            /*Increase processing time of the bottle currently being packaged*/
            this.processingTime++;

             /*If the sealing unit hasnt finished the sealing then continue*/
            if(this.empty==false && this.processingTime<3){
                System.out.println("Sealing Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                
                /*If packaging unit is finished with packaging of the bottle*/
                if(this.processingTime>=3 && this.empty==false){

                     /*set that the sealing is done for that bottle*/
                    if(this.currentBottle.sealingStatus==false){
                        
                        /*Increase the count*/
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Sealing DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                        this.currentBottle.sealingStatus= true;
                    }
                    else
                    {
                        /*else sealing unit is idle*/
                        System.out.println("Sealing unit is IDLE");
                    }
                    
                    /*If packaging is also done then transfer the file to godown else push into required tray accordingly if
                    * has some space.
                    */
                    if(this.currentBottle.packagingStatus == true){
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
                        this.empty = true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    /*Switch on the flag to show that packaging unit is idle and bottles needs to be pushed into the tray
                    * at the end of the second.
                    * Switch on the empty flag to show that currently packaging unit is not processing anything
                    */
                    else if(this.currentBottle.type == 1 && this.B1PackagingTray.q.size() < this.B1PackagingTray.size)
                    {   
                         
                        this.empty=true;
                        this.pending=true;
                    }  
                    else if(this.currentBottle.type == 2 && this.B2PackagingTray.q.size() < this.B2PackagingTray.size){
                        this.pending=true;
                        this.empty = true;
                    }      
                }
                /*If sealing unit is processing nothing and is not holding any bottle*/
                else if(this.empty ==true){
                    
                     /*If there is nothing in the sealing tray, take bottle from unfinished tray if available*/
                    if(this.sealingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;

                            /*Decrease the count of the bottle chosen and set the next bottle that will be withdrawn, accordingly*/
                            if(this.currentBottle.type==1) {
                                rel.lock();
                                unfinishedTray.totalB1--;
                                rel.unlock();
                                if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                            }
                            else{
                                rel.lock();
                                unfinishedTray.totalB2--;
                                rel.unlock();
                                if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                            }
                            /*switch on the flag to show that a bottle is curretly being processed*/
                            this.empty=false;
                            System.out.println("Bottle drawn by Sealing Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                        }
                    }
                    /**
                     * Take the bottle from the sealing tray
                     */
                    else{
                        rel.lock();
                        this.currentBottle = this.sealingTray.q.peek();
                        this.sealingTray.q.remove();
                        rel.unlock();
                        this.empty=false;
                        System.out.println("Bottle drawn by Sealing Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
  
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