package edu.baylor.ecs.cloudhubs.radsource.app;

import edu.baylor.ecs.cloudhubs.radsource.service.RadSourceService;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class Controller {
    private final RadSourceService radSourceService;

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    @ResponseBody
    public RadSourceResponseContext getRadSourceResponseContext(@RequestBody RadSourceRequestContext request) throws IOException {
        return radSourceService.generateRadSourceResponseContext(request);
    }
}
