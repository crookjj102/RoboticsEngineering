package edu.rosehulman.integratedimagerec;

import java.util.Arrays;

/**
 * Created by crook on 5/16/2016.
 */
public class CircularMovingAverageArray
{
    private double[] circularArray;
    private int currentIndex;
    private int sizeOfArray;
    private int sumOfElements;

    public CircularMovingAverageArray(int size){
        sizeOfArray = size;
        circularArray = new double[size];
        Arrays.fill(circularArray, 0);
        currentIndex = 0;
        sumOfElements = 0;
    }

    public void addNewData(double data){
        if(currentIndex>sizeOfArray-1){
            currentIndex = 0;
        }
        sumOfElements -= circularArray[currentIndex];//remove element that is about to be erased from the sum.
        circularArray[currentIndex++] = data;
        sumOfElements += data;//add to the sum; makes getMovingAverage O(1).
    }
    public double getMovingAverage(){
       return sumOfElements/((double)sizeOfArray);//although we could worry about the first few samples where
        //the array isn't full, it's really not worth it. The sensors warm up for ~30 s anyway.
    }

    public void clearArray(){
        Arrays.fill(circularArray,0);
        sumOfElements = 0;
    }
}
