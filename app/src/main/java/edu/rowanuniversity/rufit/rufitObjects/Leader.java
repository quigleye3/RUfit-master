package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by Erin on 5/16/2017.
 */

public class Leader {

    private String name;
    private int data;

    public Leader(){ }

    public Leader(String name, int data){
        this.name = name;
        this.data = data;
    }

    public String getName() { return name; }

    public int getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(int data) {
        this.data = data;
    }
}
