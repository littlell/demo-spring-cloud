package com.demo.spring.cloud.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaasCustomUserDetailsService implements
        AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(
            CasAssertionAuthenticationToken casAssertionAuthenticationToken)
            throws UsernameNotFoundException, AccessDeniedException {

        JSONObject userJson = JSON.parseObject(String.valueOf(
                casAssertionAuthenticationToken.getAssertion().getPrincipal().getAttributes()
                        .get("detail")));
        long userId = userJson.getLongValue("id");
        String userName = StringUtils.stripToEmpty(userJson.getString("username"));
        String email = StringUtils.stripToEmpty(userJson.getString("email"));
        String mobile = StringUtils.stripToEmpty(userJson.getString("mobile"));
        String nickname = StringUtils.stripToEmpty(userJson.getString("nickname"));
        String displayName = StringUtils.stripToEmpty(userJson.getString("displayName"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));

        return new PaasCustomUser(userId, "", userName, email, mobile, nickname, displayName,
                grantedAuthorities);
    }
}
