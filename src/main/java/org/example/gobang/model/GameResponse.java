package org.example.gobang.model;

import lombok.Data;

@Data
public class GameResponse {
    private String message;
    private Integer userId;
    private Integer row;
    private Integer col;
    private Integer winner;
}
