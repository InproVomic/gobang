package org.example.gobang.model;

import lombok.Data;

@Data
public class MatchResponse {
    private String message;
    private String reason;
    private boolean ok;
}
