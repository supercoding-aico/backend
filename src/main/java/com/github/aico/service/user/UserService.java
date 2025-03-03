package com.github.aico.service.user;

import com.github.aico.repository.user.UserProfileImageRepository;
import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.user.request.ProfileUpdateRequest;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.repository.user.UserProfileImage;
import com.github.aico.web.dto.user.response.ResponseDto;
import com.github.aico.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.github.aico.service.s3.S3Uploader;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final S3Uploader s3Uploader;

    public ResponseDto getUserInfo(User user) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail()+"에 해당하는 사용자를 찾을 수 없습니다."));

        String imageUrl = userProfileImageRepository.findByUser(existUser)
                .map(UserProfileImage::getImageUrl)
                .orElse("https://aicoproject.s3.ap-northeast-2.amazonaws.com/default-profile.PNG"); // 기본 이미지 설정

        UserInfo userInfo = UserInfo.of(existUser, imageUrl);

        return new ResponseDto(HttpStatus.OK.value(), "프로필 조회 성공", userInfo);
    }

    public ResponseDto updateProfile(User user, ProfileUpdateRequest updateRequest) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        existUser.updateProfile(updateRequest);
        userRepository.save(existUser);
        return new ResponseDto(HttpStatus.OK.value(), "프로필 수정 성공");
    }

    @Transactional
    public ResponseDto deleteProfile(User user) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail() + "에 해당하는 사용자를 찾을 수 없습니다."));

        userRepository.delete(existUser);
        return new ResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공");
    }

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

}