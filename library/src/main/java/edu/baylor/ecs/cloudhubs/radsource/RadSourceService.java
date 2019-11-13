package edu.baylor.ecs.cloudhubs.radsource;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RadSourceService {
    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) {
        RadSourceResponseContext responseContext = new RadSourceResponseContext(request);

        return responseContext;
    }
}
