package kr.co.lotteon.controller;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.lotteon.dto.*;
import kr.co.lotteon.entity.Cart;
import kr.co.lotteon.entity.CartProduct;
import kr.co.lotteon.entity.ProdOption;
import kr.co.lotteon.entity.Wish;
import kr.co.lotteon.service.ProdCateService;
import kr.co.lotteon.service.ProductService;
import kr.co.lotteon.service.TestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProdCateService prodCateService;
    private final TestService testService;

    // 상품 목록 이동
    @GetMapping("/product/list")
    public String prodList(@RequestParam("cateCode") String cateCode, ProductPageRequestDTO productPageRequestDTO, Model model){
        ProductPageResponseDTO pageResponseDTO;
        productPageRequestDTO.setCateCode(cateCode);
        pageResponseDTO = productService.selectProductsByCate(productPageRequestDTO);
        String setSortType = productPageRequestDTO.getSort();
        model.addAttribute("setSortType", setSortType);
        model.addAttribute(pageResponseDTO);
        String cate01 = "";
        String cate02 = "";
        String cate03 = "";
        // cateCode AA / 101 / A101
        if (cateCode.length() == 2){
            cate01 = cateCode.substring(0,2);
        }else if (cateCode.length() == 5) {
            cate01 = cateCode.substring(0,2);
            cate02 = cateCode.substring(2,5);
        }else if (cateCode.length() > 5) {
            cate01 = cateCode.substring(0,2);
            cate02 = cateCode.substring(2,5);
            cate03 = cateCode.substring(5,9);
        }
        log.info("cate01 : " + cate01);
        log.info("cate02 : " + cate02);
        log.info("cate03 : " + cate03);

        Map<String, String> resultMap = prodCateService.findCateName(cate01, cate02, cate03);
        log.info("resultMap : " + resultMap);
        model.addAttribute("resultMap", resultMap);


        return "/product/list";
    }

    // 상품 상세보기 이동
    @GetMapping("/product/view")
    public String prodView(@RequestParam("prodNo") int prodNo, Model model){
        log.info("prodView....!!!"+prodNo);
        ProductDTO productDTO = productService.selectProduct(prodNo);
        model.addAttribute("product", productDTO);

        // 상품 Hit 추가
        //productDTO.setProdHit(productDTO.getProdHit() + 1);
        //productService.updateHit(productDTO);

        // 상품 옵션 정보 조회
        ResponseOptionDTO responseOptionDTO = productService.selectProductOption(prodNo);
        log.info("responseOptionDTO : " + responseOptionDTO);
        model.addAttribute("OptionDTOs", responseOptionDTO);
        return "/product/view";
    }

    // 상품 찜하기
    @PostMapping("/product/view/insertWish")
    public ResponseEntity<?> insertWish(@RequestBody List<WishDTO> wishDTOs, HttpServletRequest req){
        log.info("insertWish....: "+wishDTOs.toString());
        // 각 WishDTO를 Wish 객체로 변환하여 저장
        List<Wish> wishes = new ArrayList<>();
        for (WishDTO wishDTO : wishDTOs) {
            Wish wish = new Wish();
            wish.setUserId(wishDTO.getUserId());
            wish.setProdNo(wishDTO.getProdNo());
            wish.setOptNo(wishDTO.getOptNo());
            wish.setWishRdate(wishDTO.getWishRdate());
            wish.setWishCount(wishDTO.getWishCount());
            wishes.add(wish);
        }

        // 변환된 Wish 객체들을 저장
        List<Wish> savedWishes = productService.insertWish(wishes);

        if (savedWishes.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }else {
            return ResponseEntity.ok(savedWishes);
        }
    }

    @PostMapping("/product/view/insertCart")
    public ResponseEntity<?> insertCart(@RequestBody List<CartProductDTO> cartProductDTOs, HttpServletRequest req){
        log.info(cartProductDTOs.toString());
        String userId = req.getParameter("userId");
        log.info("findUserId_forCart...: "+userId);
        //userId 를 사용하여 CartNo 조회
        int cartNo = productService.findCartNoByUserId(userId);
        log.info("findUserId_forCart...1:"+String.valueOf(cartNo));

        List<CartProduct> products = new ArrayList<>();
        for (CartProductDTO cartDTO : cartProductDTOs){
            CartProduct cartProduct = new CartProduct();
            cartProduct.setCartNo(cartNo);
            cartProduct.setProdNo(cartDTO.getProdNo());
            cartProduct.setOptNo(cartDTO.getOptNo());
            cartProduct.setCount(cartDTO.getCount());
            products.add(cartProduct);
        }

        List<CartProduct> savedCardProds = productService.insertCart(products);

        if (savedCardProds.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }else {
            return ResponseEntity.ok(savedCardProds);
        }

    }

    // 상품 검색 이동
    @GetMapping("/product/search")
    public String prodSearch(){
        return "/product/search";
    }

    // 장바구니 이동
    @GetMapping("/product/cart")
    public String prodCart(@RequestParam("userId") String userId, Model model){
        int cartNo = productService.findCartNoByUserId(userId);
        if (cartNo != 0){
            List<CartInfoDTO> cartProducts = productService.findCartProdNo(cartNo);
            model.addAttribute("cartProducts", cartProducts);
        }

        return "/product/cart";
    }

    // 장바구니 이동
    @GetMapping("/product/cartTest")
    public String prodCartTest(@RequestParam("userId") String userId, Model model){
        int cartNo = productService.findCartNoByUserId(userId);
        if (cartNo != 0){
            Map<String, List<CartInfoDTO>> cartProducts = testService.findCartProdNoTest(cartNo);
            model.addAttribute("cartProducts", cartProducts);
        }

        return "/product/cartTest";
    }

    // 상품 주문 이동
    @GetMapping("/product/order")
    public String prodOrder(){
        return "/product/order";
    }

    // 상품 주문 완료 이동
    @GetMapping("/product/complete")
    public String prodComplete(){
        return "/product/complete";
    }

}
