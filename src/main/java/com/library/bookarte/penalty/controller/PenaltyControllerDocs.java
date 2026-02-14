package com.library.bookarte.penalty.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.penalty.dto.PenaltyResDto;
import com.library.bookarte.penalty.dto.ReleaseReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Penalty")
public interface PenaltyControllerDocs {

    /*Update: 패널티 해제*/
    @Operation(summary = "관리자 권한으로 패널티 해제", description = "**성공 응답 데이터:** 요청 성공 패널티 id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "페널티 해제 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 패널티 이력"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/admin/{penaltyId}/release")
    ResponseEntity<GlobalResponseDto<Long>> release(@PathVariable Long penaltyId,
                                                    @RequestBody ReleaseReqDto releaseReqDto);

    /*Update: 패널티 해제 철회*/
    @Operation(summary = "관리자 권한으로 패널티 해제 철회", description = "**성공 응답 데이터:** 요청 성공 패널티 id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "페널티 해제 철회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 패널티 이력"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/admin/{penaltyId}/revoke")
    ResponseEntity<GlobalResponseDto<Long>> revoke(@PathVariable Long penaltyId);

    /*Update: 패널티 해제 철회*/
    @Operation(summary = "관리자 권한으로 패널티 해제 사유 변경", description = "**성공 응답 데이터:** 요청 성공 패널티 id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "페널티 해제 사유 변경 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 패널티 이력"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/admin/{penaltyId}/update")
    ResponseEntity<GlobalResponseDto<Long>> update(@PathVariable Long penaltyId,
                                                   @RequestBody ReleaseReqDto releaseReqDto);

    @Operation(summary = "특정 사용자의 패널티 목록 확인", description = "**성공 응답 데이터:** 패널티 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "패널티 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin/list")
    ResponseEntity<GlobalResponseDto<List<PenaltyResDto>>> getMemberPenaltys(@RequestParam String memberUserId);

}
