package kr.co.lotteon.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lotteon.dto.PageRequestDTO;
import kr.co.lotteon.dto.PageResponseDTO;
import kr.co.lotteon.dto.PdReviewDTO;
import kr.co.lotteon.entity.*;
import kr.co.lotteon.repository.custom.PdReviewRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PdReviewRepositoryImpl implements PdReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPdReview qPdReview = QPdReview.pdReview;
    private final QPdReviewImg qPdReviewImg = QPdReviewImg.pdReviewImg;
    private final QProduct qProduct = QProduct.product;
    private final ModelMapper modelMapper;

    //my/Review 조화
    public PageResponseDTO selectReviews(String UserId, Pageable pageable, PageRequestDTO pageRequestDTO) {
        log.info("IMPL 시작");

        log.info("UserId : " + UserId);

        // SELECT * FROM `pdreview` AS a JOIN `pdreview` AS b ON a.revNo = b.revNo WHERE a.userid = '?';
        QueryResults<Tuple> selectPdReviews = jpaQueryFactory
                .select(qPdReview, qPdReviewImg)
                .from(qPdReview)
                .join(qPdReviewImg)
                .on(qPdReview.revNo.eq(qPdReviewImg.revNo))
                .where(qPdReview.userId.eq(UserId))
                .orderBy(qPdReview.revAddDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        log.info("selectPdReviews : " + selectPdReviews);

        List<Tuple> pdReviewResult = selectPdReviews.getResults();
        int total = (int) selectPdReviews.getTotal();
        Page<Tuple> pdReviewPage = new PageImpl<>(pdReviewResult, pageable, total);

        log.info("pdReviewPage" + pdReviewPage);


        List<PdReviewDTO> pdReviewDTOList = pdReviewPage.getContent().stream()
                .map(tuple -> {
                    PdReview pdReview = tuple.get(0, PdReview.class);
                    PdReviewImg pdReviewImg = tuple.get(1, PdReviewImg.class);
                    PdReviewDTO pdReviewDTO = modelMapper.map(pdReview, PdReviewDTO.class);
                    pdReviewDTO.setRevThumb(pdReviewImg.getRevThumb());
                    pdReviewDTO.setUserId(UserId);
                    return pdReviewDTO;
                }).toList();

        log.info("pdReviewDTOList : " + pdReviewDTOList);

        // 각 리뷰의 상품 이름을 조회하여 DTO에 추가
        List<PdReviewDTO> resultPdReview = new ArrayList<>();
        for (PdReviewDTO eachPdReview : pdReviewDTOList) {
            log.info("resultPdReview : " + resultPdReview);
            log.info("eachPdReview  : " + eachPdReview);
            String reviewProdName = jpaQueryFactory
                    .select(qProduct.prodName)
                    .from(qProduct)
                    .where(qProduct.prodNo.eq(eachPdReview.getProdNo()))
                    .fetchOne();
            eachPdReview.setProdName(reviewProdName);
            log.info("resultPdReview : " + resultPdReview);
            log.info("eachPdReview  : " + eachPdReview);

            resultPdReview.add(eachPdReview);
        }

        // 결과 DTO 반환
        return PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(resultPdReview)
                .total(total)
                .build();
    }
}
