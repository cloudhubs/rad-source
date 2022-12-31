package edu.baylor.ecs.cloudhubs.radsource.context;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RadSourceRequestContext {
    private List<String> pathToMsRoots;
    private String pathToExtractedJsonDataFile;
}