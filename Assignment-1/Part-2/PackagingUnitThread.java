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
    boolean initial;
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    UnfinishedTray unfinishedTray;
    int processingTime;
    GoDown goDown;
    int currBottleToDraw;
    boolean empty;
    int b1;
    int b2;
    boolean pending;
    ReentrantLock rel;
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
        this.currBottleToDraw = 1;
        this.empty=true;
        this.pending=false;
        b1=0;
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
            
            // System.out.println("Currently Processing Bottle :" + Integer.toString(this.currentBottle.id));
            this.processingTime++;
            if(this.empty==false && this.processingTime<2){
                System.out.println("Packaging Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=2 && this.empty==false){
                    if(this.currentBottle.packagingStatus==false){
                        this.currentBottle.packagingStatus =true;
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Packaging DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                    }
                    else{
                        System.out.println("IDLEEEEEEEEE");
                    }
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
                    else if(this.sealingTray.q.size() < this.sealingTray.size){
                        this.pending=true;
                        this.empty=true;
                    }
                   
                }
                else if(this.empty==true){
                    if(this.B1PackagingTray.q.size()==0 && this.B2PackagingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0 ){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            System.out.println("Bottle drawn by Packaging Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
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
                        }
                    }
                    else{
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
                        else if(this.B1PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B1PackagingTray.q.peek();
                            this.B1PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=true;
                        }
                        else if(this.B2PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B2PackagingTray.q.peek();
                            this.B2PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=false;
                        }
                        this.empty=false;
                        System.out.println("Bottle drawn by Packaging Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
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