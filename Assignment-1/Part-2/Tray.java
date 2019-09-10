/**
 * Author: B.T. Langulya
 * Summary: This module contains the Tray class to store the Tray information.
 */

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;


class Tray{
    
    /*Type of tray*/
    int id;

    /*Variable to store the size of the tray*/
    int size;

    /*Queue of stored bottles*/
    Queue<Bottles> q; 

    /*Initialize the tray*/
    Tray(int id,int size){
        this.id = id;
        this.size = size;
        q = new LinkedList<>(); 
    }
}