package kr.co.lotteon.repository.custom;

import kr.co.lotteon.dto.PageRequestDTO;
import kr.co.lotteon.entity.Qna;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepositoryCustom {
    public PageImpl<Qna> selectMyQna(String userId, PageRequestDTO pageRequestDTO, Pageable pageable);

}
