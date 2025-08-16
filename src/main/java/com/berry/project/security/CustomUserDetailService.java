package com.berry.project.security;

import com.berry.project.dto.user.UserDTO;
import com.berry.project.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class CustomUserDetailService implements UserDetailsService {

  @Autowired
  public UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserDTO userDTO = userService.selectUserEmail(username);
    log.info("username >>>{}", username);
    log.info("UserDetail userDTO >>> {}", userDTO);

    if (userDTO == null) {
      throw new UsernameNotFoundException(username);
    }

    return new AuthMember(userDTO);
  }
}
