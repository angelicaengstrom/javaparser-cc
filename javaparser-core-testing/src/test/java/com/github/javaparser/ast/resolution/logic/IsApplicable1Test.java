package com.github.javaparser.ast.resolution.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionMethodDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class IsApplicable1Test extends MethodResolutionLogic {
  private static class TestClassIsApplicable1 {
    public int methodNamesNotEqual() {
      return 1;
    }

    public int methodNotVariadicAndParametersNotEqual(int x, int y) {
      return x + y;
    }

    public int methodVariadic(int x, int... y) {
      for(int i : y) {
        x += i;
      }
      return x;
    }
  }

  @Test
  public void testMethodNamesNotEqual() throws NoSuchMethodException {
    Method method = TestClassIsApplicable1.class.getMethod("methodNamesNotEqual");
    TypeSolver typeSolver = new ReflectionTypeSolver();

    ResolvedMethodDeclaration methodDeclaration = new ReflectionMethodDeclaration(method, typeSolver);
    String needleName = "";
    List<ResolvedType> needleArgumentTypes = new ArrayList<>();

    assertFalse(isApplicable(methodDeclaration, needleName, needleArgumentTypes, typeSolver));
  }

  @Test
  public void testMethodNotVariadicAndParametersNotEqual() throws NoSuchMethodException {
    Method method = TestClassIsApplicable1.class.getMethod("methodNotVariadicAndParametersNotEqual", int.class, int.class);
    TypeSolver typeSolver = new ReflectionTypeSolver();

    ResolvedMethodDeclaration methodDeclaration = new ReflectionMethodDeclaration(method, typeSolver);
    String needleName = "methodNotVariadicAndParametersNotEqual";
    List<ResolvedType> needleArgumentTypes = new ArrayList<>();

    assertFalse(isApplicable(methodDeclaration, needleName, needleArgumentTypes, typeSolver));
  }

  @Test
  public void testMethodVariadicAndArgumentsAreShortByTwo() throws NoSuchMethodException {
    Method method = TestClassIsApplicable1.class.getMethod("methodVariadic", int.class, int[].class);
    TypeSolver typeSolver = new ReflectionTypeSolver();

    ResolvedMethodDeclaration methodDeclaration = new ReflectionMethodDeclaration(method, typeSolver);
    String needleName = "methodVariadic";
    List<ResolvedType> needleArgumentTypes = new ArrayList<>();

    assertFalse(isApplicable(methodDeclaration, needleName, needleArgumentTypes, typeSolver));
  }

  @Test
  public void testMethodVariadicAndArgumentsAreNotAssignable() throws NoSuchMethodException {
    Method method = TestClassIsApplicable1.class.getMethod("methodVariadic", int.class, int[].class);
    TypeSolver typeSolver = new ReflectionTypeSolver();

    ResolvedMethodDeclaration methodDeclaration = new ReflectionMethodDeclaration(method, typeSolver);
    String needleName = "methodVariadic";
    List<ResolvedType> needleArgumentTypes = new ArrayList<>();
    needleArgumentTypes.add(ResolvedPrimitiveType.INT);
    needleArgumentTypes.add(ResolvedPrimitiveType.INT);
    needleArgumentTypes.add(ResolvedPrimitiveType.DOUBLE);

    assertFalse(isApplicable(methodDeclaration, needleName, needleArgumentTypes, typeSolver));
  }
}
