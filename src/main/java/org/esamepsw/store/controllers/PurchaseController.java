package org.esamepsw.store.controllers;


import org.esamepsw.store.entities.ProductInPurchase;
import org.esamepsw.store.entities.Purchase;
import org.esamepsw.store.entities.User;
import org.esamepsw.store.services.PurchaseService;
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
    private PurchaseService purchaseService;

    @PostMapping("/add")
    public ResponseEntity addPurchase(@RequestBody Purchase incomingPurchase) {
        try {
            Purchase added = purchaseService.addPurchase(incomingPurchase);
            return new ResponseEntity<>(added, HttpStatus.CREATED);
        }catch (RuntimeException e){
            return new ResponseEntity<>("Quantity unavailable or user not found", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{user}")
    public ResponseEntity<List<Purchase>> getPurchases(@PathVariable User user) {

        try{
            List<Purchase> result = purchaseService.getPurchasesByUser(user);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/cart")
    public ResponseEntity<List<ProductInPurchase>> getProductInPurchaseByUser(@RequestParam User user) {
        try{
            List<ProductInPurchase> result = purchaseService.getProductInPurchaseByUser(user);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
