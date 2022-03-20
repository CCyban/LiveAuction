package com.github.ccyban.liveauction.shared.models.classes;

import java.io.Serializable;

public class SocketResponse implements Serializable {
    public Object responsePayload;

    public SocketResponse(Object _responsePayload) {
        responsePayload = _responsePayload;
    }
}
