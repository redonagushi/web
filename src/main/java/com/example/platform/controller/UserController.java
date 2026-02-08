//Endpoint-e për user normal:
//
//merr profilin (/me)
//
//update profil (PUT /me)
//
//upload/ndryshim foto (nëse e ke)
//
//Zakonisht kërkon JWT.
package com.example.platform.controller;

import com.example.platform.dto.RegisterRequest;
import com.example.platform.dto.UpdateProfileRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    protected final UserService userService;

    @GetMapping("/profile")
    public UserResponse profile() {
        return userService.getProfile();
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(@RequestBody @Valid UpdateProfileRequest req) {
        return userService.updateProfile(req);
    }


    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam MultipartFile file) {
        userService.uploadPhoto(file);
        return ResponseEntity.ok("Photo updated");
    }
}

