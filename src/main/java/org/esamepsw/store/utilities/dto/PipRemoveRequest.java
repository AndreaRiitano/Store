package org.esamepsw.store.utilities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.esamepsw.store.entities.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PipRemoveRequest {
    Product product;
    String keycloakId;
}
