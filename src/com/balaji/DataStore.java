package com.balaji;

import java.util.*;

/**
 * Created by baathreya on 7/27/15.
 */
public class DataStore {

    private TreeMap<String, Integer> primaryDataStore = new TreeMap();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    /*
    TreeMap guarantees log(n) worst-case time for get and containsKey operation. So, the get method has a time
    complexity of 2 * O(log(n)) = O(log(n))
     */
    protected Integer get(String variable){
        if(currentTransaction != null && currentTransaction.getDataStore().containsKey(variable)) {
            return currentTransaction.getDataStore().get(variable);
        }
        return primaryDataStore.get(variable);
    }

    /*
    TreeMap guarantees log(n) worst-case time for put operation. So, the put method below meets the requirement of
    O(log(N)) for set and unset operations
     */
    protected void set(String variable, Integer value){
        if(currentTransaction != null){
            currentTransaction.getDataStore().put(variable,value);
        }
        else {
            primaryDataStore.put(variable, value);
        }
    }

    /*
        This operation will take linear time which doesn't meet the requirement of log(N). :(
        Time complexity for this method is O(N) + O(N)*O(log N) = O(N) in the worst case
    */
    protected int numEqualTo(Integer value){
        int count = 0;
        if(currentTransaction != null) {
            for(Map.Entry<String, Integer> entry : currentTransaction.getDataStore().entrySet()){
                if(value.equals(entry.getValue())){
                    count++;
                }
            }
        }

        for(Map.Entry<String, Integer> entry : primaryDataStore.entrySet()){
            if(value.equals(entry.getValue())){
                if(currentTransaction == null){
                    count++;
                }
                else if(!currentTransaction.getDataStore().containsKey(entry.getKey())){
                    count++;
                }
            }
        }
        return count;
    }

    protected void begin(){
        // second transaction
        if(currentTransaction != null){
            transactions.add(currentTransaction);
            Transaction newTransanction = new Transaction();
            newTransanction.getDataStore().putAll(currentTransaction.getDataStore());   // inherit previous transaction values
            currentTransaction = newTransanction;
        }
        // nested transaction
        else if(transactions.size() != 0) {
            Transaction mostRecentTransaction = transactions.get(transactions.size() - 1);
            Transaction currentTransaction = new Transaction();
            currentTransaction.getDataStore().putAll(mostRecentTransaction.getDataStore());   // inherit previous transaction values
        }
        // first transaction
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
                primaryDataStore.putAll(temp);
            }
            transactions = new ArrayList<Transaction>();
        }
        // commit the current transaction
        if(currentTransaction != null && currentTransaction.getDataStore().size() > 0) {
            primaryDataStore.putAll(currentTransaction.getDataStore());
            currentTransaction = null;
        }
    }
}
