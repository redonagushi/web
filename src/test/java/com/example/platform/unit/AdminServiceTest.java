package com.example.platform.unit;

import com.example.platform.dto.UpdateUserRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import com.example.platform.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit teste per AdminService.
 * Testohet listimi, perditesimi dhe fshirja e user-ave,
 * duke verifikuar mbrojtjen e llogarive ADMIN.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private AdminService adminService;

    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setEmri("Test");
        normalUser.setAtesia("Testi");
        normalUser.setMbiemri("Testit");
        normalUser.setNrTel("+355692222222");
        normalUser.setDatelindja(LocalDate.of(1995, 5, 5));
        normalUser.setEmail("user@test.com");
        normalUser.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmri("Admin");
        adminUser.setAtesia("System");
        adminUser.setMbiemri("Root");
        adminUser.setEmail("admin@email.com");
        adminUser.setRole(Role.ADMIN);
    }

    // ─── DELETE USER ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteUser() – user normal → fshihet me sukses")
    void deleteUser_normalUser_deletesSuccessfully() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        assertDoesNotThrow(() -> adminService.deleteUser(2L));
        verify(userRepository, times(1)).deleteById(2L);
    }

    @Test
    @DisplayName("deleteUser() – llogari ADMIN → hedh 403 Forbidden")
    void deleteUser_adminAccount_throwsForbidden() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminService.deleteUser(1L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteUser() – ID jo ekzistues → hedh 404 Not Found")
    void deleteUser_nonExistentId_throwsNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminService.deleteUser(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ─── UPDATE USER ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUser() – user normal → perditesuar me sukses")
    void updateUser_normalUser_updatesSuccessfully() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(userRepository.save(any(User.class))).thenReturn(normalUser);

        UserResponse res = adminService.updateUser(2L, buildRequest("Ardit", "USER"));

        assertNotNull(res);
        verify(userRepository, times(1)).save(normalUser);
    }

    @Test
    @DisplayName("updateUser() – user → promovuar ne ADMIN me sukses")
    void updateUser_promoteToAdmin_updatesRole() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminService.updateUser(2L, buildRequest("Test", "ADMIN"));

        assertEquals(Role.ADMIN, normalUser.getRole());
    }

    @Test
    @DisplayName("updateUser() – llogaria ADMIN → hedh 403 Forbidden")
    void updateUser_adminAccount_throwsForbidden() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminService.updateUser(1L, buildRequest("Admin", "ADMIN")));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser() – role i pavlefshëm → hedh 400 Bad Request")
    void updateUser_invalidRole_throwsBadRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminService.updateUser(2L, buildRequest("Test", "SUPERUSER")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("updateUser() – ID jo ekzistues → hedh 404 Not Found")
    void updateUser_nonExistentId_throwsNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminService.updateUser(999L, buildRequest("Test", "USER")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private UpdateUserRequest buildRequest(String emri, String role) {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmri(emri);
        req.setAtesia("Testi");
        req.setMbiemri("Testit");
        req.setNrTel("+355692222222");
        req.setDatelindja(LocalDate.of(1995, 5, 5));
        req.setEmail("user@test.com");
        req.setRole(role);
        return req;
    }
}