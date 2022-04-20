package com.pj.squashrestapp.dto;

import lombok.Value;

import java.util.UUID;

/** */
@Value
public class TokenPair {

    String jwtAccessToken;
    UUID refreshToken;
}
