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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

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
        Map<String, Object> list = boardservice.getList(page, type, keyword, tripStart, tripEnd);
        log.info("list {}", list);
        log.info("list {}", list.get("noticeList"));
        log.info("list {}", list.get("list"));

        model.addAttribute("noticeList", list.get("noticeList"));

        Page<CustomerIqBoardDTO> customerIqBoardDTOPageList = (Page<CustomerIqBoardDTO>)list.get("list");

        CustomerIqPagingHandler<CustomerIqBoardDTO> paginghandler = new CustomerIqPagingHandler(customerIqBoardDTOPageList, pageNo, type, keyword,tripStart, tripEnd);
        model.addAttribute("ph", paginghandler);
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("bno") Long bno){
        CustomerIqBoardFileDTO customeriqboardfileDTO = boardservice.getDetail(bno);
        log.info(">>>> customeriqboardfileDTO > {} ", customeriqboardfileDTO);
        model.addAttribute("customeriqboardfileDTO", customeriqboardfileDTO);
    }
    @PostMapping("/update")
    public String modify(CustomerIqBoardDTO customeriqboardDTO,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(name = "files", required = false)
                       MultipartFile[] files) {
        log.info(">>>> customeriqboardDTO >> {}", customeriqboardDTO);
        List<CustomerIqFileDTO> fileList = null;
        if(files !=null && files[0].getSize() > 0){
            fileList = fileHandler.uploadFiles(files);
        }
        Long bno = boardservice.modify(new CustomerIqBoardFileDTO(customeriqboardDTO, fileList));
        redirectAttributes.addAttribute("bno", customeriqboardDTO.getBno());

        return "redirect:/qna/detail";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("bno") Long bno){
        boardservice.remove(bno);
        return "redirect:/qna/list";
    }

    @DeleteMapping("/customeriqfile/{uuid}")
    @ResponseBody
    public String fileRemove(@PathVariable("uuid") String uuid){
        Long bno = boardservice.fileRemove(uuid);
        return bno > 0 ? "1":"0";
    }

}