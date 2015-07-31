package com.balaji;

import java.util.TreeMap;

/**
 * Created by baathreya on 7/27/15.
 *
 * Each transaction object contains a Treemap in it. This Treemap is merged into the primary Treemap
 * on commit.
 */
public class Transaction {

    private TreeMap<String,Integer> dataStore = new TreeMap();

    public TreeMap<Integer, Integer> getNumberFrequencyIndex() {
        return numberFrequencyIndex;
    }

    private TreeMap<Integer, Integer> numberFrequencyIndex = new TreeMap<Integer, Integer>();

    public TreeMap<String, Integer> getDataStore() {
        return dataStore;
    }


}
