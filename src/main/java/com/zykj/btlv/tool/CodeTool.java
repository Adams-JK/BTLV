package com.zykj.btlv.tool;

import java.util.UUID;

public class CodeTool {

    public static String generateUniqueCode() {
        UUID uuid = UUID.randomUUID();
        String code = uuid.toString().replace("-", "").substring(0, 16);
        return code;
    }
}
