package edu.baylor.ecs.cloudhubs.radsource.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import lombok.extern.slf4j.Slf4j;

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
}
