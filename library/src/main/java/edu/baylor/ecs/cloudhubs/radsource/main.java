package edu.baylor.ecs.cloudhubs.radsource;

import edu.baylor.ecs.cloudhubs.radsource.service.RestEndpointService;

import java.io.File;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        new RestEndpointService().findRestEndpoints(new File(
                "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java"));
    }
}
