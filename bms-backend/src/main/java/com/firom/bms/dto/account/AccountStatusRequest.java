package com.firom.bms.dto.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountStatusRequest {
    private String status;
}
