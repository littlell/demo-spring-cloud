package com.demo.spring.cloud.session;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecurityUtils {

  public static PaasCustomUser currentPaasUser() {
    return (PaasCustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static Long currentPaasUserId() {
    try {
      return currentPaasUser().getUserId();
    } catch (Exception e) {
      return null;
    }
  }

  public static List<String> currentPaasUserRoles() {
    return currentPaasUser().getAuthorities().stream().map((e) -> e.getAuthority())
        .collect(Collectors.toList());
  }

  /**
   * 广联云用户详情信息.
   */
  public static Object currentPassUserDetails() {
    Map<String, Object> details = new HashMap<>();
    details.put("userId", currentPaasUser().getUserId());
    details.put("userName", currentPaasUser().getUsername());
    details.put("email", currentPaasUser().getEmail());
    details.put("mobile", currentPaasUser().getMobile());
    details.put("nickname", currentPaasUser().getNickname());
    details.put("displayName", currentPaasUser().getDisplayName());
    details.put("avatarPath", currentPaasUser().getAvatarPath());
    return details;
  }
}
