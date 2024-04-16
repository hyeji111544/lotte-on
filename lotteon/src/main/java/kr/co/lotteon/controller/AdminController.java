package kr.co.lotteon.controller;

import kr.co.lotteon.dto.NoticeDTO;
import kr.co.lotteon.dto.PageRequestDTO;
import kr.co.lotteon.dto.PageResponseDTO;
import kr.co.lotteon.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;

    // 관리자 인덱스페이지 이동
    @GetMapping("/admin/index")
    public String adminIndex(){

        return "/admin/index";
    }

// admin - config //
    // 관리자 설정 배너 이동
    @GetMapping("/admin/config/banner")
    public String configBanner(){

        return "/admin/config/banner";
    }

    // 관리자 설정 정보 이동
    @GetMapping("/admin/config/info")
    public String configInfo(){

        return "/admin/config/info";
    }

// admin - product //
    // 관리자 상품 목록 이동
    @GetMapping("/admin/product/list")
    public String productList(){

        return "/admin/product/list";
    }

    // 관리자 상품 등록 이동
    @GetMapping("/admin/product/register")
    public String productRegister(){

        return "/admin/product/register";
    }

    // 관리자 고객센터 공지사항 목록 이동
    @GetMapping("/admin/cs/notice/list")
    public String csNoticeList(Model model, PageRequestDTO pageRequestDTO){

        PageResponseDTO pageResponseDTO = adminService.selectNoticeAdmin(pageRequestDTO);
        log.info("pageResponseDTO : " + pageResponseDTO);
        model.addAttribute(pageResponseDTO);

        return "/admin/cs/notice/list";
    }

    // 관리자 고객센터 공지사항 등록폼 불러오기
    @GetMapping("/admin/cs/notice/write")
    public String csNoticeWriteForm(){
        return "/admin/cs/notice/write";
    }

    // 관리자 고객센터 공지사항 등록
    @PostMapping("/admin/cs/notice/write")
    public String csNoticeWrite(NoticeDTO noticeDTO){

        adminService.insertNoticeAdmin(noticeDTO);
        return "redirect:/admin/cs/notice/list";
    }
}
