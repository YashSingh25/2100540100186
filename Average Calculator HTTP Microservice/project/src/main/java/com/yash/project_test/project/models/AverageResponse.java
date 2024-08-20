package com.yash.project_test.project.models;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class AverageResponse {

    private List<Integer> numbers;
    private List<Integer> windowPrevState;
    private List<Integer> windowCurrState;
    private double avg;

}
