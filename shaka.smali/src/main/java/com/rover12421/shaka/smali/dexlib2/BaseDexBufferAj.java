package com.rover12421.shaka.smali.dexlib2;

import com.rover12421.shaka.lib.LogHelper;
import com.rover12421.shaka.lib.reflect.Reflect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jf.dexlib2.dexbacked.BaseDexBuffer;

/**
 * Created by rover12421 on 11/10/15.
 */
@Aspect
public class BaseDexBufferAj {
    public static int getBaseOffset(BaseDexBuffer baseDexBuffer) {
        return Reflect.on(baseDexBuffer).get("baseOffset");
    }

    public static byte[] getBuf(BaseDexBuffer baseDexBuffer) {
        return Reflect.on(baseDexBuffer).get("buf");
    }

    @Around("execution(* org.jf.dexlib2.dexbacked.BaseDexBuffer.readSmallUint(..))" +
            "&& args(offset)")
    public int readSmallUint(ProceedingJoinPoint joinPoint, int offset) {
        BaseDexBuffer thiz = (BaseDexBuffer) joinPoint.getThis();

        byte[] buf = getBuf(thiz);
        offset += getBaseOffset(thiz);
        int result = (buf[offset] & 0xff) |
                ((buf[offset+1] & 0xff) << 8) |
                ((buf[offset+2] & 0xff) << 16) |
                ((buf[offset+3]) << 24);
        if (result < 0) {
            LogHelper.warning(String.format("Encountered small uint that is out of range at offset 0x%x", offset));
        }
        return result;
    }

    @Around("execution(* org.jf.dexlib2.dexbacked.BaseDexBuffer.readOptionalUint(..))" +
            "&& args(offset)")
    public int readOptionalUint(ProceedingJoinPoint joinPoint, int offset) {
        BaseDexBuffer thiz = (BaseDexBuffer) joinPoint.getThis();

        byte[] buf = getBuf(thiz);
        offset += getBaseOffset(thiz);
        int result = (buf[offset] & 0xff) |
                ((buf[offset+1] & 0xff) << 8) |
                ((buf[offset+2] & 0xff) << 16) |
                ((buf[offset+3]) << 24);
        if (result < -1) {
            LogHelper.warning(String.format("Encountered optional uint that is out of range at offset 0x%x", offset));
        }
        return result;
    }
}
