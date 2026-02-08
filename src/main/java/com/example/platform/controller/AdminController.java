//Endpoint-e vetëm për ADMIN:
//
//listimi i user-ave (p.sh. për DataTable server-side)
//
//edit user
//
//delete user
//
//Këto endpoint-e janë ato që ushqejnë Admin Panel në frontend.
package com.example.platform.controller;

import com.example.platform.dto.*;
import com.example.platform.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public DataTableResponse<UserResponse> getUsers(@RequestBody DataTableRequest req) {
        return adminService.getUsers(req);
    }



    @PutMapping("/user/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest req) {
        return adminService.updateUser(id, req);
    }


    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }
}

