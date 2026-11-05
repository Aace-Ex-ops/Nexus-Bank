package com.bank.models;

public class User {
    private String accountNo;
    private String name;
    private double balance;

    public User(String accountNo, String name, double balance) {
        this.accountNo = accountNo;
        this.name = name;
        this.balance = balance;
    }

    public String getAccountNo() { return accountNo; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
}