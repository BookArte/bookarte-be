package com.library.bookarte.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberFindIdResponse {
    private List<String> userIds;
}
