package com.berry.project.security;

import com.berry.project.dto.user.UserDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class AuthMember extends User {

  @Getter
  private UserDTO userDTO;

  public AuthMember(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public AuthMember(UserDTO userDTO) {
    // 소셜이랑 별개의 작업이니까
    super(userDTO.getUserEmail(), userDTO.getPassword(),
        userDTO.getAuthList().stream()
            .map(auth -> new SimpleGrantedAuthority(auth.getAuthRole()))
            .collect(Collectors.toList())
    );
    this.userDTO = userDTO;

  }
}
