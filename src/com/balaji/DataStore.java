package com.balaji;

import java.util.*;

/**
 * Created by baathreya on 7/27/15.
 *
 *
 */

public class DataStore {

    private TreeMap<String, Integer> primaryDataStore = new TreeMap<String, Integer>();
    private TreeMap<Integer, Integer> primaryCountIndex = new TreeMap<Integer, Integer>();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    /*
    TreeMap guarantees log(n) worst-case time for get and containsKey operation. So, this method has a time
    complexity of O(log(n)) + O(log(n)) = O(log(n))
     */
    protected Integer get(String variable){
        if(currentTransaction != null && currentTransaction.getDataStore().containsKey(variable)) {
            return currentTransaction.getDataStore().get(variable);
        }
        return primaryDataStore.get(variable);
    }

    /*
    TreeMap guarantees log(n) worst-case time for get and put operation.
    Even though get and put operations are called several times within this method, there are no loops and the time
     complexity of this method = x times O(log N) = O(log N)

     */
    protected void set(String variable, Integer newValue){
        Integer curPrimaryVal = primaryDataStore.get(variable);
        // no transactions present. directly update primary data structures
        if(currentTransaction == null){
            updatePrimaryDataStoreAndIndex(variable, newValue, curPrimaryVal);
        }
        // we are inside a transaction. update the data structures inside the transaction object.
        else {
            currentTransaction.updateDataStoreAndIndex(variable, newValue, curPrimaryVal, primaryCountIndex);
        }
    }
    /*
        TreeMap guarantees log(n) worst-case time for get operation.
        This operation involves two Treemap.get in the worst case. Time complexity is O(log N) + O(log N) = O(log N)
         at worst
    */
    protected int numEqualTo(Integer value){
        if(currentTransaction != null) {
            Integer count = currentTransaction.getCountIndex().get(value);
            if(count != null){
                return count;
            }
        }
        Integer count = primaryCountIndex.get(value);
        return count == null ? 0 : count;
    }

    protected void begin(){
        // second transaction
        if(currentTransaction != null){
            transactions.add(currentTransaction);
            Transaction newTransanction = new Transaction();
            // inherit previous transaction values
            newTransanction.getDataStore().putAll(currentTransaction.getDataStore());
            newTransanction.getCountIndex().putAll(currentTransaction.getCountIndex());
            currentTransaction = newTransanction;
        }
        // nested transaction and current transaction was recently rolledback
        else if(transactions.size() != 0) {
            Transaction mostRecentTransaction = transactions.get(transactions.size() - 1);
            currentTransaction = new Transaction();
            // inherit values from the most recent transaction
            currentTransaction.getDataStore().putAll(mostRecentTransaction.getDataStore());
            currentTransaction.getCountIndex().putAll(currentTransaction.getCountIndex());
        }
        // first transaction
        else {
            currentTransaction = new Transaction();
        }
    }

    // simply delete the current transaction
    protected void rollback(){
        if(currentTransaction != null){
            currentTransaction = null;
            if(transactions.size() > 0){
                currentTransaction = transactions.remove(transactions.size() - 1);
            }
        }
        else {
            System.out.println("NO TRANSACTION");
        }
    }

    protected void commit(){
        // commit the current transaction
        if(currentTransaction != null) {
            primaryDataStore.putAll(currentTransaction.getDataStore());
            primaryCountIndex.putAll(currentTransaction.getCountIndex());
            currentTransaction = null;
            transactions.clear();
        }
        else{
            System.out.println("NO TRANSACTION");
        }
    }

    private void updatePrimaryDataStoreAndIndex(String variable, Integer newValue, Integer curPrimaryVal) {
        if(newValue == null) {
            primaryDataStore.put(variable, newValue);
            if(curPrimaryVal != null) {
                decrementPrimaryCount(curPrimaryVal);
            }
        }
        else if(curPrimaryVal == null) {
            primaryDataStore.put(variable, newValue);
            incrementPrimaryCount(newValue);
        }
        else if(!curPrimaryVal.equals(newValue)){
            primaryDataStore.put(variable, newValue);
            decrementPrimaryCount(curPrimaryVal);
            incrementPrimaryCount(newValue);
        }
        // do nothing if curPrimarVal==newValue
    }

    private void decrementPrimaryCount(Integer curPrimaryVal) {
        Integer curValCount = primaryCountIndex.get(curPrimaryVal);
        if(curValCount == 1){
            primaryCountIndex.put(curPrimaryVal, null);
        }
        else {
            primaryCountIndex.put(curPrimaryVal, --curValCount);
        }
    }

    private void incrementPrimaryCount(Integer newValue) {
        Integer newValueCount = primaryCountIndex.get(newValue);
        if(newValueCount == null) {
            primaryCountIndex.put(newValue, 1);
        }
        else {
            primaryCountIndex.put(newValue, ++newValueCount);
        }
    }


}
