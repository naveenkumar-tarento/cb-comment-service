package com.tarento.commenthub.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.commenthub.constant.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public final class UserProfileMapper {

  private UserProfileMapper() {
  }

  public static Map<String, Object> toUserMap(Map<String, Object> userInfo, ObjectMapper objectMapper,
      Consumer<JsonProcessingException> onProfileParseError) {
    Map<String, Object> userMap = new HashMap<>();

    String userId = (String) userInfo.get(Constants.ID);
    String userName = (String) userInfo.get(Constants.FIRST_NAME);

    userMap.put(Constants.USER_ID_KEY, userId);
    userMap.put(Constants.FIRST_NAME_KEY, userName);

    String profileDetails = (String) userInfo.get(Constants.PROFILE_DETAILS);
    if (StringUtils.isNotBlank(profileDetails)) {
      applyProfileDetails(userMap, profileDetails, objectMapper, onProfileParseError);
    }

    return userMap;
  }

  private static void applyProfileDetails(Map<String, Object> userMap, String profileDetails,
      ObjectMapper objectMapper, Consumer<JsonProcessingException> onProfileParseError) {
    Map<String, Object> profileDetailsMap;
    try {
      profileDetailsMap = objectMapper.readValue(profileDetails,
          new TypeReference<HashMap<String, Object>>() {});
    } catch (JsonProcessingException e) {
      onProfileParseError.accept(e);
      return;
    }

    if (MapUtils.isEmpty(profileDetailsMap)) {
      return;
    }

    if (profileDetailsMap.containsKey(Constants.PROFILE_IMG) && StringUtils.isNotBlank((String) profileDetailsMap.get(Constants.PROFILE_IMG))) {
      userMap.put(Constants.PROFILE_IMG_KEY, profileDetailsMap.get(Constants.PROFILE_IMG));
    }
    if (profileDetailsMap.containsKey(Constants.DESIGNATION_KEY) && StringUtils.isNotEmpty((String) profileDetailsMap.get(Constants.DESIGNATION_KEY))) {
      userMap.put(Constants.DESIGNATION_KEY, profileDetailsMap.get(Constants.PROFILE_IMG));
    }
    if (profileDetailsMap.containsKey(Constants.EMPLOYMENT_DETAILS)) {
      Map<?, ?> employmentDetails = (Map<?, ?>) profileDetailsMap.get(Constants.EMPLOYMENT_DETAILS);
      if (MapUtils.isNotEmpty(employmentDetails) && employmentDetails.containsKey(Constants.DEPARTMENT_KEY)
          && StringUtils.isNotBlank((String) employmentDetails.get(Constants.DEPARTMENT_KEY))) {
        userMap.put(Constants.DEPARTMENT, employmentDetails.get(Constants.DEPARTMENT_KEY));
      }
    }
  }
}
