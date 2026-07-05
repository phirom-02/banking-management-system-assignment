package com.geekybyte.bmsgui.api;

import com.geekybyte.bmsgui.core.ApiClient;
import com.geekybyte.bmsgui.model.DashboardDto;

public class DashboardApi {

    private final ApiClient client = new ApiClient();

    public DashboardDto getSummary() {
        return client.get("/dashboard", DashboardDto.class);
    }
}
