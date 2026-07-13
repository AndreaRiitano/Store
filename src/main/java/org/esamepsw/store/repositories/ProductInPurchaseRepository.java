package org.esamepsw.store.repositories;

import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.LongLiteral;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Long> {
    List<ProductInPurchase> findProductInPurchaseByUser(User user);
    ProductInPurchase findFirstByUserIdAndProductId(Long userId, Long productId);
    ProductInPurchase findFirstByUserIdAndProductIdAndPurchaseIsNull(Long userId, Long productId);
    List<ProductInPurchase> findProductInPurchaseByUserAndPurchaseIsNull(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductInPurchase pip WHERE pip.user.id = :userId AND pip.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
