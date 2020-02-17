package com.qw.event.good;

import java.io.Serializable;

public class GoodVisitEvent implements Serializable {
    private static final long serialVersionUID = 5996561485210365131L;

    private int userId = 0;
    private int shareUserId = 0;
    private long goodId;

    public GoodVisitEvent(long goodId, int userId, int shareUserId) {
        this.goodId = goodId;
        this.userId = userId;
        this.shareUserId = shareUserId;
    }

    public int getShareUserId() {
        return shareUserId;
    }

    public long getGoodId() {
        return goodId;
    }

    public int getUserId() {
        return userId;
    }
}