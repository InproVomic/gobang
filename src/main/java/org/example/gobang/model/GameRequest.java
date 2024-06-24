package org.example.gobang.model;

import lombok.Data;

@Data
public class GameRequest {
    private String message;
    private Integer userId;
    private Integer row;
    private Integer col;
}
