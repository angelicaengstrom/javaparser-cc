package com.github.javaparser.ast.resolution.logic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserAdapter;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsMoreSpecificTest extends MethodResolutionLogic {

    protected SymbolResolver symbolResolver(TypeSolver typeSolver) {
        return new JavaSymbolSolver(typeSolver);
    }

    protected JavaParser createParserWithResolver(TypeSolver typeSolver) {
        return new JavaParser(new ParserConfiguration().setSymbolResolver(symbolResolver(typeSolver)));
    }

    @Test
    public void testIfNotVariadicMethodDeclaration() {
        String sample =
                "public class Main {\n " +
                "   void method1();{\n" +
                "   }\n" +
                "   void method2(int i, String... string);{\n" +
                "       i++;\n" +
                "   }\n"+
                "}";
        CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver())).parse(sample);
        ResolvedMethodDeclaration methodA = cu.findAll(MethodDeclaration.class).get(0).resolve();
        ResolvedMethodDeclaration methodB = cu.findAll(MethodDeclaration.class).get(1).resolve();
        List<ResolvedType> argTypes = new ArrayList<>();
        assertTrue(isMoreSpecific(methodA, methodB, argTypes));
    }
    @Test
    public void testIfVariadicMethodDeclaration() {
        String sample =
                "public class Main {\n " +
                "   void method1(String... string);{\n" +
                "   }\n" +
                "   void method2(int i, String... string);{\n" +
                "    \n" +
                "   }\n"+
                "}";
        CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver())).parse(sample);
        ResolvedMethodDeclaration methodA = cu.findAll(MethodDeclaration.class).get(0).resolve();
        ResolvedMethodDeclaration methodB = cu.findAll(MethodDeclaration.class).get(1).resolve();
        List<ResolvedType> argTypes = new ArrayList<>();
        assertFalse(isMoreSpecific(methodA, methodB, argTypes));
    }
    @Test
    public void testIfBothAreVariadicMethodDeclaration() {
        String sample =
                "public class Main {\n " +
                "   void method1(String... string);{\n" +
                "   }\n" +
                "   void method2(String... string);{\n" +
                "    \n" +
                "   }\n"+
                "}";
        CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver())).parse(sample);
        ResolvedMethodDeclaration methodA = cu.findAll(MethodDeclaration.class).get(0).resolve();
        ResolvedMethodDeclaration methodB = cu.findAll(MethodDeclaration.class).get(1).resolve();
        List<ResolvedType> argTypes = new ArrayList<>();
        assertFalse(isMoreSpecific(methodA, methodB, argTypes));

    }

}