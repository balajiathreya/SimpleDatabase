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
    private TreeMap<Integer, Integer> valueIndex = new TreeMap();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    /*
    TreeMap guarantees log(n) worst-case time for get and containsKey operation. So, the get method below
    with 2 containsKey and 2 get method will offer O(2 * log(n)) = O(log(n)) in the worst case
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
    TreeMap guarantees log(n) worst-case time for put operation. So, the put method below meets the requirements
     */
    protected void set(String variable, Integer value){
        if(currentTransaction != null){
            currentTransaction.getTempStore().put(variable,value);
            if(currentTransaction.getValueIndex().containsKey(value)){
                int count = currentTransaction.getValueIndex().get(value);
                currentTransaction.getValueIndex().put(value,++count);
            }
        }
        else {
            store.put(variable, value);
            valueIndex.put(value, 1);
        }
    }

    /*
    TreeMap guarantees log(n) worst-case time for put operation. So, the unset method below meets the requirements
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

    */
    protected Integer numEqualTo(String value){
        return null;
    }

    protected void begin(){
        if(currentTransaction != null){
            transactions.add(currentTransaction);
            Transaction newTransanction = new Transaction();
            newTransanction.getTempStore().putAll(currentTransaction.getTempStore());
            currentTransaction = newTransanction;
        }
        else if(transactions.size() != 0) {
            Transaction mostRecentTransaction = transactions.get(transactions.size() - 1);
            Transaction currentTransaction = new Transaction();
            currentTransaction.getTempStore().putAll(mostRecentTransaction.getTempStore());
        }
        else {
            currentTransaction = new Transaction();
        }
    }

    protected void rollback(){
        if(currentTransaction != null){
            if(transactions.size() == 0){
                currentTransaction = null;
            }
            else {
                currentTransaction = transactions.remove(transactions.size() - 1);
            }
        }
        else {
            System.out.println("NO TRANSACTION");
        }
    }

    protected void commit(){
        if(transactions != null && transactions.size() > 0){
            for(Transaction transaction: transactions) {
                TreeMap temp = transaction.getTempStore();
                store.putAll(temp);
            }
            transactions = new ArrayList<Transaction>();
        }
        if(currentTransaction != null && currentTransaction.getTempStore().size() > 0) {
            store.putAll(currentTransaction.getTempStore());
        }
    }

    private void updateIndex(Transaction transaction, Integer value){
        if(valueIndex.containsKey(value)) {
            Integer count = valueIndex.get(value);
            count++;
            valueIndex.put(value,count);
        }
        else {
            valueIndex.put(value,0);
        }
    }
}
