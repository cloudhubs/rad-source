package edu.baylor.ecs.cloudhubs.radsource.graph;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.model.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class generates a Graphviz file from {@link RadSourceResponseContext}.
 * The Graphviz file visualizes the rest flows among the microservices.
 *
 * @author Dipta Das
 */

public class GVGenerator {
    public static void generate(RadSourceResponseContext responseContext) {
        StringBuilder graph = new StringBuilder();
        graph.append("digraph cil_rad {").append("\n");
        graph.append("rankdir = LR;").append("\n");
        graph.append("node [shape=box];").append("\n");

        int clusterIndex = 0;

        Map<String, Set<String>> clusters = getClusters(responseContext);

        for (String key : clusters.keySet()) {
            StringBuilder cluster = new StringBuilder();

            cluster.append("subgraph cluster_").append(clusterIndex++).append(" {").append("\n")
                    .append("label = ").append(key).append(";").append("\n")
                    .append("color=blue;").append("\n")
                    .append("rank = same;");

            Set<String> entities = clusters.get(key);

            for (String entity : entities) {
                cluster.append(" ").append(entity).append(";");
            }

            cluster.append("\n").append("}").append("\n");
            graph.append(cluster);
        }

        for (RestFlow restFlow : responseContext.getRestFlows()) {

            String nodeFrom = getFullMethodName(restFlow.getClient());

            String nodeTo = getFullMethodName(restFlow.getEndpoint());
            String label = getLinkLabel(restFlow.getClient());

            String link = String.format("%s  -> %s [ label = %s ];", nodeFrom, nodeTo, label);
            graph.append(link).append("\n");

        }

        for (MQFlow mqFlow : responseContext.getMqFlows()) {

            String nodeFrom = getFullMethodName(mqFlow.getConsumer());

            String nodeTo = getFullMethodName(mqFlow.getProducer());
            String label = getLinkLabel(mqFlow.getConsumer());

            String link = String.format("%s  -> %s [ label = %s ];", nodeFrom, nodeTo, label);
            graph.append(link).append("\n");

        }

        graph.append("}");

        try (PrintWriter out = new PrintWriter(responseContext.getRequest().getPathToMsRoots().get(0)+"-SAR.gv")) {
            out.println(graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Set<String>> getClusters(RadSourceResponseContext responseContext) {
        Map<String, Set<String>> clusters = new HashMap<>();

        for (RestFlow restFlow : responseContext.getRestFlows()) {
            String nodeFrom = getFullMethodName(restFlow.getClient());
            addToMap(clusters, addDoubleQuotations(restFlow.getClient().getMsRoot()), nodeFrom);

            String nodeTo = getFullMethodName(restFlow.getEndpoint());
            addToMap(clusters, addDoubleQuotations(restFlow.getEndpoint().getMsRoot()), nodeTo);
        }
        for (MQFlow mqFlow : responseContext.getMqFlows()) {
            String nodeFrom = getFullMethodName(mqFlow.getProducer());
            addToMap(clusters, addDoubleQuotations(mqFlow.getProducer().getMsRoot()), nodeFrom);

            String nodeTo = getFullMethodName(mqFlow.getConsumer());
            addToMap(clusters, addDoubleQuotations(mqFlow.getConsumer().getMsRoot()), nodeTo);
        }

        return clusters;
    }

    private static String addDoubleQuotations(String str) {
        return "\"" + str + "\"";
    }

    private static void addToMap(Map<String, Set<String>> m, String key, String value) {
        if (!m.containsKey(key)) {
            m.put(key, new HashSet<>());
        }
        m.get(key).add(value);
    }

    private static String getLinkLabel(RestCall restCall) {
        return addDoubleQuotations(restCall.getHttpMethod() + " " + restCall.getUrl());
    }

    private static String getLinkLabel(MessageQueue messageQueue) {
        return addDoubleQuotations("JMS Message Service");
    }

//    private static String getFullMethodName(RestEntity restEntity) {
//        return addDoubleQuotations(restEntity.getClassName() + "." + restEntity.getMethodName());
//    }

    private static String getFullMethodName(RestCall restCall) {
        return addDoubleQuotations(restCall.getParentMethod());
    }

    private static String getFullMethodName(RestEndpoint restEndpoint) {
        return addDoubleQuotations(restEndpoint.getParentMethod());
    }

    private static String getFullMethodName(MessageQueue messageQueue) {
        return addDoubleQuotations(messageQueue.getParentMethod());
    }
}
