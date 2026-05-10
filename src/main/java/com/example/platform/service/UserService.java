//Operacionet e user-it (profile update, etj.).
package com.example.platform.service;

import com.example.platform.dto.UpdateProfileRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserResponse getProfile() {
        return UserResponse.from(currentUserService.getCurrentUser());
    }

    public UserResponse updateProfile(UpdateProfileRequest req) {
        User user = currentUserService.getCurrentUser();

        if (!user.getNrTel().equals(req.getNrTel()) && userRepository.existsByNrTel(req.getNrTel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ky numër telefoni ekziston tashmë");
        }

        user.setEmri(req.getEmri());
        user.setAtesia(req.getAtesia());
        user.setMbiemri(req.getMbiemri());
        user.setNrTel(req.getNrTel());
        user.setDatelindja(req.getDatelindja());

        userRepository.save(user);
        return UserResponse.from(user);
    }



    public void uploadPhoto(MultipartFile file) {
        try {
            User user = currentUserService.getCurrentUser();

            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path target = uploadDir.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            user.setPhotoUrl("/uploads/" + filename);
            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Upload failed");
        }
    }

}
