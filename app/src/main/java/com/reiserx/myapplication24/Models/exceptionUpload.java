package com.reiserx.myapplication24.Models;

public class exceptionUpload {
    String className, methodName, message;
    int lineNumber;
    long timestamp;

    public exceptionUpload() {

    }

    public exceptionUpload(String className, String methodName, String message, int lineNumber, long timestamp) {
        this.className = className;
        this.methodName = methodName;
        this.message = message;
        this.lineNumber = lineNumber;
        this.timestamp = timestamp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
