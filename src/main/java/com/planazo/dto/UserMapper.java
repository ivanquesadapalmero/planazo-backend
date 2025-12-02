package com.planazo.dto;

import com.planazo.dto.response.UserResponse;
import com.planazo.model.User;

public class UserMapper {

    // Convertir User entity a UserResponse DTO
    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .registrationDate(user.getRegistrationDate())
                .active(user.getActive())
                .build();
    }

    // Actualizar User entity desde UpdateProfileRequest
    public static void updateUserFromRequest(User user, com.planazo.dto.request.UpdateProfileRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
    }
}
