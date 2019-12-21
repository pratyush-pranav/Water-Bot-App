package com.example.jerry;

public class SendToDatabase {
    String id;
    String time;
    String command;
    public SendToDatabase(String id, String time, String command) {
        this.id=id;
        this.time=time;
        this.command=command;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getCommand() {
        return command;
    }
}
