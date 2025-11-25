package org.example.remedy.domain.dropping.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.remedy.domain.dropping.application.dto.request.DroppingCreateRequest;
import org.example.remedy.domain.dropping.domain.DroppingType;

public class DroppingTypeValidator implements ConstraintValidator<ValidDroppingType, DroppingCreateRequest> {

    @Override
    public boolean isValid(DroppingCreateRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        return switch (request.type()) {
            case MUSIC -> validateMusicType(request, context);
            case VOTE -> validateVoteType(request, context);
            case PLAYLIST -> validatePlaylistType(request, context);
        };
    }

    private boolean validateMusicType(DroppingCreateRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (request.songId() == null || request.songId().isBlank()) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입은 songId가 필수입니다")
                    .addPropertyNode("songId")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.topic() != null) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입에는 topic을 포함할 수 없습니다")
                    .addPropertyNode("topic")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.options() != null && !request.options().isEmpty()) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입에는 options를 포함할 수 없습니다")
                    .addPropertyNode("options")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.playlistId() != null) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입에는 playlistId를 포함할 수 없습니다")
                    .addPropertyNode("playlistId")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.playlistName() != null) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입에는 playlistName을 포함할 수 없습니다")
                    .addPropertyNode("playlistName")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.songIds() != null && !request.songIds().isEmpty()) {
            context.buildConstraintViolationWithTemplate("MUSIC 타입에는 songIds를 포함할 수 없습니다")
                    .addPropertyNode("songIds")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateVoteType(DroppingCreateRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (request.topic() == null || request.topic().isBlank()) {
            context.buildConstraintViolationWithTemplate("VOTE 타입은 topic이 필수입니다")
                    .addPropertyNode("topic")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.options() == null || request.options().isEmpty()) {
            context.buildConstraintViolationWithTemplate("VOTE 타입은 options가 필수입니다 (최소 2개)")
                    .addPropertyNode("options")
                    .addConstraintViolation();
            isValid = false;
        } else if (request.options().size() < 2 || request.options().size() > 5) {
            context.buildConstraintViolationWithTemplate("VOTE 타입의 options는 2~5개여야 합니다")
                    .addPropertyNode("options")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.songId() != null) {
            context.buildConstraintViolationWithTemplate("VOTE 타입에는 songId를 포함할 수 없습니다")
                    .addPropertyNode("songId")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.playlistId() != null) {
            context.buildConstraintViolationWithTemplate("VOTE 타입에는 playlistId를 포함할 수 없습니다")
                    .addPropertyNode("playlistId")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.playlistName() != null) {
            context.buildConstraintViolationWithTemplate("VOTE 타입에는 playlistName을 포함할 수 없습니다")
                    .addPropertyNode("playlistName")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.songIds() != null && !request.songIds().isEmpty()) {
            context.buildConstraintViolationWithTemplate("VOTE 타입에는 songIds를 포함할 수 없습니다")
                    .addPropertyNode("songIds")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePlaylistType(DroppingCreateRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        // playlistId와 (playlistName + songIds) 중 하나만 있어야 함
        boolean hasPlaylistId = request.playlistId() != null && !request.playlistId().isBlank();
        boolean hasNewPlaylist = (request.playlistName() != null && !request.playlistName().isBlank()) ||
                                  (request.songIds() != null && !request.songIds().isEmpty());

        if (!hasPlaylistId && !hasNewPlaylist) {
            context.buildConstraintViolationWithTemplate("PLAYLIST 타입은 playlistId 또는 (playlistName + songIds)가 필수입니다")
                    .addPropertyNode("playlistId")
                    .addConstraintViolation();
            isValid = false;
        } else if (hasPlaylistId && hasNewPlaylist) {
            context.buildConstraintViolationWithTemplate("PLAYLIST 타입은 playlistId와 (playlistName + songIds)를 동시에 사용할 수 없습니다")
                    .addPropertyNode("playlistId")
                    .addConstraintViolation();
            isValid = false;
        }

        // 새 플레이리스트 생성 시 validation
        if (!hasPlaylistId && hasNewPlaylist) {
            if (request.playlistName() == null || request.playlistName().isBlank()) {
                context.buildConstraintViolationWithTemplate("새 플레이리스트 생성 시 playlistName이 필수입니다")
                        .addPropertyNode("playlistName")
                        .addConstraintViolation();
                isValid = false;
            }

            if (request.songIds() == null || request.songIds().isEmpty()) {
                context.buildConstraintViolationWithTemplate("새 플레이리스트 생성 시 songIds가 필수입니다 (최소 1개)")
                        .addPropertyNode("songIds")
                        .addConstraintViolation();
                isValid = false;
            } else if (request.songIds().size() > 50) {
                context.buildConstraintViolationWithTemplate("PLAYLIST 타입의 songIds는 최대 50개까지 가능합니다")
                        .addPropertyNode("songIds")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (request.songId() != null) {
            context.buildConstraintViolationWithTemplate("PLAYLIST 타입에는 songId를 포함할 수 없습니다")
                    .addPropertyNode("songId")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.topic() != null) {
            context.buildConstraintViolationWithTemplate("PLAYLIST 타입에는 topic을 포함할 수 없습니다")
                    .addPropertyNode("topic")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.options() != null && !request.options().isEmpty()) {
            context.buildConstraintViolationWithTemplate("PLAYLIST 타입에는 options를 포함할 수 없습니다")
                    .addPropertyNode("options")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
