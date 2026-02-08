//Logjika e admin:
//
//listimi për DataTable (paging + search)
//
//update user
//
//delete user
package com.example.platform.service;

import com.example.platform.dto.*;
import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public UserResponse updateUser(Long id, @Valid UpdateUserRequest req) {
        User user = userRepository.findById(id).orElseThrow();

        user.setEmri(req.getEmri());
        user.setAtesia(req.getAtesia());
        user.setMbiemri(req.getMbiemri());
        user.setNrTel(req.getNrTel());
        user.setDatelindja(req.getDatelindja());
        user.setEmail(req.getEmail());
        user.setRole(Role.valueOf(req.getRole()));

        return UserResponse.from(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public DataTableResponse<UserResponse> getUsers(DataTableRequest req) {

        int page = req.getStart() / req.getLength();
        String q = (req.getSearch() != null) ? req.getSearch().getValue() : null;

        Sort sort = buildSort(req);
        Pageable pageable = PageRequest.of(page, req.getLength(), sort);

        long total = userRepository.count();
        var pageData = userRepository.search(q, pageable);

        var data = pageData.getContent().stream()
                .map(UserResponse::from)
                .toList();

        return new DataTableResponse<>(
                req.getDraw(),
                total,
                pageData.getTotalElements(),
                data
        );
    }

    private Sort buildSort(DataTableRequest req) {
        // default sort
        Sort fallback = Sort.by("id").descending();

        if (req.getOrder() == null || req.getOrder().isEmpty()) return fallback;
        if (req.getColumns() == null || req.getColumns().isEmpty()) return fallback;

        Sort combined = Sort.unsorted();

        for (DataTableRequest.Order ord : req.getOrder()) {
            int colIndex = ord.getColumn();
            if (colIndex < 0 || colIndex >= req.getColumns().size()) continue;

            String field = req.getColumns().get(colIndex).getData();
            if (field == null || field.isBlank()) continue;

            // whitelist për siguri (mos lejo sort në çdo string që vjen nga klienti)
            if (!isAllowedSortField(field)) continue;

            Sort s = "asc".equalsIgnoreCase(ord.getDir())
                    ? Sort.by(field).ascending()
                    : Sort.by(field).descending();

            combined = combined.and(s);
        }

        return combined.isUnsorted() ? fallback : combined;
    }

    private boolean isAllowedSortField(String field) {
        return switch (field) {
            case "id", "emri", "atesia", "mbiemri", "nrTel", "datelindja", "email", "role" -> true;
            default -> false;
        };
    }
}
