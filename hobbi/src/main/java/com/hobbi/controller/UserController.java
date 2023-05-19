package com.hobbi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hobbi.model.dto.AppClientSignUpDto;
import com.hobbi.model.dto.BusinessRegisterDto;
import com.hobbi.model.dto.UpdateAppClientDto;
import com.hobbi.model.dto.UpdateBusinessDto;
import com.hobbi.model.entities.AppClient;
import com.hobbi.model.entities.BusinessOwner;
import com.hobbi.model.entities.UserEntity;
import com.hobbi.model.entities.enums.UserRoleEnum;
import com.hobbi.security.HobbieUserDetailsService;
import com.hobbi.service.UserService;




@RestController
@CrossOrigin(allowedHeaders="*")
public class UserController {
    private final UserService userService;
	private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
	private HobbieUserDetailsService hobbieUserDetailsService;
    

    @Autowired
    public UserController(UserService userService,PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,HobbieUserDetailsService hobbieUserDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.hobbieUserDetailsService = hobbieUserDetailsService;
        this.authenticationManager = authenticationManager;
    }
    
    //for testing adding dummy method
    @GetMapping("/signup")
    public String testing() {
    	return "USER";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AppClientSignUpDto user) {
        System.out.println(user);
        if (this.userService.userExists(user.getUsername(), user.getEmail())) {
            throw new RuntimeException("Username or email address already in use.");
        }
        AppClient client = this.userService.register(user);
        return new ResponseEntity<AppClient>(client, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessRegisterDto business) {
        if (this.userService.businessExists(business.getBusinessName()) || this.userService.userExists(business.getUsername(), business.getEmail())) {
            throw new RuntimeException("Username or email address already in use.");
        }
        BusinessOwner businessOwner = this.userService.registerBusiness(business);
        return new ResponseEntity<BusinessOwner>(businessOwner, HttpStatus.CREATED);
    }

    @GetMapping("/client")
    public AppClient showUserDetails(@RequestParam String username) {
        return this.userService.findAppClientByUsername(username);
    }

    @GetMapping("/business")
    public BusinessOwner showBusinessDetails(@RequestParam String username) {
        return this.userService.findBusinessByUsername(username);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateAppClientDto user) {
        AppClient client = this.userService.findAppClientById(user.getId());
        client.setPassword(this.passwordEncoder.encode(user.getPassword()));
        client.setGender(user.getGender());
        client.setFullName(user.getFullName());
        this.userService.saveUpdatedUserClient(client);
        return new ResponseEntity<AppClient>(client, HttpStatus.CREATED);
    }
    
    @PutMapping("/password")
    public ResponseEntity<?> setUpNewPassword(@RequestParam Long id, String password) {
        UserEntity userById = this.userService.findUserById(id);
        this.userService.saveUserWithUpdatedPassword(userById);
        return new ResponseEntity<UserEntity>(userById, HttpStatus.CREATED);
    }

    @PutMapping("/business")
    public ResponseEntity<?> updateBusiness(@RequestBody UpdateBusinessDto business) {
        BusinessOwner businessOwner = this.userService.findBusinessOwnerById(business.getId());
        if (this.userService.businessExists(business.getBusinessName()) && (!businessOwner.getBusinessName().equals(business.getBusinessName()))) {
            throw new RuntimeException("Business name already in use.");
        }
        businessOwner.setBusinessName(business.getBusinessName());
        businessOwner.setAddress(business.getAddress());
        this.userService.saveUpdatedUser(businessOwner);

        return new ResponseEntity<BusinessOwner>(businessOwner, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable Long id) {
        boolean isRemoved = this.userService.deleteUser(id);
        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestBody Map<String, String> authenticationRequest) throws Exception {
        String username = authenticationRequest.get("username");
        String password = authenticationRequest.get("password");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    
    public String logInUser(@RequestParam String username) {
        UserEntity userByUsername = this.userService.findUserByUsername(username);
        if (userByUsername.getRoles().stream()
                .anyMatch(u -> u.getRole().equals(UserRoleEnum.USER))) {
            return "USER";
        } else if (userByUsername.getRoles().stream()
                .anyMatch(u -> u.getRole().equals(UserRoleEnum.BUSINESS_USER))) {
            return "BUSINESS_USER";
        }
        return null;
    }
}


