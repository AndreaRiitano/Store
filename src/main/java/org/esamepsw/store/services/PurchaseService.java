package org.esamepsw.store.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.esamepsw.store.entities.Product;
import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.Purchase;
import org.esamepsw.store.entities.User;
import org.esamepsw.store.repositories.ProductInPurchaseRepository;
import org.esamepsw.store.repositories.ProductRepository;
import org.esamepsw.store.repositories.PurchaseRepository;
import org.esamepsw.store.repositories.UserRepository;
import org.esamepsw.store.utilities.dto.PipRemoveRequest;
import org.esamepsw.store.utilities.dto.PurchaseRequest;
import org.esamepsw.store.utilities.exceptions.product.ProductNotFoundException;
import org.esamepsw.store.utilities.exceptions.purchase.CartIsEmptyException;
import org.esamepsw.store.utilities.exceptions.purchase.QuantityUnavailableException;
import org.esamepsw.store.utilities.dto.PipAddRequest;
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

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUser(PurchaseRequest request) throws UserNotFoundException {
        if ( !userRepository.existsByKeycloakId(request.getKeycloakId())) {
            throw new UserNotFoundException();
        }
        User user = userRepository.findByKeycloakId(request.getKeycloakId());
        return purchaseRepository.findByUser(user);
    }
    @Transactional(readOnly = true)
    public List<ProductInPurchase> getProductInPurchaseByUser( User user) throws UserNotFoundException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        return productInPurchaseRepository.findProductInPurchaseByUserAndPurchaseIsNull(user);
    }

    @Transactional(readOnly = false)
    public Purchase addPurchase(PurchaseRequest purchaseRequest) {

        Long userId = userRepository.findByKeycloakId(purchaseRequest.getKeycloakId()).getId();

        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<ProductInPurchase> cartItems = productInPurchaseRepository.findProductInPurchaseByUserAndPurchaseIsNull(user);
        Purchase newPurchase = new Purchase();
        newPurchase.setUser(user);

        if(cartItems.isEmpty()){
            throw new CartIsEmptyException();
        }

        for (ProductInPurchase incomingPip : cartItems) {

            Product product = entityManager.find(
                    Product.class,
                    incomingPip.getProduct().getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (product == null) {
                throw new ProductNotFoundException();
            }

            if (product.getQuantity() < incomingPip.getQuantity()) {
                throw new QuantityUnavailableException();
            }

            product.setQuantity(product.getQuantity() - incomingPip.getQuantity());

            incomingPip.setPurchase(newPurchase);
        }
        newPurchase.setProductInPurchase(cartItems);

        entityManager.persist(newPurchase);
        return newPurchase;
    }


    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional(readOnly = false)
    public ProductInPurchase addProductInPurchase(PipAddRequest incomingPurchase) {
        User user = userRepository.findByKeycloakId(incomingPurchase.getKeycloakId());
        ProductInPurchase existingPip = productInPurchaseRepository.findFirstByUserIdAndProductIdAndPurchaseIsNull(user.getId(), incomingPurchase.getProduct().getId());
        ProductInPurchase toAdd = new ProductInPurchase();
        toAdd.setUser(user);
        toAdd.setProduct(incomingPurchase.getProduct());
        toAdd.setQuantity(incomingPurchase.getQuantity());
        if(!userRepository.existsByKeycloakId(incomingPurchase.getKeycloakId())) {
            throw new UserNotFoundException();
        }
        if(!productRepository.existsProductById(toAdd.getProduct().getId())) {
            throw new ProductNotFoundException();
        }
        if(existingPip != null){
            existingPip.setQuantity(existingPip.getQuantity() + 1);
            return productInPurchaseRepository.save(existingPip);
        }else{

            return productInPurchaseRepository.save(toAdd);
        }


    }
    @Transactional(readOnly = false)
    public ProductInPurchase removeProductInPurchase(PipRemoveRequest incomingRemoveRequest) {

        User user = userRepository.findByKeycloakId(incomingRemoveRequest.getKeycloakId());
        if(!userRepository.existsByKeycloakId(incomingRemoveRequest.getKeycloakId())) {
            throw new UserNotFoundException();
        }
        if(!productRepository.existsProductById(incomingRemoveRequest.getProduct().getId())) {
            throw new ProductNotFoundException();
        }
        ProductInPurchase existingPip = productInPurchaseRepository.findFirstByUserIdAndProductIdAndPurchaseIsNull(user.getId(), incomingRemoveRequest.getProduct().getId());
        if(existingPip != null){
            if(existingPip.getQuantity() > 1) {
                existingPip.setQuantity(existingPip.getQuantity() - 1);
                productInPurchaseRepository.save(existingPip);

            }else {
                productInPurchaseRepository.delete(existingPip);

            }
        }
        return existingPip;
    }

}
