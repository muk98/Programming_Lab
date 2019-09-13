/**
 * Author: B.T. Langulya
 * Summary: This Module contains the Phasor class which synchronizes the working of Packaging and Sealing threads.
 */

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;


/*Phasor class*/
class CustomPhaser extends Phaser { 

    /*Variable to store the seconds*/
    int numPhases;
    PackagingUnitThread pkg;
    SealingUnitThread slg;
    ReentrantLock rel;
    
    /* Initialize the Phasor object
    *  Parties equal to the number of threads registered to the Phasor.
    */
	CustomPhaser(int parties, int phaseCount,ReentrantLock rel) 
	{   
        /*Initialize the phasor superclass*/
        super(parties);
        
        /*Initialize the total seconds for which the system needs to work*/
        numPhases = phaseCount - 1; 
        this.rel = rel;
	} 
    
    /*Initialize the packaging and Sealing threads*/
    void set(PackagingUnitThread pkg,SealingUnitThread slg){
        this.pkg = pkg;
        this.slg = slg;  
    }
      
    /** Override the onAdvance method of the Phasor superclass
     * @param phase is the second of processing
     * @param registeredParties is the number of parties it expects before proceesing
    */
	@Override
	protected boolean onAdvance(int phase, 
								int registeredParties) 
	{ 
        
        /* At the end of the second, if Packaging unit has completed the packaging of a bottle
        *  then push the bottle into the sealing tray.
        */

        if(pkg.pending && slg.pending){
            boolean flag=false;
            if((slg.currentBottle.type == 1 && slg.B1PackagingTray.q.size() >= slg.B1PackagingTray.size) ||
                (slg.currentBottle.type == 2 && slg.B2PackagingTray.q.size() >= slg.B2PackagingTray.size)){
                flag=true;
            }
            if(pkg.sealingTray.q.size() >= pkg.sealingTray.size  && flag){

            }
            else{
               
                if(pkg.sealingTray.q.size() < pkg.sealingTray.size)
                {
                    pkg.sealingTray.q.add(pkg.currentBottle);

                    if(slg.currentBottle.type==1)
                    {
                        pkg.currentBottle = pkg.B1PackagingTray.q.peek();
                        pkg.B1PackagingTray.q.remove();
                        slg.B1PackagingTray.q.add(slg.currentBottle);
                    }   
                    else{
                        pkg.currentBottle = pkg.B2PackagingTray.q.peek();
                        pkg.B2PackagingTray.q.remove();
                        slg.B2PackagingTray.q.add(slg.currentBottle);
                    }
                    slg.currentBottle = slg.sealingTray.q.peek();
                    slg.sealingTray.q.remove();

                }
                else
                {
                
                    if(slg.currentBottle.type==1)
                    {
                        slg.B1PackagingTray.q.add(slg.currentBottle);
                        slg.currentBottle = slg.sealingTray.q.peek();
                        slg.sealingTray.q.remove();
                        pkg.sealingTray.q.add(pkg.currentBottle);
                        pkg.currentBottle = pkg.B1PackagingTray.q.peek();
                        pkg.B1PackagingTray.q.remove();
                    }   
                    else{
                        slg.B2PackagingTray.q.add(slg.currentBottle);
                        slg.currentBottle = slg.sealingTray.q.peek();
                        slg.sealingTray.q.remove();
                        pkg.sealingTray.q.add(pkg.currentBottle);
                        pkg.currentBottle = pkg.B2PackagingTray.q.peek();
                        pkg.B1PackagingTray.q.remove();
                    }
 
                }
                slg.processingTime=0;
                slg.pending = false;
                slg.empty=false;
                pkg.processingTime=0;
                pkg.pending = false;
                pkg.empty=false;        
            }
        }
        else if(pkg.pending){
            if(pkg.sealingTray.q.size() < pkg.sealingTray.size)
            {
                rel.lock();
                pkg.sealingTray.q.add(pkg.currentBottle);
                System.out.println("transferred to sealing Tray "+ Integer.toString(pkg.currentBottle.type) + " "+  Integer.toString(pkg.currentBottle.id));
                pkg.pending=false;
                /*Switch on the empty flag to show that currently packaging unit is not processing anything*/
                pkg.empty=true;
                rel.unlock();
            }
            else
            {
                pkg.empty=false;
                pkg.pending=false;
            }
        }

        /* At the end of the second, if Sealing unit has completed the sealing of a bottle
        *  then push the bottle into the packaging trays respectively.
        */
        else if(slg.pending)
        {
            rel.lock();
            slg.pending=false;
            if(slg.currentBottle.type == 1 && slg.B1PackagingTray.q.size() < slg.B1PackagingTray.size)
            {   
                slg.B1PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B1 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
                slg.empty=true;
                
            }  
            else if(slg.currentBottle.type == 2 && slg.B2PackagingTray.q.size() < slg.B2PackagingTray.size){
               
                slg.empty = true;
                slg.B2PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B2 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
            }
            else{

            }     
            rel.unlock();
        }          
        

        System.out.println("Phase "
						+ phase 
						+ " completed.\n"); 

        /*Return true if the time alloted is finished and system needs to be stopped*/
		if (phase == numPhases 
			|| registeredParties == 0) { 
			return true; 
        } 
        
		/*otherwise, return false*/ 
		return false; 
	} 
} 
