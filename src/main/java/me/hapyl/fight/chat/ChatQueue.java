package me.hapyl.fight.chat;

import java.util.LinkedList;

public class ChatQueue {

    private LinkedList<ChatMessage> queue;
    private int pointer;

    public ChatQueue() {
        queue = new LinkedList<>() {
            @Override
            public boolean removeFirstOccurrence(Object o) {
                return size() > 100;
            }
        };

        pointer = 0;
    }

    public synchronized boolean modifyChatMessage(int id, ChatMessage.Status status) {
        for (ChatMessage message : queue) {
            if (message.getId() != id) {
                continue;
            }

            if (message.getStatus() == status) {
                return false;
            }

            message.setStatus(status);
            return true;
        }

        return false;
    }

}
