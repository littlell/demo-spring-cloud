package com.demo.spring.cloud.session;

import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * https://account.glodon.com/serviceLogin?service_key=9j7p95yBTBnTKRHJpeeu8913K2nlQuRD&callback_url=http%3A%2F%2Ffz.glodon.com%2Fapi%2Fj_spring_cas_security_check
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityCasConfig extends WebSecurityConfigurerAdapter {

    @Value("${cas.host}/cas/serviceLogout?service=9j7p95yBTBnTKRHJpeeu8913K2nlQuRD&return_to=${service.host}")
    private String casLogoutUrl;
    @Value("${cas.host}/cas")
    private String casHost;
    @Value("/${cas.filter.processing.path}")
    private String casFilterProcessingUrl;
    private String serviceKey = "9j7p95yBTBnTKRHJpeeu8913K2nlQuRD";
    private String serviceSecret = "tHnb8zYi6P1e5jtFdE4D8EXEbL97tD0U";

    @Autowired
    private REST401AuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private CustomUrlSuccessHandler customSuccessHandler;
    @Autowired
    private PaasCustomUserDetailsService customUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(casAuthenticationFilter());
        http.addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class);

        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
        http.authorizeRequests()
                .antMatchers("/cas/login").permitAll()
                .antMatchers("/cas/return").permitAll()
                .antMatchers("/custom").permitAll()
                .anyRequest().fullyAuthenticated();

        http.csrf().disable();
    }

    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(serviceKey);
        sp.setSendRenew(false);
        return sp;
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(casFilterProcessingUrl);
        casAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() throws Exception {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService);
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey(UUID.randomUUID().toString());
        return casAuthenticationProvider;
    }

    @Bean
    public TicketValidator cas20ServiceTicketValidator() throws UnsupportedEncodingException {
        return new PaasCas20ServiceTicketValidator(casHost, serviceKey, serviceSecret);
    }

    @Bean
    public LogoutFilter requestCasGlobalLogoutFilter() throws UnsupportedEncodingException {
        LogoutFilter logoutFilter = new LogoutFilter(casLogoutUrl, new SecurityContextLogoutHandler());
        logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));
        return logoutFilter;
    }
}
