package com.qw.event.withdraw;

import java.io.Serializable;

public class PointWithdrawEvent implements Serializable {
    private int withdrawId;

    public PointWithdrawEvent(int withdrawId) {
        this.withdrawId = withdrawId;
    }

    public int getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(int withdrawId) {
        this.withdrawId = withdrawId;
    }
}
