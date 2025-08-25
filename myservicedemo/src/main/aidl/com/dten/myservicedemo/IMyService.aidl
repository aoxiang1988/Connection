// IMyService.aidl
package com.dten.myservicedemo;

// Declare any non-default types here with import statements

interface IMyService {
    String getName();
    void setName(in String name);//Service to client
}