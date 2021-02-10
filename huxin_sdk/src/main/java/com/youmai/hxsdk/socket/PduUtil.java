package com.youmai.hxsdk.socket;

import android.util.Log;

import com.youmai.hxsdk.utils.LogFile;

import java.nio.ByteBuffer;


public abstract class PduUtil {

    private static final String TAG = "TcpClient";

    public abstract void OnRec(PduBase pduBase);

    public abstract void OnCallback(PduBase pduBase);


    public int ParsePdu(ByteBuffer buffer) {
        if (buffer.limit() > PduBase.pdu_basic_length) {
            int begin = buffer.getInt(0);
            Log.v(TAG, "begin is " + begin);
            if (begin != PduBase.flag) {
                Log.e(TAG, "header error...");
                LogFile.inStance().toFile("header error...");
                buffer.clear();
                return -1;
            }
        } else {    //did not contain a start flag yet.continue read.
            Log.v(TAG, "did not has full start flag");
            buffer.position(buffer.limit());
            buffer.limit(buffer.capacity());
            return 0;
        }

        if (buffer.limit() >= PduBase.pdu_header_length) {
            //has full header
            int bodyLength = buffer
                    .getInt(PduBase.pdu_body_length_index);
            int totalLength = bodyLength + PduBase.pdu_header_length;

            if (totalLength <= buffer.limit()) {
                //has a full pack.
                byte[] packByte = new byte[totalLength];
                buffer.get(packByte);
                PduBase pduBase = BuildPdu(packByte);
                OnRec(pduBase);
                buffer.compact();
                //read to read.
                buffer.flip();
                return totalLength;

            } else {
                buffer.position(buffer.limit());
                buffer.limit(buffer.capacity());
                return 0;
            }

        } else {
            Log.v(TAG, " not a full header");
            buffer.position(buffer.limit());
            buffer.limit(buffer.capacity());
            return -2;
        }
    }

    private PduBase BuildPdu(byte[] bytes) {
        PduBase units = new PduBase();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        buffer.getInt();  //units.flag
        buffer.get(units.user_id);
        units.service_id = buffer.getInt();
        units.command_id = buffer.getInt();
        units.seq_id = buffer.getInt();
        units.version = buffer.get();

        int length = buffer.getInt();
        units.length = length;
        units.body = new byte[length];
        buffer.get(units.body);
        return units;
    }

    public ByteBuffer serializePdu(PduBase pduBase) {
        int length = PduBase.pdu_header_length + pduBase.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        byteBuffer.clear();

        byteBuffer.putInt(PduBase.flag);
        byteBuffer.put(pduBase.user_id);
        byteBuffer.putInt(pduBase.service_id);
        byteBuffer.putInt(pduBase.command_id);
        byteBuffer.putInt(pduBase.seq_id);
        byteBuffer.put(pduBase.version);
        byteBuffer.putInt(pduBase.length);
        byteBuffer.put(pduBase.body);

        return byteBuffer;

    }


}
