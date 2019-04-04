// TODO: Header goes here

package com.logicpd.papapill.device.serial;

import com.logicpd.papapill.device.enums.TinyGStatusCode;

/**
 * This helper class defines the response footer object and is used by Response Parser to hold
 * the JSON fields from Tinyg's response message. The footer contains four fields in an array
 * form.
 * Example Response JSON: {"r":{"xvm":12000},"f":[1,0,255,1234]}
 * The footer portion: "f":[<protocol_version>, <status_code>, <input_available>, <checksum>]
 */
public class ResponseFooter {

    // Tinyg's footer array protocol version. This is set to 1 by default.
    private static int protocolVersion = 1;

    // Response status code. Default to the OK status code (0).
    private static TinyGStatusCode statusCode = TinyGStatusCode.OK;

    // Indicates how many characters were removed from the serial RX buffer to process this command.
    // Tinyg's internal RX buffer can hold 254 characters. During large gcode sequences, we may
    // have to throttle sending commands if the number characters removed cannot keep up with the
    // number of characters we're sending.
    public static int rxRecvd = 254;

    // According to the TinyG wiki page: https://github.com/synthetos/TinyG/wiki/JSON-Details
    // The checksum is generated for the JSON line up to but not including the comma preceding the
    // checksum itself. I.e, the comma is where the nul termination would exist. The checksum is
    // computed as a [Java hashcode](http://en.wikipedia.org/wiki/Java_hashCode(\)) from which
    // a modulo 9999 is taken to limit the length to no more than 4 characters.
    private static long checkSum;

    public ResponseFooter() { }

    // Some getters and setters
    public int getProtocolVersion() { return ResponseFooter.protocolVersion; }
    public void setProtocolVersion(int version) { ResponseFooter.protocolVersion = version; }

    public TinyGStatusCode getStatusCode() { return ResponseFooter.statusCode; }
    public void setStatusCode(int statusCode) { ResponseFooter.statusCode = TinyGStatusCode.valueOf(statusCode); }

    public int getRxRecvd() { return ResponseFooter.rxRecvd; }
    public void setRxRecvd(int rxRecvd) { ResponseFooter.rxRecvd = rxRecvd; }

    public long getCheckSum() { return ResponseFooter.checkSum; }
    public void setCheckSum(long checkSum) { ResponseFooter.checkSum = checkSum; }
}