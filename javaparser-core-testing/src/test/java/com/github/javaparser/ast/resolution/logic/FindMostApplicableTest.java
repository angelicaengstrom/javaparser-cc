package com.github.javaparser.ast.resolution.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.logic.CCHelper;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
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

        private static CCHelper ch;

        @BeforeAll
        public static void setUp() {
                ch = new CCHelper(IntStream.range(1, 24).toArray());
        }

        @AfterAll
        public static void tearDown() {
                ch.printResult("FindMostApplicableTest");
        }

        /**
         * Test case asserting that an {@link UnsolvedSymbolException} is thrown an
         * empty list of candidate methods is passed to the method resolution logic.
         */
        @Test
        public void testNoApplicableMethodsFoundThrowsException() {
                List<ResolvedMethodDeclaration> candidateSolvedMethods = new ArrayList<>();
                List<ResolvedType> argumentsTypes = new ArrayList<>();
                TypeSolver typeSolver = new ReflectionTypeSolver();

                SymbolReference<ResolvedMethodDeclaration> applicableMethod = findMostApplicable(candidateSolvedMethods,
                                "",
                                argumentsTypes, typeSolver, false, ch);

                assertThrows(UnsolvedSymbolException.class, () -> applicableMethod.getCorrespondingDeclaration());
        }

        /**
         * Test case to verify behavior when a single applicable method is passed as an
         * argument.
         */
        @Test
        public void testSingleApplicableMethodFound() {
                String code = "public class Main {\n" +
                                "\n" +
                                "   void foo() {}"
                                + "}";

                CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver()))
                                .parse(code);

                // Retrieving the resolved method declaration from the parsed code
                ResolvedMethodDeclaration expected = cu.findAll(MethodDeclaration.class).get(0).resolve();

                // Creating a list containing only the expected method declaration
                List<ResolvedMethodDeclaration> candidateSolvedMethods = new ArrayList<>(Arrays.asList(expected));

                // Finding the most applicable method
                SymbolReference<ResolvedMethodDeclaration> applicableMethod = findMostApplicable(candidateSolvedMethods,
                                "foo",
                                Collections.emptyList(), new ReflectionTypeSolver(), false, ch);

                // Retrieving the actual resolved method declaration
                ResolvedMethodDeclaration actual = applicableMethod.getCorrespondingDeclaration();

                assertEquals(expected, actual);
        }

        /**
         * Test to verify the return type of an overloaded method with an integer
         * argument.
         */
        @Test
        public void testReturnTypeOfOverloadedMethodWithIntArgument() {
                String code = "public class Main {\n" +
                                "\n" +
                                "   void foo() {}" +
                                "  int foo(int a) {" +
                                " return a;" +
                                "}" +
                                "  boolean foo(boolean b) {" +
                                " return b;" +
                                "}" +
                                "}";

                CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver()))
                                .parse(code);

                List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);

                // Creating a list containing the type of argument expected (in this case, int)
                List<ResolvedType> argumentsTypes = new ArrayList<>(
                                Arrays.asList(ResolvedPrimitiveType.INT));

                // Resolving each method declaration and collecting them into a list
                List<ResolvedMethodDeclaration> candidateSolvedMethods = methodDeclarations.stream()
                                .map(m -> m.resolve())
                                .collect(Collectors.toList());

                SymbolReference<ResolvedMethodDeclaration> applicableMethod = findMostApplicable(candidateSolvedMethods,
                                "foo",
                                argumentsTypes, new ReflectionTypeSolver(), false, ch);

                // Retrieving the actual return type of the resolved method declaration
                ResolvedType actual = applicableMethod.getCorrespondingDeclaration().getReturnType();
                ResolvedType expected = ResolvedPrimitiveType.INT;

                // Asserting that the expected return type matches the actual return type
                assertEquals(expected, actual);
        }

        /**
         * Tests that attempting to match a method by name with incorrect argument types
         * throws an {@link UnsolvedSymbolException} when an attempt is made to get the
         * corresponding declaration.
         * This test case ensures that the method resolution process correctly
         * identifies when no available method matches
         * the given method name and argument types combination, and throws an exception
         * to signal the unresolved method.
         */
        @Test
        public void testMatchingMethodNameWithWrongArgumentThrowsException() {
                String code = "public class Main {\n" +
                                "\n" +
                                "   void foo() {}" +
                                "}";
                CompilationUnit cu = JavaParserAdapter.of(createParserWithResolver(new ReflectionTypeSolver()))
                                .parse(code);

                List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class); // Add methods to the
                                                                                                  // list
                List<ResolvedMethodDeclaration> candidateSolvedMethods = methodDeclarations.stream()
                                .map(m -> m.resolve())
                                .collect(Collectors.toList());

                List<ResolvedType> argumentsTypes = new ArrayList<>(
                                Arrays.asList(ResolvedPrimitiveType.INT));

                SymbolReference<ResolvedMethodDeclaration> applicableMethod = findMostApplicable(candidateSolvedMethods,
                                "foo",
                                argumentsTypes,
                                new ReflectionTypeSolver(), false, ch);

                assertThrows(UnsolvedSymbolException.class, () -> applicableMethod.getCorrespondingDeclaration());
        }

}
