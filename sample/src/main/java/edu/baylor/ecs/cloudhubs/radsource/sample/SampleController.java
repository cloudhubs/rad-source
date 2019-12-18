package edu.baylor.ecs.cloudhubs.radsource.sample;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rad/sample")
public class SampleController {
    @GetMapping("/get-mapping")
    public SampleModel doGetMapping() {
        return new SampleModel();
    }

    @PostMapping("/post-mapping")
    public SampleModel doPostMapping(@RequestBody SampleModel sampleModel) {
        return sampleModel;
    }

    @RequestMapping("/request-mapping-get")
    public SampleModel doRequestMappingGet() {
        return new SampleModel();
    }

    @RequestMapping(value = "/request-mapping-post", method = RequestMethod.POST)
    public SampleModel doRequestMappingPost(@RequestBody SampleModel sampleModel) {
        return sampleModel;
    }
}
