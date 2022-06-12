/*
 * Copyright (C) 2012-2022 SonarSource SA - mailto:info AT sonarsource DOT com
 * This code is released under [MIT No Attribution](https://opensource.org/licenses/MIT-0) license.
 */
package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.java.model.statement.ForEachStatementImpl;
import org.sonar.java.model.statement.ForStatementTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

@Rule(key = "MyFirstCustomRule")
public class MyFirstCustomCheck extends IssuableSubscriptionVisitor {

  private final BaseTreeVisitor forStatementVisitor = new ForStatementVisitor();

  private final BaseTreeVisitor updateViewStatementVistor = new UpdateViewStatementVisitor();

  /**
   * Unit tests are special methods, so we are just going to visit all of them.
   */
  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Arrays.asList(Tree.Kind.METHOD);
  }

  @Override
  public void visitNode(Tree tree) {
    MethodTree method = (MethodTree) tree;
    BlockTree block = method.block();
    if (block == null) {
      // an abstract test method maybe?
      return;
    }
    block.accept(forStatementVisitor);
  }

  private class ForStatementVisitor extends BaseTreeVisitor {

    @Override
    public void visitForStatement(ForStatementTree tree) {
      StatementTree statement = tree.statement();
      if (statement == null) {
        return;
      }
      System.out.println("for-------------------start----------------------" + ((ForStatementTreeImpl) tree).getLine());
      statement.accept(updateViewStatementVistor);
      System.out.println("for-------------------end----------------------" + ((ForStatementTreeImpl) tree).getLine());
    }

    @Override
    public void visitForEachStatement(ForEachStatement tree) {
      StatementTree statement = tree.statement();
      if (statement == null) {
        return;
      }
      System.out.println("forEach-------------------start----------------------" + ((ForEachStatementImpl) tree).getLine());
      statement.accept(updateViewStatementVistor);
      System.out.println("forEach-------------------end----------------------" + ((ForEachStatementImpl) tree).getLine());
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
      MemberSelectExpressionTree met = (MemberSelectExpressionTree) tree.methodSelect();
      String name = met.identifier().name();
      System.out.println("forMethod-------------------start----------------" + name);
      if (StringUtils.equals(name, "forEach")) {
        tree.arguments().forEach(item -> item.accept(updateViewStatementVistor));
      }
      System.out.println("forMethod-------------------end-----------------" + name);
    }
  }

  private class UpdateViewStatementVisitor extends BaseTreeVisitor {

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
      MemberSelectExpressionTree met = (MemberSelectExpressionTree) tree.methodSelect();
      String name = met.identifier().name();
      System.out.println("inMethod-------------------start----------------" + name);
      if (StringUtils.equals(name, "updateView")) {
        reportIssue(met, "不要在循环中调用 updateView.");
      }
      tree.arguments().forEach(item -> item.accept(updateViewStatementVistor));
      System.out.println("inMethod-------------------end-----------------" + name);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatementTree tree) {
      ExpressionTree exp = tree.expression();
      if (exp instanceof MethodInvocationTree) {
        MethodInvocationTree method = (MethodInvocationTree) exp;
        System.out.println("exp-------------------表达式----------------------" + method.arguments().size());
        MemberSelectExpressionTree met = (MemberSelectExpressionTree) method.methodSelect();
        String name = met.identifier().name();
        System.out.println("outMethod-------------------start----------------" + name);
        if (StringUtils.equals(name, "updateView")) {
          reportIssue(met, "不要在循环中调用 updateView.");
        }
        method.arguments().forEach(item -> item.accept(updateViewStatementVistor));
        System.out.println("outMethod-------------------end-----------------" + name);
      }
    }


  }
}
