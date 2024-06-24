package org.example.gobang.model;

import lombok.Data;

@Data
public class GameReadyResponse {
    private String message;
    private boolean ok;
    private String reason;
    private String roomId;
    private Integer thisUser;
    private Integer thatUser;
    private Integer whiteUser;
}
