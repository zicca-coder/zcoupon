package com.zicca.zcoupon.framework.exeception;

import com.zicca.zcoupon.framework.errorcode.BaseErrorCode;
import com.zicca.zcoupon.framework.errorcode.IErrorCode;

/**
 * 远程调用异常
 *
 * @author zicca
 */
public class RemoteException extends AbstractException {

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
