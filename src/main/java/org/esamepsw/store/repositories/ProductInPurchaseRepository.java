package org.esamepsw.store.repositories;

import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Long> {
    List<ProductInPurchase> findProductInPurchaseByUser(User user);
}
