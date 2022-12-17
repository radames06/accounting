package com.jd.accounting;

import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.RoleName;
import com.jd.accounting.model.security.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Arrays;

//@TestConfiguration
//public class SpringSecurityWebAuxTestConfig {
//
////    @Bean
//    @Primary
//    public UserDetailsService userDetailsService() {
//
//        User userUser = new User();
//        User userAdmin = new User();
//        userUser.setUsername("User");
//        userUser.setRoles(new ArrayList<Role>());
//        userUser.getRoles().add(new Role(1, "ROLE_USER"));
//        userAdmin.setUsername("Admin");
//        userAdmin.setRoles(new ArrayList<Role>());
//        userAdmin.getRoles().add(new Role(2, "ROLE_ADMIN"));
//
////        UserActive basicActiveUser = new UserActive(basicUser, Arrays.asList(
////                new SimpleGrantedAuthority("ROLE_USER"),
////                new SimpleGrantedAuthority("PERM_FOO_READ")
////        ));
//
////        User managerUser = new UserImpl("Manager User", "manager@company.com", "password");
////        UserActive managerActiveUser = new UserActive(managerUser, Arrays.asList(
////                new SimpleGrantedAuthority("ROLE_MANAGER"),
////                new SimpleGrantedAuthority("PERM_FOO_READ"),
////                new SimpleGrantedAuthority("PERM_FOO_WRITE"),
////                new SimpleGrantedAuthority("PERM_FOO_MANAGE")
////        ));
//
//        return new InMemoryUserDetailsManager(Arrays.asList(
//                userUser, userAdmin
//        ));
//    }
//}
