package com;

import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  

import com.*;
class Tray{
    int id;
    int size;
    Queue<Bottles> q; 

    Tray(int id,int size){
        this.id = id;
        this.size = size;
        q = new LinkedList<>(); 
    }
}