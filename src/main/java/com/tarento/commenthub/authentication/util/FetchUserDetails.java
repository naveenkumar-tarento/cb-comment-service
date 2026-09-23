package com.tarento.commenthub.authentication.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.commenthub.cache.CacheService;
import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.transactional.cassandrautils.CassandraOperation;
import com.tarento.commenthub.utility.UserProfileMapper;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

@Slf4j
@Component
public class FetchUserDetails {

    private final JedisPool jedisPool;

  private final CassandraOperation cassandraOperation;

  ObjectMapper objectMapper = new ObjectMapper();

  public FetchUserDetails(JedisPool jedisPool, CassandraOperation cassandraOperation) {
    this.jedisPool = jedisPool;
    this.cassandraOperation = cassandraOperation;
  }

    public List<Object> fetchDataForKeys(List<String> keys) {
        log.info("FetchUserDetails::fetchDataForKeys::inside method");
        List<Object> result = new ArrayList<>();
        try (var jedis = jedisPool.getResource()) {
            String[] keysArray = keys.toArray(new String[0]);
            List<String> values = jedis.mget(keysArray);
            for (String stringifiedJson : values) {
                if (stringifiedJson != null) {
                    addDeserializedValue(result, stringifiedJson);
                }
            }
        }
        return result;
    }

    private void addDeserializedValue(List<Object> result, String stringifiedJson) {
        try {
            result.add(objectMapper.readValue(stringifiedJson, Object.class));
        } catch (Exception e) {
            log.error("Error while fetching user details from Redis: {}", e.getMessage(), e);
        }
    }

  public List<Object> fetchUserFromprimary(List<String> userIds) {
    log.info("FetchUserDetails::fetchUserFromprimary::fetching userDetails from primaryDb");
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put(Constants.ID, userIds);
    List<Map<String, Object>> userInfoList = cassandraOperation.getRecordsByPropertiesWithoutFiltering(
        Constants.KEYSPACE_SUNBIRD, Constants.TABLE_USER, propertyMap,
        Arrays.asList(Constants.PROFILE_DETAILS, Constants.FIRST_NAME, Constants.ID), null);

    return userInfoList.stream()
        .map(this::toUserMap)
        .collect(Collectors.toList());
  }

  private Map<String, Object> toUserMap(Map<String, Object> userInfo) {
    return UserProfileMapper.toUserMap(userInfo, objectMapper, e -> {
      throw new UncheckedIOException(e);
    });
  }
}
