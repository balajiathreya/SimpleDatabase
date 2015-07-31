package com.balaji;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by baathreya on 7/27/15.
 */
public class DataStore {

    private TreeMap<String, Integer> store = new TreeMap();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    /*
    TreeMap guarantees log(n) worst-case time for get and containsKey operation. So, the get method has a time
    complexity of O(2 * log(n)) = O(log(n))
     */
    protected Integer get(String variable){
        if(currentTransaction != null && currentTransaction.getTempStore().containsKey(variable)){
            return currentTransaction.getTempStore().get(variable);
        }
        else if(store.containsKey(variable)){
            return store.get(variable);
        }
        return null;
    }

    /*
    TreeMap guarantees log(n) worst-case time for put operation. So, the put method below meets the requirement of
    O(log(N))
     */
    protected void set(String variable, Integer value){
        if(currentTransaction != null){
            currentTransaction.getTempStore().put(variable,value);
        }
        else {
            store.put(variable, value);
        }
    }

    /*
    TreeMap guarantees log(n) worst-case time for put operation. So, the unset method below meets the requirement of
    O(log(N))
     */
    protected void unset(String variable){
        if(currentTransaction != null){
            currentTransaction.getTempStore().put(variable,null);
        }
        else {
            store.put(variable, null);
        }
    }

    /*
        This operation will take linear time which doesn't meet the requirement of log(N). :(
    */
    protected int numEqualTo(Integer value){
        int count = 0;
        if(currentTransaction.getTempStore().containsValue(value)){
            Set<String> keySet = currentTransaction.getTempStore().keySet();
            for(String str : keySet) {
                if(currentTransaction.getTempStore().get(str).equals(value)) {
                    count++;
                }
            }
        }
        if(store.containsValue(value)) {
            Set<String> keySet = store.keySet();
            for(String str : keySet) {
                if(store.get(str).equals(value) && !currentTransaction.getTempStore().containsKey(str)) {
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
            newTransanction.getTempStore().putAll(currentTransaction.getTempStore());   // inherit previous transaction values
            currentTransaction = newTransanction;
        }
        // nested transaction
        else if(transactions.size() != 0) {
            Transaction mostRecentTransaction = transactions.get(transactions.size() - 1);
            Transaction currentTransaction = new Transaction();
            currentTransaction.getTempStore().putAll(mostRecentTransaction.getTempStore());   // inherit previous transaction values
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
                TreeMap temp = transaction.getTempStore();
                store.putAll(temp);
            }
            transactions = new ArrayList<Transaction>();
        }
        // commit the current transaction
        if(currentTransaction != null && currentTransaction.getTempStore().size() > 0) {
            store.putAll(currentTransaction.getTempStore());
            currentTransaction = new Transaction();
        }
    }
}
