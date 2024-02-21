package com.github.javaparser.ast.resolution.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.UnsolvedSymbolException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserAdapter;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class FindMostApplicableTest extends MethodResolutionLogic {
    protected JavaParser createParserWithResolver(TypeSolver typeSolver) {
        return new JavaParser(new ParserConfiguration().setSymbolResolver(symbolResolver(typeSolver)));
    }

    protected SymbolResolver symbolResolver(TypeSolver typeSolver) {
        return new JavaSymbolSolver(typeSolver);
    }

    @Test
    public void testNoApplicableMethodsFound() {
        List<ResolvedMethodDeclaration> candidateSolvedMethods = new ArrayList<>();
        List<ResolvedType> argumentsTypes = new ArrayList<>();
        TypeSolver typeSolver = new ReflectionTypeSolver();

        SymbolReference<ResolvedMethodDeclaration> applicableMethods = findMostApplicable(candidateSolvedMethods, "",
                argumentsTypes, typeSolver, false);

        assertThrows(UnsolvedSymbolException.class, () -> applicableMethods.getCorrespondingDeclaration());

    }

    @Test
    public void testSingleApplicableMethodFound() {
        String code = "public class Main {\n" +
                "\n" +
                "   void foo() {}"
                + "}";
        CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver())).parse(code);
        ResolvedMethodDeclaration expected = cu.findAll(MethodDeclaration.class).get(0).resolve();

        List<ResolvedMethodDeclaration> candidateSolvedMethods = new ArrayList<>(Arrays.asList(expected));
        SymbolReference<ResolvedMethodDeclaration> applicableMethods = findMostApplicable(candidateSolvedMethods, "foo",
                Collections.emptyList(), new ReflectionTypeSolver(), false);

        ResolvedMethodDeclaration actual = applicableMethods.getCorrespondingDeclaration();

        assertEquals(expected, actual);
    }
}
