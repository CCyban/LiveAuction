package com.github.ccyban.liveauction.shared.models.classes;

import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;

import java.io.Serializable;
import java.util.UUID;

public class SocketRequest implements Serializable {
    public SocketRequestType requestType;
    public UUID targetUUID;

    public SocketRequest(SocketRequestType _requestType, UUID _targetUUID) {
        requestType = _requestType;
        targetUUID = _targetUUID;
    }
}
