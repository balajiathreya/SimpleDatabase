package com.balaji;

import java.util.*;

/**
 * Created by baathreya on 7/27/15.
 */
public class DataStore {

    private TreeMap<String, Integer> primaryStore = new TreeMap();
    private TreeMap<Integer, Integer> primaryNumberFrequencyIndex = new TreeMap<Integer, Integer>();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    /*
    TreeMap guarantees log(n) worst-case time for get operation. So, the get method has a time
    complexity of O(2 * log(n)) = O(log(n)) in the worst case
     */
    protected Integer get(String variable){
        Integer value = null;
        if(currentTransaction != null) {
            value = currentTransaction.getDataStore().get(variable);
        }
        if(value == null) {
            return primaryStore.get(variable);
        }
        return null;
    }

    /*
    TreeMap guarantees log(n) worst-case time for get and put operation. So, the set method has a time complexity of
    O(3 * log N) = O(log N) in the worst case
     */
    protected void set(String variable, Integer value){
        TreeMap<String, Integer> operatingDataStore = getOperatingDataStore();
        TreeMap<Integer, Integer> operatingNumberFrequencyIndex = getOperatingNumberCountIndex();
        Integer currentVal = operatingDataStore.get(variable);
        decrementNumFrequency(operatingNumberFrequencyIndex, currentVal);
        operatingDataStore.put(variable, value);
        incrementNumFrequency(operatingNumberFrequencyIndex, value);
    }

    /*
    TreeMap guarantees log(n) worst-case time for put operation. So, the unset method below meets the requirement of
    O(log(N))
     */
    protected void unset(String variable){
        TreeMap<String, Integer> operatingDataStore = getOperatingDataStore();
        operatingDataStore.put(variable, null);
    }

    /*
        This operation will take linear time which doesn't meet the requirement of log(N). :(
        2 + O(N) for looping through the keyset to get count.

    */
    protected Integer numEqualTo(Integer value){
        Integer count = primaryNumberFrequencyIndex.get(value);
        if(currentTransaction != null){
            Integer transactionCount = currentTransaction.getNumberFrequencyIndex().get(value);
            if(transactionCount == null){
                return count == null ? 0 : count;
            }
            else {
                return count + transactionCount;
            }
        }
        return count == null ? 0 : count;
    }

    protected void begin(){
        // second transaction
        if(currentTransaction != null){
            transactions.add(currentTransaction);
            Transaction newTransanction = new Transaction();
            // inherit previous transaction values
            newTransanction.getDataStore().putAll(currentTransaction.getDataStore());
            newTransanction.getNumberFrequencyIndex().putAll(currentTransaction.getNumberFrequencyIndex());
            currentTransaction = newTransanction;
        }
        // nested transaction
        else if(transactions.size() != 0) {
            Transaction mostRecentTransaction = transactions.get(transactions.size() - 1);
            Transaction currentTransaction = new Transaction();
            // inherit previous transaction values
            currentTransaction.getDataStore().putAll(mostRecentTransaction.getDataStore());
            currentTransaction.getNumberFrequencyIndex().putAll(currentTransaction.getNumberFrequencyIndex());
        }
        // new transaction
        else {
            currentTransaction = new Transaction();
        }
    }

    // simply delete the current transaction
    protected void rollback(){
        if(currentTransaction != null){
            if(transactions.size() == 0){
                currentTransaction = null;
            } else {
                currentTransaction = transactions.remove(transactions.size() - 1);
            }
        }
        else {
            System.out.println("NO TRANSACTION");
        }
    }

    protected void commit(){
        // nested transaction. commit all of them in order
        if(transactions != null && transactions.size() > 0){
            for(Transaction transaction: transactions) {
                TreeMap temp = transaction.getDataStore();
                primaryStore.putAll(temp);
            }
            transactions = new ArrayList<Transaction>();
        }
        // commit the current transaction and update primaryNumberFrequencyIndex
        if(currentTransaction != null && currentTransaction.getDataStore().size() > 0) {
            primaryStore.putAll(currentTransaction.getDataStore());
            addToValueIndex(currentTransaction.getNumberFrequencyIndex());
            currentTransaction = null;
        }
    }



    private void incrementNumFrequency(TreeMap<Integer, Integer> numFrequency, Integer value) {
        Integer count = null;
        if(value != null) {
            count = numFrequency.get(value);
        }

        if(count != null) {
            numFrequency.put(value, ++count);
        }
        else {
            numFrequency.put(value, 1);
        }
    }

    private void decrementNumFrequency(TreeMap<Integer, Integer> numFrequency, Integer value) {
        if(value != null){
            Integer count = numFrequency.get(value);
            if(count != null) {
                numFrequency.put(value, --count);
            }
            else {
                numFrequency.put(value, -1);
            }
        }
    }


    private void addToValueIndex(TreeMap<Integer, Integer> transactionNumberFrequencyIndex) {
        for(Map.Entry<Integer,Integer> entry: transactionNumberFrequencyIndex.entrySet()){
            Integer count = primaryNumberFrequencyIndex.get(entry.getKey());
            if(count != null){
                primaryNumberFrequencyIndex.put(entry.getKey(), count + entry.getValue());
            }
        }
    }


    private TreeMap<String, Integer> getOperatingDataStore(){
        if(currentTransaction != null){
            return currentTransaction.getDataStore();
        }
        return primaryStore;
    }

    private TreeMap<Integer, Integer> getOperatingNumberCountIndex(){
        if(currentTransaction != null){
            return currentTransaction.getNumberFrequencyIndex();
        }
        return primaryNumberFrequencyIndex;
    }
}
