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
            switch(tokens.length) {
                case 1:
                    if("end".equalsIgnoreCase(command)){
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
                    else {
                        System.out.println("invalid command: " + input);
                    }
                    break;
                case 2:
                    if("get".equalsIgnoreCase(command)){
                        String variable = tokens[1];
                        System.out.println(dataStore.get(variable));
                    }
                    else if("unset".equalsIgnoreCase(command)){
                        String variable = tokens[1];
                        dataStore.set(variable, null);
                    }
                    else if("numequalto".equalsIgnoreCase(command)){
                        String value = tokens[1];
                        Integer val = getInt(value);
                        if(val != null){
                            System.out.println(dataStore.numEqualTo(val));
                        }
                    }
                    else {
                        System.out.println("invalid command: " + input);
                    }
                    break;
                case 3:
                    if("set".equalsIgnoreCase(command)){
                        String variable = tokens[1];
                        String value = tokens[2];
                        Integer val = getInt(value);
                        if(val != null){
                            dataStore.set(variable, val);
                        }
                    }
                    else {
                        System.out.println("invalid command: " + input);
                    }
                    break;
                default:
                    System.out.println("invalid command: " + input);
                    break;
            }
        }
    }

    private static Integer getInt(String str){
        try{
            return Integer.parseInt(str);
        }
        catch (NumberFormatException ex){
            System.out.println("invalid integer: " + str);
            return null;
        }
    }
}
