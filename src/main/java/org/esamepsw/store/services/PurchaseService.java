package org.esamepsw.store.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.esamepsw.store.entities.Product;
import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.Purchase;
import org.esamepsw.store.entities.User;
import org.esamepsw.store.repositories.ProductInPurchaseRepository;
import org.esamepsw.store.repositories.PurchaseRepository;
import org.esamepsw.store.repositories.UserRepository;
import org.esamepsw.store.utilities.exceptions.purchase.QuantityUnavailableException;
import org.esamepsw.store.utilities.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUser(User user) throws UserNotFoundException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        return purchaseRepository.findByBuyer(user);
    }


    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }
}
