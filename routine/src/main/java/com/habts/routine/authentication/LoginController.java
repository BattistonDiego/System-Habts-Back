package com.habts.routine.authentication;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/habitos/auth")
public class LoginController {


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        if("admin@admin.com".equals(request.email()) && "admin123".equals(request.senha())){
            return ResponseEntity.ok(
                    Map.of("token", "fake-token")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}
