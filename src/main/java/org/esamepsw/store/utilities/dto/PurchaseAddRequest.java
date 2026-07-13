package org.esamepsw.store.utilities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esamepsw.store.entities.ProductInPurchase;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PurchaseAddRequest {
    List<ProductInPurchase> cart;
    String user;
}
