package com.github.aico.repository.chat;

import java.util.List;

public interface CustomChatRepository {
    void saveAllBatch(List<Chat> chats);
}
