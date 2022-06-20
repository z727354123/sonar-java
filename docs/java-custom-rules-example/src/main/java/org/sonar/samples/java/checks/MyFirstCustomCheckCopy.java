/*
 * Copyright (C) 2012-2022 SonarSource SA - mailto:info AT sonarsource DOT com
 * This code is released under [MIT No Attribution](https://opensource.org/licenses/MIT-0) license.
 */
package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.samples.java.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Rule(key = "MyFirstCustomRule")
public class MyFirstCustomCheckCopy extends IssuableSubscriptionVisitor {

  private final BaseTreeVisitor blockStatementVisitor = new BlockStatementVisitor();
  private final BaseTreeVisitor expressionStatementVisitor = new ExpressionStatementVisitor();


  /**
   * Unit tests are special methods, so we are just going to visit all of them.
   */
  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Arrays.asList(Tree.Kind.METHOD);
  }

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;
    super.scanFile(context);
  }

  @Override
  public void visitNode(Tree tree) {
    MethodTree methodTree = (MethodTree) tree;
    List<StatementTree> body = Objects.requireNonNull(methodTree.block()).body();
    for (StatementTree statementTree : body) {
      if ((statementTree instanceof ForEachStatement) || (statementTree instanceof ForStatementTree)) {
        statementTree.accept(blockStatementVisitor);
      }
      if (statementTree instanceof ExpressionStatementTree) {
        ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statementTree;
        if (expressionStatementTree.expression().is(Tree.Kind.METHOD_INVOCATION)) {
          MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionStatementTree.expression();
          MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) methodInvocationTree.methodSelect();
          String name = memberSelectExpressionTree.identifier().name();
          if (StringUtils.equals("forEach", name) || StringUtils.equals("map", name)) {
            methodInvocationTree.accept(blockStatementVisitor);
          }
        }
      }
    }
  }

  public void expressionStatement(MethodInvocationTree methodInvocationTree) {
    MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) methodInvocationTree.methodSelect();
    String name = memberSelectExpressionTree.identifier().name();
    if (StringUtils.equals("updateView", name)) {
      reportIssue(methodInvocationTree, "the updateView is not allowed to be a block in circulate '");
    } else {
      Arguments arguments = methodInvocationTree.arguments();
      if (arguments.size() != 0) {
        arguments.forEach(
          item -> {
            if(!item.is(Tree.Kind.LAMBDA_EXPRESSION)){
              item.accept(expressionStatementVisitor);
            }
          }
        );
      }
    }
  }

  private class BlockStatementVisitor extends BaseTreeVisitor {
    @Override
    public void visitBlock(BlockTree tree) {
      List<StatementTree> body = tree.body();
      for (StatementTree statementTree : body) {
        if (statementTree instanceof ExpressionStatementTree) {
          ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statementTree;
          if (expressionStatementTree.expression().is(Tree.Kind.METHOD_INVOCATION)) {
            expressionStatement((MethodInvocationTree) expressionStatementTree.expression());
          }
        }
      }
      super.visitBlock(tree);
    }
  }

  private class ExpressionStatementVisitor extends BaseTreeVisitor {
    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
      expressionStatement(tree);
      super.visitMethodInvocation(tree);
    }
  }
}
