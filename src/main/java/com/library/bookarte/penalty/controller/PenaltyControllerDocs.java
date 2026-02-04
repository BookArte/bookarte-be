package com.library.bookarte.penalty.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.penalty.dto.ReleaseReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
}
