package com.github.ccyban.liveauction.shared.models.enumerations;

import java.io.Serializable;

public enum SocketRequestType implements Serializable {
    SignIn,
    GetListOfAllAuctions,
    GetAuctionDetailsById,
    PostAuctionBid,
    CloseAllSubscriptions,
    GetPublicKey,
    PostSymmetricKey,
    ToggleAuctionFollowByIds
}
