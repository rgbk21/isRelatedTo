package com.company;

public class Tuple {
    private int key;
    private String value;
    private int count;

    public Tuple(int keyP, String valueP) {
        this.key = keyP;
        this.value = valueP;
        this.count = 1;
    }

    public int getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean equals(Tuple t) {
        if (t.key == this.key && (t.value).equals(this.value)) {
            return true;
        } else return false;
    }


}
