package com.library.bookarte.penalty.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.penalty.dto.PenaltyResDto;
import com.library.bookarte.penalty.dto.ReleaseReqDto;
import com.library.bookarte.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/penalty")
public class PenaltyController implements PenaltyControllerDocs {

    private final PenaltyService penaltyService;

    @Override
    public ResponseEntity<GlobalResponseDto<Long>> release(@PathVariable Long penaltyId,
                                                      @AuthenticationPrincipal Long memeberId,
                                                      @RequestBody ReleaseReqDto releaseReqDto){
        Long result = penaltyService.releasePenalty(penaltyId, memeberId,releaseReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Long>> revoke(@PathVariable Long penaltyId) {
        Long result = penaltyService.revokePenalty(penaltyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Long>> update(@PathVariable Long penaltyId,
                                                          @RequestBody ReleaseReqDto releaseReqDto) {
        Long result = penaltyService.updateReason(penaltyId,releaseReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
    @Override
    public ResponseEntity<GlobalResponseDto<List<PenaltyResDto>>> getMemberPenaltys(@RequestParam String memberUserId){
        List<PenaltyResDto> result = penaltyService.getPenaltyList(memberUserId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }
}
