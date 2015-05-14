package com.parse.starter;

/**
 * Created by Andersson on 08/05/15.
 */

import java.util.ArrayList;
import java.util.Collections;


@SuppressWarnings("serial")
public class SortedList<T extends Comparable<? super T>> extends ArrayList<T> {

    public synchronized boolean add(T e){

        super.add(e);
        Collections.sort(this);
        return true;
    }
    public synchronized T get(int e){

        return super.get(e);
    }
    public int length(){

        return super.size();
    }
    public synchronized T remove(int e){

        return super.remove(e);
    }
    public synchronized void sort(){

        Collections.sort(this);
    }
    public String toString(){

        return super.toString();
    }



}