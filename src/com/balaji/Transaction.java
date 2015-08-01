package com.balaji;

import java.util.TreeMap;

/**
 * Created by baathreya on 7/27/15.
 *
 */
public class Transaction {

    private TreeMap<String,Integer> dataStore = new TreeMap();

    private TreeMap<Integer, Integer> countIndex = new TreeMap<Integer, Integer>();

    public TreeMap<String, Integer> getDataStore() {
        return dataStore;
    }


    public TreeMap<Integer, Integer> getCountIndex() {
        return countIndex;
    }

    public void updateDataStoreAndIndex(String variable, Integer newValue, Integer curPrimaryVal
            , TreeMap<Integer, Integer> primaryCountIndex) {
        Integer curVal = dataStore.get(variable);
        // variable is not in transaction datastore
        if(curVal == null) {
            if(newValue == null) {
                if(curPrimaryVal != null){
                    dataStore.put(variable, newValue);
                    decrementCount(curPrimaryVal, primaryCountIndex);
                }
            }
            // variable is not in both transaction and primary datastore
            else if(curPrimaryVal == null){
                dataStore.put(variable, newValue);
                incrementCount(newValue, primaryCountIndex);
            }
            // variable's new value is same as its existing value in primary datastore, do nothing
            else if(newValue.equals(curPrimaryVal)){
                return;
            }
            // variable's new value is different from its existing value in primary datastore
            else {
                dataStore.put(variable, newValue);
                decrementCount(curPrimaryVal, primaryCountIndex);
                incrementCount(newValue, primaryCountIndex);
            }
        }
        // variable is in transaction datastore
        else {
            if(newValue == null) {
                dataStore.put(variable, newValue);
                decrementCount(curVal, primaryCountIndex);
                if(curPrimaryVal != null){
                    decrementCount(curPrimaryVal, primaryCountIndex);
                }
            }
            // variable is in transaction and is different from the current value
            else if(!newValue.equals(curVal)){
                dataStore.put(variable, newValue);
                decrementCount(curVal, primaryCountIndex);
                incrementCount(newValue, primaryCountIndex);
            }
            // variable is in transaction and is same as the current value, do nothing
        }
    }

    private void incrementCount(Integer value, TreeMap<Integer, Integer> primaryCountIndex){
        Integer count = countIndex.get(value);
        // this value has not been seen in this transaction. Get the count for this value from the
        // primary index, increment it and use it
        if(count == null){
            Integer primaryCount = primaryCountIndex.get(value);
            int c = primaryCount == null ? 1 : primaryCount + 1;
            countIndex.put(value,c);
        }
        // this value is already there in this transaction. simple increment
        else {
            countIndex.put(value,++count);
        }
    }

    private void decrementCount(Integer value, TreeMap<Integer, Integer> primaryCountIndex){
        Integer count = countIndex.get(value);
        // this value has not been seen in this transaction. Get the count for this value from the
        // primary index, decrement it and use it
        if(count == null){
            Integer primaryCount = primaryCountIndex.get(value);
            int c = primaryCount == null ? 0 : primaryCount - 1;
            countIndex.put(value,c);
        }
        else if(count == 1){
            countIndex.put(value,null);
        }
        // this value is already there in this transaction. simple decrement
        else {
            countIndex.put(value,--count);
        }
    }




}
