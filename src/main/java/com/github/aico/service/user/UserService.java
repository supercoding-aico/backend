package com.github.aico.service.user;

import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.user.request.ProfileUpdateRequest;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.web.dto.user.response.ResponseDto;
import com.github.aico.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseDto getUserInfo(User user) {
        // 사용자 존재 여부 확인 (이메일 기준)
        User existUser = userRepository.findByEmailUserFetchJoin(user.getEmail())
                .orElseThrow(() -> new NotFoundException(user.getEmail()+"에 해당하는 사용자를 찾을 수 없습니다."));

        return new ResponseDto(HttpStatus.OK.value(), "프로필 조회 성공", UserInfo.from(existUser));
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
}
