package com.github.aico.service.user;

import com.github.aico.repository.role.Role;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.UserProfileImageRepository;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.user.request.ProfileUpdateRequest;
import com.github.aico.web.dto.user.request.RoleUpdateRequest;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.user.UserProfileImage;
import com.github.aico.repository.role.RoleRepository;
import com.github.aico.repository.user_role.UserRole;
import com.github.aico.repository.user_role.UserRoleRepository;
import com.github.aico.web.dto.user.response.ResponseDto;
import com.github.aico.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.github.aico.service.s3.S3Uploader;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final S3Uploader s3Uploader;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;

    public ResponseDto getUserInfo(User user) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        String imageUrl = userProfileImageRepository.findByUser(existUser)
                .map(UserProfileImage::getImageUrl)
                .orElse("https://aicoproject.s3.ap-northeast-2.amazonaws.com/default-profile.PNG"); // 기본 이미지 설정

        UserInfo userInfo = UserInfo.of(existUser, imageUrl);

        return new ResponseDto(HttpStatus.OK.value(), "프로필 조회 성공", userInfo);
    }


    @CacheEvict(value = "userInfo", key= "#user.userId")
    public ResponseDto updateProfile(User user, ProfileUpdateRequest updateRequest) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        existUser.updateProfile(updateRequest);
        userRepository.save(existUser);
        return new ResponseDto(HttpStatus.OK.value(), "프로필 수정 성공");
    }

    @CacheEvict(value = "userInfo", key= "#user.userId")
    @Transactional
    public ResponseDto deleteProfile(User user) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        userRepository.delete(existUser);
        return new ResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공");
    }

    @CacheEvict(value = "userInfo", key= "#user.userId")
    @Transactional
    public ResponseDto updateProfileImage(User user, MultipartFile profileImage) {
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        String imageUrl;

        // 이미지가 비어 있는 경우 기본 이미지로 설정
        if (profileImage == null || profileImage.isEmpty()) {
            imageUrl = "https://aicoproject.s3.ap-northeast-2.amazonaws.com/default-profile.PNG";
        } else {
            imageUrl = s3Uploader.uploadFile(profileImage);
        }

        UserProfileImage userProfileImage = userProfileImageRepository.findByUser(existUser)
                .orElse(UserProfileImage.createDefault(existUser)); // 없으면 기본값 생성

        userProfileImage.updateImage(imageUrl);
        userProfileImageRepository.save(userProfileImage);

        return new ResponseDto(HttpStatus.OK.value(), "프로필 이미지 변경 성공", imageUrl);
    }

    @Transactional
    public ResponseDto updateUserRole(User currentUser, Long teamId, RoleUpdateRequest roleUpdateRequest) {
        // MANAGER 권한 확인
        if (!hasManagerRole(currentUser, teamId)) {
            throw new AccessDeniedException("역할 수정은 MANAGER만 가능합니다.");
        }

        // 대상 유저 조회
        User targetUser = userRepository.findById(roleUpdateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("userId " + roleUpdateRequest.getUserId() + "에 해당하는 사용자를 찾을 수 없습니다."));

        // MANAGER 본인 수정 불가
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new AccessDeniedException("MANAGER는 자신의 역할을 수정할 수 없습니다.");
        }

        // 팀 존재 여부 확인
        validateTeamId(teamId);

        // teamRole 값 검증 및 변환
        TeamRole newTeamRole;
        try {
            newTeamRole = TeamRole.valueOf(roleUpdateRequest.getTeamRole());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("존재하지 않는 역할입니다: " + roleUpdateRequest.getTeamRole());
        }

        // 대상 유저의 역할 업데이트
        updateTargetUserRole(targetUser, newTeamRole, teamId);

        return new ResponseDto(HttpStatus.OK.value(), "유저 역할 수정 성공");
    }

    private boolean hasManagerRole(User user, Long teamId) {
        // 팀별 MANAGER 확인
        return teamUserRepository.existsByUserAndTeamIdAndTeamRole(user, teamId, TeamRole.MANAGER);
    }

    private void validateTeamId(Long teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("teamId " + teamId + "에 해당하는 팀이 없습니다."));
    }

    private void updateTargetUserRole(User targetUser, TeamRole newTeamRole, Long teamId) {
        TeamUser teamUser = teamUserRepository.findByTeamTeamIdAndUserId(teamId, targetUser.getId())
                .orElseThrow(() -> new NotFoundException("teamId " + teamId + "에 해당하는 유저의 팀 역할 정보를 찾을 수 없습니다."));
        teamUser.updateTeamRole(newTeamRole); // setter 대신 전용 메서드 호출
        teamUserRepository.save(teamUser);
    }
}