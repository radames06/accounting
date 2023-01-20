package com.jd.accounting;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Order(1)
//@Configuration
//public class AllowAnonymousWebAccess extends WebSecurityConfigurerAdapter {
//    @Override
//    public void configure(HttpSecurity web) throws Exception {
//        web.antMatcher("**/*").anonymous();
//    }
//}
