package com.youmai.hxsdk.socket;

public class PduBase {
    /****************************************************
     * basic unit of data type length
     */
    public static final int pdu_basic_length = 4;
    public static final int pdu_body_length_index = 53;
    public static final int pdu_header_length = 57;


    /****************************************************
     * index 0. pos:[0-4)
     * the begin flag of a pdu.
     */
    public static final int flag = 0x66aa;

    /****************************************************
     * index 1. pos:[4-40)
     */
    public byte[] user_id = new byte[36];

    /****************************************************
     * index 2. pos:[40-44)
     */
    public int service_id;

    /****************************************************
     * index 3. pos:[44-48)
     */
    public int command_id;

    /****************************************************
     * index 4. pos:[48-52)
     */
    public int seq_id;

    /****************************************************
     * index 5, [52,53)
     * pdu verions define.
     */
    public byte version;

    /*********************************************
     * index 6, [53,57)
     * pdu body length.
     */

    public int length;

    /***************************************************
     * index 7. pos:[57-infinity)
     */
    public byte[] body;
}


