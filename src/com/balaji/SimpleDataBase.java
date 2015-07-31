package com.balaji;


import java.util.Scanner;

public class SimpleDataBase {

    public static void main(String[] args) {
        DataStore dataStore = new DataStore();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] tokens = input.split("\\s");
            String command = tokens[0];
            if("get".equalsIgnoreCase(command)){
                String variable = tokens[1];
                System.out.println(dataStore.get(variable));
            }
            else if("set".equalsIgnoreCase(command)){
                String variable = tokens[1];
                String value = tokens[2];
                Integer val = getInt(value);
                if(val != null){
                    dataStore.set(variable, value);
                }
            }
            else if("numequalto".equalsIgnoreCase(command)){
                String value = tokens[1];
                Integer val = getInt(value);
                if(val != null){
                    System.out.println(dataStore.numEqualTo(value));
                }
            }
            else if("unset".equalsIgnoreCase(command)){
                String variable = tokens[1];
                dataStore.unset(variable);
            }
            else if("end".equalsIgnoreCase(command)){
                System.exit(0);
            }
            else if("begin".equalsIgnoreCase(command)){
                dataStore.begin();
            }
            else if("rollback".equalsIgnoreCase(command)) {
                dataStore.rollback();
            }
            else if("commit".equalsIgnoreCase(command)) {
                dataStore.commit();
            }

        }
    }

    private static Integer getInt(String str){
        try{
            return Integer.parseInt(str);
        }
        catch (NumberFormatException ex){
            return null;
        }
    }
}
