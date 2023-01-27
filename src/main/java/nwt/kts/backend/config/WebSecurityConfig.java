package nwt.kts.backend.config;

import nwt.kts.backend.security.auth.RestAuthenticationEntryPoint;
import nwt.kts.backend.security.auth.TokenAuthenticationFilter;
import nwt.kts.backend.service.CustomUserDetailsService;
import nwt.kts.backend.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                .authorizeRequests().antMatchers("/auth/**").permitAll()
                .antMatchers("/drives/accept-drive-consent").permitAll()
                .antMatchers("/drives/reject-drive-consent").permitAll()
                .antMatchers("/drives/get-paid-drive").permitAll()
                .antMatchers("/drives/get-started-drive").permitAll()
                .antMatchers("/drives/get-ended-drive").permitAll()
                .antMatchers("/drivers/").permitAll()
                .antMatchers("/drivers/get-by-id/{id}").permitAll()
                .antMatchers("/drivers/update-coordinates/{id}").permitAll()
                .antMatchers("/drivers/set-coordinates/{id}").permitAll()
                .antMatchers("/drivers/closest-stop/{id}").permitAll()
                .antMatchers("/passengers/register").permitAll()
                .antMatchers("/passengers/activate-account/{id}").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/secured/**", "/secured/**/**", "/secured/**/**/**").permitAll()
                .anyRequest().authenticated().and()
                .cors().and()
                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, customUserDetailsService), BasicAuthenticationFilter.class);
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/auth/**");
        web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico", "/**/*.html",
                "/**/*.css", "/**/*.js");

    }

}
