package com.github.aico.service.redis;

import com.github.aico.repository.chat.Chat;
import com.github.aico.repository.chat.ChatRepository;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.chat.request.ActiveTeamUser;
import com.github.aico.web.dto.chat.request.Chatting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;
    private final RedisTemplate<String, Chatting> chattingRedisTemplate;
    private final RedisTemplate<String,Long> longRedisTemplate;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final RedisTemplate<String, ActiveTeamUser> activeTeamUserRedisTemplate;
    private final String team = "team:";

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


    public List<Chatting> getAllMessage(){
        String pattern = team+"*";
        Set<String> keys = chattingRedisTemplate.keys(pattern);
        if (keys.isEmpty()){
            return Collections.emptyList();
        }
        List<Chatting> allChatting = new ArrayList<>();
        for (String key : keys){
            List<Chatting> chattings = chattingRedisTemplate.opsForList().range(key,0,-1);
            allChatting.addAll(chattings);
        }
        return allChatting;
    }

    public List<Long> getUserIdByTeamId(Long teamId){
        String key  = "team_user:" + teamId;
        Set<Long> teamUserIds = longRedisTemplate.opsForSet().members(key);
        if (teamUserIds.isEmpty()){
            List<Long> userIds = teamUserRepository.findTeamUserIdsByTeam(teamId);
            longRedisTemplate.opsForSet().add(key,userIds.toArray(new Long[0]));
            longRedisTemplate.expire(key,24*60*60, TimeUnit.SECONDS);
            return userIds;
        }
        return teamUserIds.stream().toList();
    }
    public List<Long> getTeamIdByUserId(Long userId){
        String key = "user_team:" + userId;

        Set<Long> teamIds = longRedisTemplate.opsForSet().members(key);
        log.info("Redis teamIds for user {}: {}", userId, teamIds);
        if (teamIds.isEmpty()){
            List<Long> teamIdsByDb = teamUserRepository.findTeamIdsByUser(userId);

            if (!teamIdsByDb.isEmpty()) {
                longRedisTemplate.opsForSet().add(key, teamIdsByDb.toArray(new Long[0]));
                longRedisTemplate.expire(key, 24 * 60 * 60, TimeUnit.SECONDS);
            } else {
                log.warn("No team IDs found in DB for user {}", userId);
            }
            return teamIdsByDb;
        }
        return teamIds.stream().toList();
    }
    public void invalidateTeamIdsCache(Long userId) {
        String key = "user_team:" + userId;
        longRedisTemplate.delete(key);
        log.info("Invalidated Redis cache for user {}", userId);
    }
    public void invalidateUsersCache(Long teamId) {
        String key = "team_user:" + teamId;
        longRedisTemplate.delete(key);
    }
    public void saveTeamChatting(Long userId) {
        List<Long> teamIds =  getTeamIdByUserId(userId);
        List<Team> teams = teamRepository.findAllById(teamIds);
        teams.forEach(dbTeam -> processTeamChatting(dbTeam));

    }

    private void processTeamChatting(Team dbTeam) {
        Long teamId = dbTeam.getTeamId();
        List<Chatting> chattings = getMessageListFromRedis(teamId);
        if (!chattings.isEmpty()) {
            removeChattingFromRedis(teamId);
            List<Chat> historyChats = chattings.stream()
                    .map(chat -> createChat(chat, dbTeam))
                    .toList();
            chatRepository.saveAllBatch(historyChats);
        }
    }

    private Chat createChat(Chatting chat, Team dbTeam) {
        User findUser = userRepository.findById(chat.getUserId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(dbTeam, findUser)
                .orElseThrow(() -> new NotFoundException("팀에 해당되어 있지 않은 유저가 있습니다."));
        return Chat.of(chat, teamUser);
    }




    public void deleteAllMessage(){
        String pattern = team+"*";
        Set<String> keys = chattingRedisTemplate.keys(pattern);
        chattingRedisTemplate.delete(keys);
    }
    public void addChatting(Chatting chatting) {
        String key = team + chatting.getTeamId().toString();
        chattingRedisTemplate.opsForList().rightPush(key, chatting);
    }
    public void addTeamLastReadAt(ActiveTeamUser activeTeamUser){
        String key = "team_user_inactive:"+activeTeamUser.getTeamId() +"_"+activeTeamUser.getUserId();
        log.info("키 값: " + key);
        ValueOperations<String, ActiveTeamUser> ops = activeTeamUserRedisTemplate.opsForValue();
        ops.set(key, activeTeamUser);
    }
    public List<ActiveTeamUser> getTeamLastReadAt(Long userId){
        String pattern = "team_user_inactive:*_" + userId;
        Set<String> keys = activeTeamUserRedisTemplate.keys(pattern);
        ValueOperations<String, ActiveTeamUser> ops = activeTeamUserRedisTemplate.opsForValue();
        return keys.stream()
                .map(ops::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void removeAllTeamLastReadAtByUserId(Long userId) {
        String pattern = "team_user_inactive:*_" + userId;
        Set<String> keys = activeTeamUserRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            activeTeamUserRedisTemplate.delete(keys);
            log.info("Deleted {} keys for user {}", keys.size(), userId);
        } else {
            log.info("No keys found to delete for user {}", userId);
        }
    }
    public List<ActiveTeamUser> getTeamLastReadAtAll(){
        String pattern = "team_user_inactive:*_*";
        Set<String> keys = activeTeamUserRedisTemplate.keys(pattern);
        ValueOperations<String, ActiveTeamUser> ops = activeTeamUserRedisTemplate.opsForValue();
        return keys.stream()
                .map(ops::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public void removeAllTeamLastReadAt() {
        String pattern = "team_user_inactive:*_*";
        Set<String> keys = activeTeamUserRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            activeTeamUserRedisTemplate.delete(keys);

        }
    }



    public List<Chatting> getMessageListFromRedis(Long teamId) {
        String key = team + teamId.toString();
        List<Chatting> chattings = chattingRedisTemplate.opsForList().range(key, 0, -1);
        if (chattings != null){
            return chattings;
        }else {
            return Collections.emptyList();
        }
    }
    public void removeChattingFromRedis(Long teamId) {
        String key = team + teamId.toString();
        chattingRedisTemplate.delete(key);
    }

}
