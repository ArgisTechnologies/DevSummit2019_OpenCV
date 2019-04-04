package com.logicpd.papapill.device.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contains the indexes of all elements of the TinyG response footer
 * (which is a JSON array).
 */
public enum ResponseFooterIndex {
    INDEX_PROTOCOL_VERSION(0),
    INDEX_STATUS_CODE(1),
    INDEX_RX_RECVD(2),
    INDEX_CHECKSUM(3);

    private int value;
    private static Map map = new HashMap<>();

    private ResponseFooterIndex(int value) {
        this.value = value;
    }

    static {
        for (ResponseFooterIndex pageType : ResponseFooterIndex.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static ResponseFooterIndex valueOf(int index) {
        return (ResponseFooterIndex) map.get(index);
    }

    public int getValue() {
        return value;
    }
}
