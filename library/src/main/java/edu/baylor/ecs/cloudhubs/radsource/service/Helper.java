package edu.baylor.ecs.cloudhubs.radsource.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@Slf4j
public class Helper {
    public static String findFQClassName(CompilationUnit cu, String param) {
        if (param.equals("String")) {
            return "java.lang.String";
        } else if (param.equals("Object")) {
            return "java.lang.Object";
        }

        for (ImportDeclaration id : cu.findAll(ImportDeclaration.class)) {
            if (id.getNameAsString().endsWith(param)) {
                log.debug("import: " + id.getNameAsString());
                return id.getNameAsString();
            }
        }

        return param; // if FQ name not found then return original
    }

    public static String findPackage(CompilationUnit cu) {
        for (PackageDeclaration pd : cu.findAll(PackageDeclaration.class)) {
            return pd.getNameAsString();
        }
        return null;
    }

    public static String mergePaths(String classPath, String methodPath) {
        if (classPath.startsWith("/")) classPath = classPath.substring(1);
        if (methodPath.startsWith("/")) methodPath = methodPath.substring(1);

        String path = FilenameUtils.normalizeNoEndSeparator(FilenameUtils.concat(classPath, methodPath), true);
        if (!path.startsWith("/")) path = "/" + path;

        return path;
    }

    public static String removeEnclosedQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedBraces(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("{") && s.endsWith("}")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedSingleQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static boolean matchUrl(String clientUrl, String serverUrl) {
        if (clientUrl == null || serverUrl == null) return false;
        return removeAmbiguity(clientUrl).equals(removeAmbiguity(serverUrl));
    }

    public static String unifyPathVariable(String url) {
        return url.replaceAll("\\{[^{]*?}", "{var}");
    }

    public static String removeAmbiguity(String url) {
        return unifyPathVariable(url).replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String mergeUrlPath(String url, String path) {
        url = Helper.removeEnclosedSingleQuotations(url);
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (path != null && path.length() > 1) url = url + path; // merge if path not empty
        return url;
    }
}
