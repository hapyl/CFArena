package me.hapyl.fight.chat;

import org.bukkit.command.CommandSender;

public class ChatMessage {

    private final CommandSender sender;
    private final String message;
    private int id;
    private Status status;

    public ChatMessage(CommandSender sender, String message) {
        this.sender = sender;
        this.message = message;
        this.status = Status.OK;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        OK,
        DELETED,
        RESTORED
    }

}
