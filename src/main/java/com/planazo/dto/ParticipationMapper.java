package com.planazo.dto;

import com.planazo.dto.response.ParticipantResponse;
import com.planazo.dto.response.ParticipationResponse;
import com.planazo.model.Participation;
import com.planazo.model.User;

public class ParticipationMapper {

    public static ParticipationResponse toParticipationResponse(Participation participation) {
        if (participation == null) {
            return null;
        }

        return ParticipationResponse.builder()
                .id(participation.getId())
                .planId(participation.getPlan().getId())
                .user(UserMapper.toUserResponse(participation.getUser()))
                .status(participation.getStatus())
                .joinedAt(participation.getJoinedAt())
                .leftAt(participation.getLeftAt())
                .build();
    }

    public static ParticipantResponse toParticipantResponse(User user) {
        if (user == null) {
            return null;
        }

        return ParticipantResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
