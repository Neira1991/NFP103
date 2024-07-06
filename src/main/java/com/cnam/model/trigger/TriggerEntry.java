package com.cnam.model.trigger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriggerEntry {
    private String clientId;
    private String accountId;
    private String campaignId;
    /**
     * Timestamp of the first hit that triggered campaign calculation
     */
    private Long minHitTime;
    /**
     * Timestamp of the last hit that triggered campaign calculation
     */
    private Long maxHitTime;

    private boolean isMVT;

    public TriggerEntry(String clientId, String accountId, String campaignId, Long minHitTime, Long maxHitTime) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.campaignId = campaignId;
        this.minHitTime = minHitTime;
        this.maxHitTime = maxHitTime;
    }

    public TriggerEntry(String clientId, String accountId, String campaignId) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.campaignId = campaignId;
        this.minHitTime = 0L;
        this.maxHitTime = 0L;
    }

    @Override
    public String toString() {
        return "[" + clientId + ":" + accountId + ":" + campaignId + ']';
    }


}
