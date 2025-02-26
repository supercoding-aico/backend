package com.github.aico.web.controller.user;

import com.github.aico.web.dto.auth.resposne.UserInfo;
import com.github.aico.web.dto.user.request.ProfileUpdateRequest;
import com.github.aico.web.dto.user.response.ResponseDto;
import com.github.aico.service.user.UserService;
import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/info")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseDto getUserInfo(@JwtUser User user) {
        return userService.getUserInfo(user);
    }

    @PutMapping
    public ResponseDto updateProfile(@JwtUser User user, @RequestBody ProfileUpdateRequest updateRequest) {
        return userService.updateProfile(user, updateRequest);
    }

    @DeleteMapping
    public ResponseDto deleteProfile(@JwtUser User user) {
        return userService.deleteProfile(user);
    }
}
