package kr.co.lotteon.repository.custom;

import kr.co.lotteon.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepositoryCustom {
    public void selectSellerInfo(String prodSeller);
}
