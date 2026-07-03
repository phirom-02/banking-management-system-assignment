package com.firom.bms.services.impl;

import com.firom.bms.dto.dashboard.DashboardResponse;
import com.firom.bms.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    @Override
    public DashboardResponse getSummary() {
        return null;
    }
}
