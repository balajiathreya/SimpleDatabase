package com.balaji;

import java.util.TreeMap;

/**
 * Created by baathreya on 7/27/15.
 */
public class Transaction {

    private TreeMap<String,Integer> tempStore = new TreeMap();

    private TreeMap<Integer,Integer> valueIndex = new TreeMap();

    public TreeMap<String, Integer> getTempStore() {
        return tempStore;
    }

    public TreeMap<Integer, Integer> getValueIndex() {
        return valueIndex;
    }


}
