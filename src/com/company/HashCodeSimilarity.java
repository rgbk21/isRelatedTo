package com.company;

import java.util.ArrayList;
import java.util.List;

public class HashCodeSimilarity {

    private ArrayList<String> s1_Array;//MultiSet S in the problem
    private ArrayList<String> s2_Array;//MultiSet T in the problem
    private ArrayList<Integer> ArrayU = new ArrayList<>(); //Unique elements from set S and T
    private List<Tuple>[] hashTable_s1; //Hash table containing elements from s1_Array
    private List<Tuple>[] hashTable_s2; //Hash table containing elements from s2_Array
    //    private int sLength2;
    private HashTable HSS_Table_1 = new HashTable(1024);
    private HashTable HSS_Table_2 = new HashTable(1024);


    public HashCodeSimilarity(String s1, String s2, int sLength) {

        s1_Array = splitStringIntoArray(s1, sLength);
        s2_Array = splitStringIntoArray(s2, sLength);
//        sLength2 = sLength;

        //Rollover hashing method

        hashTable_s1 = RolloverHashing(sLength, s1, s1_Array, HSS_Table_1);
        hashTable_s2 = RolloverHashing(sLength, s2, s2_Array, HSS_Table_2);

        createArrayU();
//        printoutArrayU();
    }

    public float lengthOfS1() {

        int count = 0;
        int squaredCount = 0;
        double sum = 0;
        int rolloverValue = 0;
        float vectorLength = 0;
        ArrayList<Tuple> t = new ArrayList<>();
//        System.out.println("*********** CALCULATING LENGTH OF S1 ****************");
        for (int i = 0; i < ArrayU.size(); i++) {
//            System.out.println("Looking at: " + ArrayU.get(i));
            rolloverValue = ArrayU.get(i);
            t = HSS_Table_1.search(rolloverValue);
//            count = t.size();
//            System.out.println("count = " + count);
//            squaredCount = count * count;
//            sum += squaredCount;
//            System.out.println("Sum = " + sum);
            // IS THIS CORRECT????
            // TO COMMENT OUT THIS CODE????
            for (int j = 0; j < t.size(); j++) {
                if (t.get(j).getKey() == (ArrayU.get(i))) {
//                    System.out.println("Match found");
                    count = t.get(j).getCount();
//                    System.out.println("count = " + count);
                    squaredCount = count * count;
                    sum += squaredCount;
//                    System.out.println("Sum = " + sum);

                }

            }

        }

//        System.out.println("Final sum is: " + sum);
        vectorLength = (float) Math.sqrt(sum);
//        System.out.println("Vector Length of S1 is: " + vectorLength);
        return vectorLength;

    }

    public float lengthOfS2() {

        int count = 0;
        int squaredCount = 0;
        double sum = 0;
        int rolloverValue = 0;
        float vectorLength = 0;
        ArrayList<Tuple> t = new ArrayList<>();
//        System.out.println("*********** CALCULATING LENGTH OF S2 ****************");
        for (int i = 0; i < ArrayU.size(); i++) {
//            System.out.println("Looking at: " + ArrayU.get(i));
            rolloverValue = ArrayU.get(i);
            t = HSS_Table_2.search(rolloverValue);
//            count = t.size();
//            System.out.println("count = " + count);
//            squaredCount = count * count;
//            sum += squaredCount;
//            System.out.println("Sum = " + sum);

            for (int j = 0; j < t.size(); j++) {
                if (t.get(j).getKey() == (ArrayU.get(i))) {
//                    System.out.println("Match found");
                    count = t.get(j).getCount();
//                    System.out.println("count = " + count);
                    squaredCount = count * count;
                    sum += squaredCount;
//                    System.out.println("Sum = " + sum);

                }

            }

        }

//        System.out.println("Final sum is: " + sum);
        vectorLength = (float) Math.sqrt(sum);
//        System.out.println("Vector Length of S2 is: " + vectorLength);
        return vectorLength;

    }

    public float similarity() {

        int count_1 = 0;
        int count_2 = 0;
        float sum = 0;
        int rolloverValue = 0;
        float similarity = 0;
        ArrayList<Tuple> t_1;
        ArrayList<Tuple> t_2;
//        System.out.println("****************************************************");
//        System.out.println("*********** CALCULATING SIMILARITY ****************");
//        System.out.println("****************************************************");
        for (int i = 0; i < ArrayU.size(); i++) {
//            System.out.println("Looking at: " + ArrayU.get(i));
            rolloverValue = ArrayU.get(i);
            t_1 = HSS_Table_1.search(rolloverValue);
            t_2 = HSS_Table_2.search(rolloverValue);

            count_1 = 0;
            count_2 = 0;


            for (int j = 0; j < t_1.size(); j++) {

//                System.out.println("Checking in Table 1");
                if (t_1.get(j).getKey() == (ArrayU.get(i))) {
//                    System.out.println("Looking at: " + ArrayU.get(i));
//                    System.out.println("Match found in table 1 " + t_1.get(j).getKey() + " " + t_1.get(j).getValue());
                    count_1 = t_1.get(j).getCount();
//                    System.out.println("count = " + count_1);
                }
            }

            for (int j = 0; j < t_2.size(); j++) {
//                System.out.println("Checking in Table 2");
                if (t_2.get(j).getKey() == (ArrayU.get(i))) {
//                    System.out.println("Looking at: " + ArrayU.get(i));
//                    System.out.println("Match found in table 2 " + t_2.get(j).getKey() + " " + t_2.get(j).getValue());
                    count_2 = t_2.get(j).getCount();
//                    System.out.println("count = " + count_2);
                }
            }

//            System.out.println("Earlier Value of sum: " + sum);
            sum = sum + (count_1 * count_2);
//            System.out.println("count_1 * count_2 = " + count_1 + " * " + count_2 + " = " + count_1 * count_2);
//            System.out.println("After Value of sum: " + sum);

        }

//        System.out.println("Final value of similarity sum is: " + sum);

        similarity = sum / (lengthOfS1() * lengthOfS2());

        //System.out.println("Value of similarity is: " + similarity);
        return similarity;
    }


    private List<Tuple>[] RolloverHashing(int slength, String s, ArrayList<String> myArray, HashTable HSS_Table) {

        //What is a good initial size for a hash table?
        //Hash table will store the elements of array S: s1_array
        //Setting it to 4 right now without any reason

        String[] array1 = new String[s.length()];
//        System.out.println("slength value is: " + slength);
        int[] exponent = new int[slength];
        int alpha = 31;
        int asciiValue;

        //Calculating exponent array
        exponent[exponent.length - 1] = 1;
        for (int i = exponent.length - 1; i > 0; i--) {
            exponent[i - 1] = exponent[i] * alpha;
        }
//        for (int i=0; i < exponent.length; i++)
//        {
//            System.out.println(i + " th element is " + exponent[i]);
//        }

        //Doing all the work here. Split s1 into individual characters to
        //calculate the hashValue later
//        System.out.println("********** SPLITTING each element of myArray (s1Array and s2Array)*********");
        array1 = s.split("");
//        for (int i =0; i < array1.length; i++)
//        {
//            System.out.println(i + "th element in array1 is " + array1[i]);
//        }
        //Compute h1 -- the first hash value

        int h1 = 0;

//        System.out.println("********** Computing first Hash Value *********");

        for (int i = 0; i < slength; i++) {
            //If your string contains exactly one character the simplest way to convert
            // it to a character is probably to call the charAt method:
            char c = array1[i].charAt(0);
//            System.out.println("character c is : " + c);
            asciiValue = (int) c;
//            System.out.println("ascii value of c is " + asciiValue);
//            System.out.println("exponent[i] = " + exponent[i]);
//            System.out.println("Pre Value of h1 is: " + h1);
//            System.out.println("asciiValue * exponent[i] = " + asciiValue * exponent[i]);
            h1 = h1 + (asciiValue * exponent[i]);
//            System.out.println("Post Value of h1 is " + h1);
        }


        //Create a new tuple for h1
        Tuple newTuple = new Tuple(h1, myArray.get(0));
        HSS_Table.add(newTuple);

        //Calculate the remaining hash values
        char subtract;
        int asciiValue_subtract;
        int finally_subtract;
        char add;
        int asciiValue_add;
        int h_next;
        for (int i = 1; i < array1.length - slength + 1; i++) {
            subtract = array1[i - 1].charAt(0);
//            System.out.println("subtract char is: " + subtract);
            asciiValue_subtract = (int) subtract;
            finally_subtract = asciiValue_subtract * exponent[0];
//            System.out.println("Ascii value of subtract is " + asciiValue_subtract);
            add = array1[i + slength - 1].charAt(0);
//            System.out.println("add char is: " + add);
            asciiValue_add = (int) add;
//            System.out.println("Ascii value of add is " + asciiValue_add);
            h_next = (h1 - finally_subtract) * alpha + asciiValue_add;
//            System.out.println("value of i " + i);

            //And create new tuples for them as well

            Tuple newTuple_T = new Tuple(h_next, myArray.get(i));
            HSS_Table.add(newTuple_T);
            h1 = h_next;
        }

//        System.out.println("****** PRINTING HASH TABLE *****************");
//        HSS_Table.printHashTable();

        List<Tuple>[] hashTable = HSS_Table.returnHashTable();
        return hashTable;
    }

    public void createArrayU() {

        //Adding elements from hashTable_s1 into ArrayU
        //ELements are copied from the hash table created
        //Elements contained in hash table are already in a way unique

        for (int i = 0; i < hashTable_s1.length; i++) {
            if (!hashTable_s1[i].isEmpty()) {
                for (int j = 0; j < hashTable_s1[i].size(); j++) {
                    ArrayU.add(hashTable_s1[i].get(j).getKey());
                }
            }
        }

        //Adding elements from s2_Array into ArrayU
        //We dont actually use s2_array for this.
        //Instead, use hashTable_s2.
        //1) Double for loop over hashTable_s2,
        // take rollover values from each of the entries in the hashTable_s2
        //2) hash the rollover values using HSS_Table_1 hash function
        //3) Iterate over the returned tuple array to check for rollover
        // equal to hashTable_s2 tuple's rollover
        //4) Add to the ArrayU if key was not found

//        System.out.println("**********  CREATING ARRAY U ***********");

        int rolloverValue;
        boolean flag = true;
        ArrayList<Tuple> t = new ArrayList<>();

        for (int i = 0; i < hashTable_s2.length; i++) {
            for (int j = 0; j < hashTable_s2[i].size(); j++) {
                flag = true;
                rolloverValue = hashTable_s2[i].get(j).getKey();
//                System.out.println("****");
//                System.out.println("In hashTable_S2, key is: " + rolloverValue);
                t = HSS_Table_1.search(rolloverValue);
                if (!t.isEmpty()) {
//                    System.out.println("returned array list t was not empty");
                    for (int k = 0; k < t.size(); k++) {
                        if (t.get(k).getKey() == rolloverValue) {
//                           System.out.println(rolloverValue + "is in Array U is " + ArrayU.contains(rolloverValue));
//                           System.out.println("rollover value " + rolloverValue + " already exists in hashTable_s1");
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
//                        System.out.println(rolloverValue + "is in Array U is " + ArrayU.contains(rolloverValue));
//                        System.out.println("t was not empty but rollover value " + rolloverValue + " was not found");
//                        System.out.println( rolloverValue + " was added to ArrayU");
                        ArrayU.add(rolloverValue);
                    }
                } else {
//                        System.out.println(rolloverValue + "is in Array U is " + ArrayU.contains(rolloverValue));
//                        System.out.println("Returned t was empty.. " + rolloverValue + " added to ArrayU");
                    ArrayU.add(rolloverValue);
                }

            }
        }
    }


    private ArrayList splitStringIntoArray(String str, int slength) {

        ArrayList<String> ret = new ArrayList<String>((str.length() + slength - 1) / slength);//fixed its length

        for (int start = 0; start < str.length() - slength + 1; start++) {
            ret.add(str.substring(start, Math.min(str.length(), start + slength)));
        }
        return ret;
    }

    private void printoutArrayU() {
        System.out.println("******* PRINTING OUT ARRAY U **********");
        for (int i = 0; i < ArrayU.size(); i++) {
            System.out.println(i + "th element is " + ArrayU.get(i));
        }
    }

}

