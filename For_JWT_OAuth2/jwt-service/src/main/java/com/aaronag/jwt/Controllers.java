package com.aaronag.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

record LoginRequest(String username, String password) {

}
record TokenResponse(String token) {

}

@RestController
public class Controllers {
    @Autowired AuthenticationManager authMgr;
    @Autowired JwtService jwt;

    @PostMapping("/auth/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authMgr.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwt.generate(((UserDetails) auth.getPrincipal()).getUsername(), role);
        return new TokenResponse(token);
    }

    @GetMapping("/secure/hello")
    public String secureHello() { return "Hello (JWT)"; }
}
