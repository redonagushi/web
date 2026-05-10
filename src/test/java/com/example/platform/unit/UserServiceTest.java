package com.example.platform.unit;

import com.example.platform.dto.UpdateProfileRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import com.example.platform.service.CurrentUserService;
import com.example.platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit teste per UserService.
 * Testohet marrja e profilit dhe perditesimi i tij.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CurrentUserService currentUserService;
    @InjectMocks private UserService userService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmri("Redon");
        currentUser.setAtesia("Hysni");
        currentUser.setMbiemri("Agushi");
        currentUser.setNrTel("+355691234567");
        currentUser.setDatelindja(LocalDate.of(2000, 1, 1));
        currentUser.setEmail("redon@test.com");
        currentUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("getProfile() – kthen profilin e user-it aktual")
    void getProfile_returnsCurrentUserProfile() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        UserResponse res = userService.getProfile();

        assertNotNull(res);
        assertEquals("Redon", res.getEmri());
        assertEquals("redon@test.com", res.getEmail());
    }

    @Test
    @DisplayName("updateProfile() – te dhena valide → perditeson dhe kthen")
    void updateProfile_validData_updatesAndReturns() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.existsByNrTel("+355699999999")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        UpdateProfileRequest req = buildRequest("Ardit", "+355699999999");
        UserResponse res = userService.updateProfile(req);

        assertNotNull(res);
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    @DisplayName("updateProfile() – i njejti nr tel → nuk kontrollon konflikt")
    void updateProfile_samePhone_noConflictCheck() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        UpdateProfileRequest req = buildRequest("Redon", "+355691234567"); // i njëjti tel
        assertDoesNotThrow(() -> userService.updateProfile(req));

        verify(userRepository, never()).existsByNrTel("+355691234567");
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    @DisplayName("updateProfile() – nr tel i nje user tjeter → hedh CONFLICT")
    void updateProfile_phoneOfAnotherUser_throwsConflict() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.existsByNrTel("+355697777777")).thenReturn(true);

        UpdateProfileRequest req = buildRequest("Redon", "+355697777777");

        assertThrows(ResponseStatusException.class, () -> userService.updateProfile(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile() – emri i ri ruhet sakte")
    void updateProfile_newEmri_savedCorrectly() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest req = buildRequest("Besmir", "+355691234567");
        userService.updateProfile(req);

        assertEquals("Besmir", currentUser.getEmri());
    }

    private UpdateProfileRequest buildRequest(String emri, String nrTel) {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setEmri(emri);
        req.setAtesia("Hysni");
        req.setMbiemri("Agushi");
        req.setNrTel(nrTel);
        req.setDatelindja(LocalDate.of(2000, 1, 1));
        return req;
    }
}