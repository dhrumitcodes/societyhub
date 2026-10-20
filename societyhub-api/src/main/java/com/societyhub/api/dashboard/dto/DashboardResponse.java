package com.societyhub.api.dashboard.dto;

import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.membership.SocietyRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private Long societyId;
    private String societyName;
    private SocietyRole myRole;
    private boolean canManage;
    private long memberCount;
    private long upcomingEventCount;
    private Map<String, Long> membersByRole;
    private List<EventResponse> upcomingEvents;
}