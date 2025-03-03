package com.github.aico.service.redis;

import com.github.aico.web.dto.chat.request.Chatting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;
    private final RedisTemplate<String, Chatting> chattingRedisTemplate;

    public String getData(String key){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }
    public void setDataExpire(String key, String value,long duration){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }
    public void setData(String key,String value){//지정된 키(key)에 값을 저장하는 메서드
        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        valueOperations.set(key,value);
    }
    public void deleteData(String key){//지정된 키(key)에 해당하는 데이터를 Redis에서 삭제하는 메서드
        redisTemplate.delete(key);
    }
    public void addChatting(Chatting chatting) {
        String key = chatting.getTeamId().toString();
        chattingRedisTemplate.opsForList().rightPush(key, chatting);

    }
    public List<Chatting> getMessageListFromRedis(Long teamId) {
        String key = teamId.toString();
        List<Chatting> messages = chattingRedisTemplate.opsForList().range(key, 0, -1);
        if (messages != null){
            return messages;
        }else {
            return Collections.emptyList();
        }
    }
    public void removeChattingFromRedis(Long teamId) {
        String key = teamId.toString();
        chattingRedisTemplate.delete(key);
    }

}
