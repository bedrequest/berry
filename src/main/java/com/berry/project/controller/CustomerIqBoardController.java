package com.berry.project.controller;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.dto.qna.CustomerIqFileDTO;
import com.berry.project.handler.CustomerIqFileHandler;
import com.berry.project.handler.CustomerIqPagingHandler;
import com.berry.project.service.qna.CustomerIqBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class CustomerIqBoardController {

    private final CustomerIqBoardService boardservice;
    private final CustomerIqFileHandler fileHandler;

    @GetMapping("/register")
    public void register() {
    }

    @PostMapping("/register")
    public String register(CustomerIqBoardDTO customeriqboardDTO,
                           @RequestParam(name = "files", required = false)
                           MultipartFile[] files) {
        log.info("DTO {}", customeriqboardDTO);
        if (customeriqboardDTO.getIsSecret() == null) {
            customeriqboardDTO.setIsSecret(false);
        }
        // file
        List<CustomerIqFileDTO> fileList = null;
        if (files != null && files[0].getSize() > 0) {
            // 파일 핸들러 작업
            fileList = fileHandler.uploadFiles(files);
        }
        Long bno = boardservice.insert(new CustomerIqBoardFileDTO(customeriqboardDTO, fileList));
        log.info("bno {}", bno);
        return "redirect:/qna/list";
    }

    @GetMapping("/list")
    public void list(Model model,
                     @RequestParam(name = "pageNo", defaultValue = "1", required = false) int pageNo,
                     @RequestParam(name = "type", required = false) String type,
                     @RequestParam(name = "keyword", required = false) String keyword,
                     @RequestParam(name = "tripStart", required = false) String tripStart,
                     @RequestParam(name = "tripEnd", required = false) String tripEnd

    ) {

        log.info("pageNo {}", pageNo);
        log.info("type {}", type);
        log.info("keyword {}", keyword);
        log.info("tripStart {}", tripStart);
        log.info("tripEnd {}", tripEnd);

        if(type != null && type.equals("--선택--")){
            type = null;
        }


        int page = pageNo - 1;
        Page<CustomerIqBoardDTO> list = boardservice.getList(page, type, keyword, tripStart, tripEnd);
        log.info("list {}", list);
//        model.addAttribute("list", list);

        CustomerIqPagingHandler<CustomerIqBoardDTO> paginghandler = new CustomerIqPagingHandler(list, pageNo, type, keyword,tripStart, tripEnd);
        model.addAttribute("ph", paginghandler);
    }

    @GetMapping("/modify")
    public void modify() {
        log.info("modify");
    }
}