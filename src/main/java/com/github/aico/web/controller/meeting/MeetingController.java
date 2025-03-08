package com.github.aico.web.controller.meeting;

import com.github.aico.repository.user.User;
import com.github.aico.service.meeting.MeetingService;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.meeting.request.MeetingAiRequest;
import com.github.aico.web.dto.meeting.request.MeetingUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/{teamId}")
    public ResponseDto requestAiSummary(
            @PathVariable Long teamId,
            @RequestBody List<MeetingAiRequest> requestList,
            @AuthenticationPrincipal User user) {
        return meetingService.requestAiSummary(teamId, requestList, user);
    }

    @GetMapping("/{teamId}")
    public ResponseDto getMeetings(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page) {
        return meetingService.getMeetings(teamId, page);
    }

    @PutMapping("/{meetingId}")
    public ResponseDto updateMeeting(
            @PathVariable(name = "meetingId", required = true) Long meetingId,
            @Valid @RequestBody MeetingUpdateRequest request,
            @AuthenticationPrincipal User user) {
        return meetingService.updateMeeting(meetingId, request);
    }

    @DeleteMapping("/{meetingId}")
    public ResponseDto deleteMeeting(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal User user) {
        return meetingService.deleteMeeting(meetingId);
    }
}