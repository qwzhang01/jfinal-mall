package cn.qw.kit;

import java.util.UUID;

public class IdKit {
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }
}
