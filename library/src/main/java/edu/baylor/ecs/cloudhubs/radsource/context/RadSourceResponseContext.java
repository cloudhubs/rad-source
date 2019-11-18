package edu.baylor.ecs.cloudhubs.radsource.context;

import edu.baylor.ecs.ciljssa.context.AnalysisContext;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RadSourceResponseContext {
    private RadSourceRequestContext request;
    private AnalysisContext result;
}