package com.company;

import java.util.ArrayList;
import java.util.List;

public class HashTable {

    private List<Tuple>[] myHashTable;
    private HashFunction myHashFn;


    public HashTable(int size) {

        int sizeOfHashT;
        this.myHashFn = new HashFunction(size);
        sizeOfHashT = findPrime(size);//findPrime returns the next largest prime from int size
        this.myHashTable = new List[sizeOfHashT];// Value returned in the earlier step is the hash table size

        for (int i = 0; i < myHashTable.length; i++)
        {
            ArrayList<Tuple> l1 = new ArrayList<>();
            myHashTable[i] = l1;
        }
    }

    public List<Tuple>[] returnHashTable() {

        return myHashTable;
    }

    public void add(Tuple t) {

        int hashValue;
        int count = 0;
        {
            hashValue = myHashFn.hash(t.getKey());//calculate which bucket the tuple should go to

            //If tuple already exists, do not add the tuple
            //Just increment the count of the earlier tuple by 1

            for (int i = 0; i < myHashTable[hashValue].size(); i++)
            {
                if (myHashTable[hashValue].get(i).equals(t))
                {
//                    System.out.println("Duplicate element with Value:" + myHashTable[hashValue].get(i).getValue() + " Key: " + myHashTable[hashValue].get(i).getKey());
//                    System.out.println("Earlier count: " + myHashTable[hashValue].get(i).getCount());
                    count = myHashTable[hashValue].get(i).getCount();
                    myHashTable[hashValue].get(i).setCount(count + 1);
//                    System.out.println("After count: " + myHashTable[hashValue].get(i).getCount());
                    return;
                }

            }

            //If tuple does not exist, then add tuple
            myHashTable[hashValue].add(t); //add the tuple to the bucket

//            System.out.println("*******************************");
//            System.out.println("Added element with key: " + t.getKey());
//            System.out.println("Added element with value: " + t.getValue());
//            System.out.println("Load factor is " + loadFactor());
        }
        if (loadFactor() > 0.7) {
//            System.out.println("WARNING :::: Load factor > 0.7");
            reHash(myHashTable);
        }

    }

    public float loadFactor() {
        float loadFactor = 0;
        loadFactor = (float) numElements() / size();
        return loadFactor;
    }

    private void reHash(List<Tuple>[] oldHashT) {

        int hashValue;
        int reHash_size;

        //Creating a new Hash Table of double the previous size

        reHash_size = findPrime(2 * oldHashT.length);
//        System.out.println("Rehash_size " + reHash_size);
        List<Tuple>[] newHashTable = new List[reHash_size];
        for (int i = 0; i < newHashTable.length; i++) {
            ArrayList<Tuple> L1 = new ArrayList<>();
            newHashTable[i] = L1;
        }

//        System.out.println("Size of the newHashTable " + newHashTable.length);

        //Calculate a new HashFunction for the new hash table

        HashFunction myNewHashFn = new HashFunction(2 * oldHashT.length);
        myHashFn = myNewHashFn;

        //Rehashing old data into new table

        for (int i = 0; i < oldHashT.length; i++) {
            for (int j = 0; j < oldHashT[i].size(); j++) {
                //calculate the new hash value of each element in the oldHashT
                hashValue = myHashFn.hash(oldHashT[i].get(j).getKey());

                //add the tuple to the newHashTable
                newHashTable[hashValue].add(oldHashT[i].get(j));
//                System.out.println("Rehashed value: " + oldHashT[i].get(j).getValue());
//                System.out.println("Rehashed key: " + oldHashT[i].get(j).getKey());
            }
        }

        myHashTable = newHashTable;
    }

    public ArrayList<Tuple> search(int k) {
        int k_HashValue;
        ArrayList<Tuple> t = new ArrayList<>();
        k_HashValue = myHashFn.hash(k);
//        System.out.println("Bucket hash value is: " + k_HashValue);


//        for (int i = 0; i < myHashTable[k_HashValue].size(); i++)
//        {
//            t.add(myHashTable[k_HashValue].get(i));
//        }
//        printHashTable();
        //New Code:
        if (! myHashTable[k_HashValue].isEmpty())
        {
            for (int i = 0; i < myHashTable[k_HashValue].size(); i++)
            {
                if(myHashTable[k_HashValue].get(i).getKey() == k)
                {
                    t.add(myHashTable[k_HashValue].get(i));
                }
            }
        }else
        {
//            System.out.println("Empty list...");
            return t;
        }
        //End of new Code
        for (int i = 0 ; i < t.size(); i++) {
//            System.out.println(t.get(i).getKey() + " " + t.get(i).getValue());
        }
        return t;
    }

    public int search(Tuple t) {
        int t_HashValue;
        t_HashValue = myHashFn.hash(t.getKey());
//        System.out.println("Bucket: " + t_HashValue);
//        System.out.println("Searching for tuple with " + " Key: " + t.getKey() + " Value: " + t.getValue());
//        System.out.println("** Printing Hash Table **");
//        printHashTable();
        for (int i = 0; i < myHashTable[t_HashValue].size(); i++)
        {
            if (myHashTable[t_HashValue].get(i).equals(t))
            {
//                System.out.println("Found tuple with Value " + myHashTable[t_HashValue].get(i).getValue() );
//                System.out.println("Found tuple with Key " + myHashTable[t_HashValue].get(i).getKey() );
//                System.out.println("Found Count as: " + myHashTable[t_HashValue].get(i).getCount());
                return myHashTable[t_HashValue].get(i).getCount();

            }
        }
//        System.out.println("Tuple not found...");
        return 0;
    }


    public void remove(Tuple t) {
        int count = search(t);
        int t_HashValue;
        int temp; //stores the count of the tuple

        t_HashValue = myHashFn.hash(t.getKey());
        if (count == 0) {
            System.out.println("Element does not exist");
        }
        if (count == 1) {
            for (int i = 0; i < myHashTable[t_HashValue].size(); i++) {
                if (myHashTable[t_HashValue].get(i).equals(t)) {
                    myHashTable[t_HashValue].remove(i);
                }
            }
        }
        if (count > 1) {
            for (int i = 0; i < myHashTable[t_HashValue].size(); i++) {
                if (myHashTable[t_HashValue].get(i).equals(t)) {
                    temp = myHashTable[t_HashValue].get(i).getCount();
                    myHashTable[t_HashValue].get(i).setCount(temp - 1);
                }
            }
        }

    }

    public int maxLoad() {

        int maxLoad = 0;
        int numOfElmnts = 0;
        int bucketNum = 0;

        for (int i = 0; i < myHashTable.length; i++) {
            numOfElmnts = myHashTable[i].size();
            if (numOfElmnts > maxLoad) {
                maxLoad = numOfElmnts;//what is the max no. of tuples in a ArrayList
                bucketNum = i;//In which bucket do you have the max elements

            }
        }
        return maxLoad;
    }

    public float averageLoad() {
        float totalNumOfElements = 0;//number of distinct elements
        int nonNullCells = 0;
        float averageLoad = 0;

        for (int i = 0; i < myHashTable.length; i++) {
            totalNumOfElements += myHashTable[i].size();
            if (myHashTable[i].size() != 0) {
                nonNullCells++;
            }

        }

//        System.out.println("Total no. of elements is " + totalNumOfElements);
//        System.out.println("No. of non null cells " + nonNullCells);

        averageLoad = (totalNumOfElements) / (nonNullCells);
        return averageLoad;
    }

    public int size() {
        return myHashTable.length;
    }

    public int numElements() {
        int totalNumOfElements = 0;//number of distinct elements

        for (int i = 0; i < myHashTable.length; i++) {
            totalNumOfElements += myHashTable[i].size();

        }
        return totalNumOfElements;
    }

    public void printHashTable() {

        for (int i = 0; i < myHashTable.length; i++) {
            {
                for (int j = 0; j < myHashTable[i].size(); j++) {
                    System.out.println("In bucket number " + i);
                    System.out.println("Key is:" + myHashTable[i].get(j).getKey());
                    System.out.println("Value is:" + myHashTable[i].get(j).getValue());
                    System.out.println("Count is:" + myHashTable[i].get(j).getCount());

                }

            }
        }

    }

    private int findPrime(int n) {
        boolean found = false;
        int num = n;
        while (!found) {
            if (isPrime(num))
                return num;
            num++;
        }
        return -1;

    }

    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++)
            if (n % i == 0)
                return false;
        return true;
    }


}

