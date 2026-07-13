package org.esamepsw.store.controllers;


import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.Purchase;
import org.esamepsw.store.entities.User;
import org.esamepsw.store.services.PurchaseService;
import org.esamepsw.store.services.UserService;
import org.esamepsw.store.utilities.dto.PipAddRequest;
import org.esamepsw.store.utilities.dto.PipRemoveRequest;
import org.esamepsw.store.utilities.dto.PurchaseRequest;
import org.esamepsw.store.utilities.exceptions.product.ProductNotFoundException;
import org.esamepsw.store.utilities.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PurchaseService purchaseService;


    @GetMapping("/allPurchase")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Purchase>> getAllPurchase() {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return new ResponseEntity<>(purchases,HttpStatus.OK);
    }

    @PostMapping("/addPurchase")
    public ResponseEntity addPurchase(@RequestBody PurchaseRequest incomingPurchase) {
        try {
            Purchase added = purchaseService.addPurchase(incomingPurchase);
            return new ResponseEntity<>(added, HttpStatus.CREATED);
        }catch (RuntimeException e){
            return new ResponseEntity<>("Quantity unavailable or user not found", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addToCart")
    public ResponseEntity addToCart(@RequestBody PipAddRequest incomingProduct) {
        try {
            ProductInPurchase added = purchaseService.addProductInPurchase(incomingProduct);
            return new ResponseEntity<>(added, HttpStatus.CREATED);
        }catch(ProductNotFoundException e){
            return new ResponseEntity<>("Product not found", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/orders")
    public ResponseEntity<List<Purchase>> getPurchases(@RequestBody PurchaseRequest request) {

        try{
            List<Purchase> result = purchaseService.getPurchasesByUser(request);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/cart")
    public ResponseEntity<List<ProductInPurchase>> getProductInPurchaseByUser(@RequestParam String keycloakId) {
        try{
            List<ProductInPurchase> result = purchaseService.getProductInPurchaseByUser(userService.findUserByKeycloakId(keycloakId));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cartRemove")
    public ResponseEntity ProductInPurchase(@RequestBody PipRemoveRequest incomingProduct) {

        try {
            purchaseService.removeProductInPurchase(incomingProduct);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ProductNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
