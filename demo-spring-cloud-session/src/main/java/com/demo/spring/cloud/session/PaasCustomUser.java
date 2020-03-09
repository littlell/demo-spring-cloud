package com.demo.spring.cloud.session;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class PaasCustomUser implements UserDetails {

  private long userId;
  private String password;
  private String userName;
  private String email;
  private String mobile;
  private String nickname;
  private String displayName;
  private List<String> avatarPath;
  private Collection<? extends GrantedAuthority> authorities;

  public PaasCustomUser(long userId, String password, String userName, String email,
                        String mobile, String nickname, String displayName,
                        Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.password = password;
    this.userName = userName;
    this.email = email;
    this.mobile = mobile;
    this.nickname = nickname;
    this.displayName = displayName;
    this.authorities = authorities;
  }

  public PaasCustomUser(long userId, String password, String userName, String email, String mobile, String nickname, String displayName, Collection<? extends GrantedAuthority> authorities, List<String> avatarPath) {
    this(userId, password, userName, email, mobile, nickname, displayName, authorities);
    this.avatarPath = avatarPath;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getMobile() {
    return mobile;
  }

  public String getNickname() {
    return nickname;
  }

  public String getDisplayName() {
    return displayName;
  }

  public List<String> getAvatarPath() {
    return avatarPath;
  }
}
