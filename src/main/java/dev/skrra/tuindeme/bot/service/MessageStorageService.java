package dev.skrra.tuindeme.bot.service;

import dev.skrra.tuindeme.bot.model.ChatMessage;
import dev.skrra.tuindeme.bot.config.prop.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStorageService {
    private final ApplicationProperties applicationProperties;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void addMessage(ChatMessage message) {
        lock.writeLock().lock();
        try {
            messages.add(message);
            if (messages.size() > applicationProperties.getStorage().getMaxMessages()) {
                messages.removeFirst();
            }
            log.debug("Added message to storage. Current size: {}", messages.size());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<ChatMessage> getLastMessages() {
        lock.readLock().lock();
        try {
            return List.copyOf(messages);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<ChatMessage> getLastMessages(int count) {
        if (count <= 0 || count > applicationProperties.getStorage().getMaxMessages()) {
            throw new IllegalArgumentException(
                    "Count must be between 1 and " + applicationProperties.getStorage().getMaxMessages());
        }

        lock.readLock().lock();
        try {
            int fromIndex = Math.max(0, messages.size() - count);
            return List.copyOf(messages.subList(fromIndex, messages.size()));
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