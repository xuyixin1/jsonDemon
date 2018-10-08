package com.example.springbootdemo.jsonDemon;

public class Test {
    @JsonField(name = "String")
    private String a;
    @JsonField(name = "int")
    private int b;
    @JsonField(name = "Integer")
    private Integer c;

    public Test(String a, int b,Integer c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Test (){

    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }
}
