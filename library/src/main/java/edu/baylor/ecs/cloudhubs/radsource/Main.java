package edu.baylor.ecs.cloudhubs.radsource;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        RadSourceService rs = new RadSourceService();
        String filePath = "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java";
        rs.generateRadSourceResponseContext(new RadSourceRequestContext(filePath));
    }
}
