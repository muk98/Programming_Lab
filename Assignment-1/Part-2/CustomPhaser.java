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
      
    /*Override the onAdvance method of the Phasor superclass*/
	@Override
	protected boolean onAdvance(int phase, 
								int registeredParties) 
	{ 
        
        /* At the end of the second, if Packaging unit has completed the packaging of a bottle
        *  then push the bottle into the sealing tray.
        */
        if(pkg.pending){
            rel.lock();
            pkg.sealingTray.q.add(pkg.currentBottle);
            System.out.println("transferred to sealing Tray "+ Integer.toString(pkg.currentBottle.type) + " "+  Integer.toString(pkg.currentBottle.id));
            pkg.pending=false;
            rel.unlock();
        }
 
         /* At the end of the second, if Sealing unit has completed the sealing of a bottle
        *  then push the bottle into the packaging trays respectively.
        */
        if(slg.pending)
        {
            rel.lock();
            slg.pending=false;
            if(slg.currentBottle.type == 1){
                slg.B1PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B1 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
            }
            else if(slg.currentBottle.type == 2){
                slg.B2PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B2 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
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
