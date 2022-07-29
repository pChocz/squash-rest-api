package com.pj.squashrestapp.dto.setresultshistogram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.TreeMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetResultsPlayerHistogramDto {

    private TreeMap<SetResultForHistogram, Integer> resultToCountMap;
}
