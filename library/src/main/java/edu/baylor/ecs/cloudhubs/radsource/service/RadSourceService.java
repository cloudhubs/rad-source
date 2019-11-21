package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RestCall;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class RadSourceService {
    private final RestCallService restCallService;

    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
        RadSourceResponseContext responseContext = new RadSourceResponseContext();
        responseContext.setRequest(request);

        String filePath = request.getPathToSource();
        List<RestCall> restCalls = new ArrayList<>();

        for (File sourceFile : getSourceFiles(filePath)) {
            restCalls.addAll(restCallService.findRestCalls(sourceFile));
        }

        responseContext.setRestCalls(restCalls);

        return responseContext;
    }

    private List<File> getSourceFiles(String directoryOrFile) {
        if (directoryOrFile.endsWith(".java")) { // not a directory, but a single java file
            return new ArrayList<>(Collections.singletonList(new File(directoryOrFile)));
        } else {
            return (List<File>) FileUtils.listFiles(new File(directoryOrFile), new String[]{"java"}, true);
        }
    }
}
