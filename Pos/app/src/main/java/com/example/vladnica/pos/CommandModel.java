package com.example.vladnica.pos;

/**
 * Created by Vlad on 10/19/2018.
 */
public class CommandModel {

    private int id;
    private String commandName;
    private int length;
    private byte[] commandBytes;
    private String commandString;

    public CommandModel() {
        
    }

    public CommandModel(int id, String commandName, int length, byte[] commandBytes, String commandString) {
        this.id = id;
        this.commandName = commandName;
        this.length = length;
        this.commandBytes = commandBytes;
        this.commandString = commandString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getCommandBytes() {
        return commandBytes;
    }

    public void setCommandBytes(byte[] commandBytes) {
        this.commandBytes = commandBytes;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }
}
