package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class MessageStorageService {
    private static final int MAX_MESSAGES = 50;
    private final List<ChatMessage> messages;
    private final ReentrantReadWriteLock lock;

    public MessageStorageService() {
        this.messages = new ArrayList<>(MAX_MESSAGES);
        this.lock = new ReentrantReadWriteLock();
    }

    public void addMessage(ChatMessage message) {
        lock.writeLock().lock();
        try {
            if (messages.size() >= MAX_MESSAGES) {
                messages.remove(0);
            }
            messages.add(message);
            log.debug("Added message to storage. Current size: {}", messages.size());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<ChatMessage> getLastMessages() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(messages));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<ChatMessage> getLastMessages(int count) {
        if (count <= 0 || count > MAX_MESSAGES) {
            throw new IllegalArgumentException("Count must be between 1 and " + MAX_MESSAGES);
        }

        lock.readLock().lock();
        try {
            int startIndex = Math.max(0, messages.size() - count);
            return Collections.unmodifiableList(new ArrayList<>(messages.subList(startIndex, messages.size())));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clearMessages() {
        lock.writeLock().lock();
        try {
            messages.clear();
            log.info("Message storage cleared");
        } finally {
            lock.writeLock().unlock();
        }
    }
}