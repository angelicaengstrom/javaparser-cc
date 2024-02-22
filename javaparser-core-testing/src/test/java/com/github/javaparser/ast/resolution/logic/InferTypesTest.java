package com.github.javaparser.ast.resolution.logic;

import java.nio.file.Path;
import java.util.*;

import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionFactory;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionMethodDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserAdapter;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedTypeVariable;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import static org.junit.jupiter.api.Assertions.*;

class InferTypesTest extends MethodResolutionLogic {
    protected JavaParser createParserWithResolver(TypeSolver typeSolver) {
        return new JavaParser(new ParserConfiguration().setSymbolResolver(symbolResolver(typeSolver)));
    }

    public static class TestClass {
        public boolean testMethod() {
            return true;
        }

        public boolean testMethodWithArgs(boolean x) {
            return true;
        }
    }

    public static boolean[] flags = new boolean[25];

    protected SymbolResolver symbolResolver(TypeSolver typeSolver) {
        return new JavaSymbolSolver(typeSolver);
    }

    private ResolvedType getRefType() {
        String code = "public class Program {\n" +
                "\n" +
                "    public static class InnerClass<T> {\n" +
                "       void method1() {\n" +
                "           new InnerClass<T>();\n" + // Buggy
                "       }\n" +
                "       <T1> void method2() {\n" +
                "           new InnerClass<T1>();\n" + // OK
                "           new InnerClass<T>();\n" + // Buggy
                "       }\n" +
                "    }\n" +
                "}";

        CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver())).parse(code);
        List<ObjectCreationExpr> objCrtExprs = cu.findAll(ObjectCreationExpr.class);
        ObjectCreationExpr expr = objCrtExprs.get(0);
        ResolvedType resRefType = expr.getType().resolve();
        assertTrue(resRefType.isReferenceType());
        return resRefType;
    }

    /*
     * Test if primitive types works.
     */
    @Test
    public void testPrimitive() {
        ResolvedType src = ResolvedPrimitiveType.BOOLEAN;
        ResolvedType tgt = ResolvedPrimitiveType.INT;
        Map<ResolvedTypeParameterDeclaration, ResolvedType> mappings = new HashMap<ResolvedTypeParameterDeclaration, ResolvedType>();

        inferTypes(src, tgt, mappings);
        assertTrue(mappings.size() == 0);
    }


    /*
     * Nothing should be done if the source and target is the same.
     */
    @Test
    public void testSame(){
        ResolvedType t = getRefType();
        Map<ResolvedTypeParameterDeclaration, ResolvedType> mappings = new HashMap<ResolvedTypeParameterDeclaration, ResolvedType>();

        inferTypes(t, t, mappings);
        assertTrue(mappings.size() == 0);
    }

    /*
     * Type variable should be resolved to reference type.
     */
    @Test
    public void testTypeVariableReference(){
        ResolvedType rt = getRefType();
        ResolvedTypeVariable rtv = new ResolvedTypeVariable(ResolvedTypeParameterDeclaration.onType("A", "foo.Bar", Collections.emptyList()));
        Map<ResolvedTypeParameterDeclaration, ResolvedType> mappings = new HashMap<ResolvedTypeParameterDeclaration, ResolvedType>();

        inferTypes(rt, rtv, mappings);
        assertTrue(mappings.size() == 1);
    }

    /*
     * Spread resolved type variables to unresolved one.
     */
    @Test
    public void testTypeVariable(){
        ResolvedTypeVariable rtv = new ResolvedTypeVariable(ResolvedTypeParameterDeclaration.onType("A", "foo.Bar", Collections.emptyList()));
        ResolvedTypeVariable rtv2 = new ResolvedTypeVariable(ResolvedTypeParameterDeclaration.onType("B", "foo.Bar", Collections.emptyList()));
        Map<ResolvedTypeParameterDeclaration, ResolvedType> mappings = new HashMap<ResolvedTypeParameterDeclaration, ResolvedType>();

        inferTypes(rtv, rtv2, mappings);
        assertTrue(mappings.size() == 1);
    }

    @Test
    public void testIsApplicableFalseWhenDifferentNames() throws NoSuchMethodException {
        TypeSolver rts = new ReflectionTypeSolver();

        ResolvedMethodDeclaration m = new ReflectionMethodDeclaration(TestClass.class.getMethod("testMethod"), rts);
        MethodUsage mu = new MethodUsage(m);

        List<ResolvedType> lst = new ArrayList<>();

        String needle = "notTheTestMethod";

        assertFalse(isApplicable(mu, needle, lst, rts));
    }

    @Test
    public void testIsApplicableFalseWhenDifferentArgs() throws NoSuchMethodException {
        TypeSolver rts = new ReflectionTypeSolver();

        ResolvedMethodDeclaration m = new ReflectionMethodDeclaration(TestClass.class.getMethod("testMethodWithArgs", boolean.class), rts);
        MethodUsage mu = new MethodUsage(m);

        List<ResolvedType> lst = new ArrayList<>();
        lst.add(ResolvedPrimitiveType.INT);

        String needle = "testMethodWithArgs";

        assertFalse(isApplicable(mu, needle, lst, rts));
    }

    @BeforeAll
    public static void setupFlags () {
        flags = new boolean[25];
    }

    @AfterAll
    public static void checkFlags() {
        int index = 0;
        int numReached = 0;
        for (boolean flag : flags) {
            if (flag){
                System.out.println("Branch reached:" + index);
                numReached++;
            } else {
                System.out.println("Branch not reached: " + index);
            }
            index++;
        }

        float cov = (float) numReached / (index+1);

        System.out.println("Total coverage: " + cov);
    }

}
