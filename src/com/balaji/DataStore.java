package com.balaji;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by baathreya on 7/27/15.
 */
public class DataStore {

    private TreeMap store = new TreeMap();
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private Transaction currentTransaction;


    protected String get(String variable){
        if(currentTransaction != null && currentTransaction.getTempStore().containsKey(variable)){
            return (String) currentTransaction.getTempStore().get(variable);
        }
        else if(store.containsKey(variable)){
            return (String) store.get(variable);
        }
        return null;
    }

    protected void set(String variable, String value){
        if(currentTransaction != null){
            currentTransaction.getTempStore().put(variable,value);
        }
        else {
            store.put(variable, value);
        }
    }

    protected void unset(String variable){
        if(currentTransaction != null){
            currentTransaction.getTempStore().put(variable,null);
        }
        else {
            store.put(variable, null);
        }
    }

    protected int numEqualTo(String value){
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
                if(store.get(str) == value && !currentTransaction.getTempStore().containsKey(str)) {
                    count++;
                }
            }
        }
        return count;
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
}
