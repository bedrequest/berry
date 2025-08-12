package com.berry.project.controller;

import com.berry.project.dto.user.UserDTO;
import com.berry.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerAdvice {

  private final UserService userService;

  @ModelAttribute
  public void getUserId(Principal principal, Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated() &&
        !(authentication instanceof AnonymousAuthenticationToken)) {

      String username = authentication.getName(); // web: email, oauth2: uid
      UserDTO userDTO = userService.getUserInfo(username);
      log.info("Controller Advice userId > {}", userDTO.getUserId());
      model.addAttribute("userId", userDTO.getUserId());
    }
  }

}
