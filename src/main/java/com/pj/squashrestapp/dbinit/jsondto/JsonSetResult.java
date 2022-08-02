package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonSetResult {

    private int number;
    private Integer firstPlayerResult;
    private Integer secondPlayerResult;
}
