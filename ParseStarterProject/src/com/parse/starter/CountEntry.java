package com.parse.starter;

/**
 * Created by Andersson on 08/05/15.
 */

public class CountEntry implements Comparable<CountEntry> {

    public double sum;
    public String payToName;
    public String name;

    public CountEntry(Double sum, String name) {

        this.sum = sum;
        this.name = name;
        this.payToName = "NoName";

    }

    public CountEntry(double d, String name, String payToName) {
        this.sum = d;
        this.payToName = payToName;
        this.name = name;

    }

    public int compareTo(CountEntry e) {

        return (int) (getSum() - e.getSum());

    }
    public double getSum(){

        return sum;
    }
    public void setSum(double d){

        sum = d;
    }
    public String getName(){

        return name;
    }
    public String getPayTo(){

        return payToName;
    }

    @Override
    public String toString() {
        return getName() + " ska ge " + getSum() + " till " + getPayTo();

    }
}

