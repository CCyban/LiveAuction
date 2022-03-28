package com.github.ccyban.liveauction.shared.models.classes;

import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;

import java.io.Serializable;
import java.util.UUID;

public class SocketRequest implements Serializable {
    public SocketRequestType requestType;
    public UUID targetUUID;
    public Object requestPayload;

    public SocketRequest(SocketRequestType _requestType, UUID _targetUUID, Object _requestPayload) {
        requestType = _requestType;
        targetUUID = _targetUUID;
        requestPayload = _requestPayload;
    }
}
