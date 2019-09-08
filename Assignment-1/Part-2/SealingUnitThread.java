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
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    UnfinishedTray unfinishedTray;
    int processingTime;
    boolean empty;
    int b1;
    int b2;
    GoDown goDown;
    int currBottleToDraw;
    boolean pending;
    ReentrantLock rel;

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
        this.currBottleToDraw =  2;
        this.empty=true;
        this.pending=false;
        this.rel=rel;
        b1 = 0;
        b2=0;
		phsr.register(); 
		new Thread(this).start(); 
	}  

	@Override
	public void run() 
	{ 
		while (!phsr.isTerminated()) { 
			System.out.println("Thread " + name 
							+ " Beginning Phase "
							+ phsr.getPhase()); 
            
            this.processingTime++;
            if(this.empty==false && this.processingTime<3){
                System.out.println("Sealing Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=3 && this.empty==false){
                    if(this.currentBottle.sealingStatus==false){
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Sealing DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                        this.currentBottle.sealingStatus= true;
                    }
                    else
                    {
                        System.out.println("Idleeee");
                    }

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
                else if(this.empty ==true){
                    
                    if(this.sealingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
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
                            this.empty=false;
                            System.out.println("Bottle drawn by Sealing Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                        }
                    }
                    else{
                        rel.lock();
                        this.currentBottle = this.sealingTray.q.peek();
                        this.sealingTray.q.remove();
                        rel.unlock();
                        this.empty=false;
                        System.out.println("Bottle drawn by Sealing Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
  
                    }
                    this.processingTime = 1;
                }
            }
            phsr.arriveAndAwaitAdvance(); 

			try { 
				Thread.sleep(50); 
			} 
			catch (InterruptedException e) { 
				System.out.println(e); 
			} 
		} 
	} 
} 