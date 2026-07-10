package org.esamepsw.store.controllers;


import org.esamepsw.store.entities.User;
import org.esamepsw.store.services.UserService;
import org.esamepsw.store.utilities.exceptions.user.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            User added = userService.addUser(user);
            return new ResponseEntity<>(added, HttpStatus.CREATED);
        }catch (UserAlreadyExistsException e){
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncUserData(@AuthenticationPrincipal Jwt jwt) {


        String keycloakId = jwt.getClaimAsString("sub");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String email = jwt.getClaimAsString("email");
        String phone = jwt.getClaimAsString("phone");
        String address = jwt.getClaimAsString("address");

        userService.syncUserFromKeycloak(keycloakId, email, firstName, lastName, phone, address);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
