package org.esamepsw.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchaseRepository, Long> {

}
