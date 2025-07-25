package com.berry.project.controller;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.service.qna.CustomerIqBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class CustomerIqBoardController {

    private final CustomerIqBoardService service;

    @GetMapping("/register")
    public void register(){}

    @PostMapping("/register")
    public String register(CustomerIqBoardDTO customeriqboardDTO){
        log.info("DTO");
        log.info("DTO {}",customeriqboardDTO);
        if(customeriqboardDTO.getIsSecret() == null){
            customeriqboardDTO.setIsSecret(false);
        }

        Long bno= service.insert(customeriqboardDTO);
        log.info("bno {}",bno);
        return "index";
    }

    @GetMapping("/list")
    public void list(Model model){
        List<CustomerIqBoardDTO> list= service.getlist();
        log.info("list {}",list);
        model.addAttribute("list",list);
    }

    @GetMapping("/modify")
    public void modify(){
        log.info("modify");
    }


}