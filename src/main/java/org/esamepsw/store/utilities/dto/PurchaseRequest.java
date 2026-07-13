package org.esamepsw.store.utilities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PurchaseRequest {
    String keycloakId;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
}
