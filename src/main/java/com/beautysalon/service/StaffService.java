package com.beautysalon.service;

import com.beautysalon.dto.StaffResponse;
import com.beautysalon.enums.Category;
import com.beautysalon.enums.Role;
import com.beautysalon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final UserRepository userRepository;

    public StaffService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<StaffResponse> getByCategory(Category category){

        return userRepository
                .findByCategoryAndRole(category, Role.STAFF)
                .stream()
                .map(user -> new StaffResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getCategory()
                ))
                .toList();
    }
}