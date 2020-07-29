package com.travelplanner.travelplanner_server.restservice;

import com.travelplanner.travelplanner_server.model.User;
import com.travelplanner.travelplanner_server.mongodb.dal.UserDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.travelplanner.travelplanner_server.services.JwtUserDetailsService;
import com.travelplanner.travelplanner_server.restservice.config.JwtTokenUtil;
import com.travelplanner.travelplanner_server.restservice.payload.JwtRequest;
import com.travelplanner.travelplanner_server.restservice.payload.JwtResponse;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDAL userDAL;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        System.out.println("can we got here???");
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        System.out.println("but not here!");
//        final UserDetails userDetails = userDetailsService
//                .loadUserByUsername(authenticationRequest.getUsername());
        User user = userDAL.findUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(user);
        System.out.println("token is:" + token);
        return ResponseEntity.ok(new JwtResponse(token));
    }
    private void authenticate(String username, String password) throws Exception {
        try {
            System.out.println("Trying!!!");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
