//DTO që kthen user-in pa fields sensitive (p.sh. pa password).
package com.example.platform.dto;

import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {

    private Long id;
    private String emri;
    private String atesia;
    private String mbiemri;
    private String nrTel;
    private LocalDate datelindja;
    private String email;
    private String photoUrl;
    private Role role;



    public static UserResponse from(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmri(user.getEmri());
        res.setAtesia(user.getAtesia());
        res.setMbiemri(user.getMbiemri());
        res.setNrTel(user.getNrTel());
        res.setDatelindja(user.getDatelindja());
        res.setEmail(user.getEmail());
        res.setPhotoUrl(user.getPhotoUrl());
        res.setRole(user.getRole());
        return res;
    }
}

