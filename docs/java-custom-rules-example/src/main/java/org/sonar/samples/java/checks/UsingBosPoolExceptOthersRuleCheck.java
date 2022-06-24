/*
 * Copyright (C) 2012-2022 SonarSource SA - mailto:info AT sonarsource DOT com
 * This code is released under [MIT No Attribution](https://opensource.org/licenses/MIT-0) license.
 */
package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.*;

import java.util.Arrays;
import java.util.List;

@Rule(key = "UsingBosPoolExceptOthersRule")
public class UsingBosPoolExceptOthersRuleCheck extends IssuableSubscriptionVisitor {

  /**
   * 线程执行方法
   */
  private final List<String> methodList = Arrays.asList("start", "execute", "submit");

  /**
   * Unit tests are special methods, so we are just going to visit all of them.
   */
  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Arrays.asList(Tree.Kind.METHOD_INVOCATION);
  }

  @Override
  public void visitNode(Tree tree) {
      MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
      MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) methodInvocationTree.methodSelect();
      String name = memberSelectExpressionTree.identifier().name();
      if(methodList.contains(name)){
        String fullyQualifiedName = memberSelectExpressionTree.expression().symbolType().fullyQualifiedName();
        if(checkClass(fullyQualifiedName)){
          reportIssue(memberSelectExpressionTree, "the kd.bos.threads.ThreadPools must to be used");
        } else if(fullyQualifiedName.contains("$")){
          Type type = memberSelectExpressionTree.expression().symbolType().symbol().superClass();
          if(type != null && (checkClass(type.fullyQualifiedName()))){
            reportIssue(memberSelectExpressionTree, "the kd.bos.threads.ThreadPools must to be used");
          }
        }
      }
  }

  private Boolean checkClass(String className){
    return className.contains("java.util.concurrent") || className.contains("java.lang.Thread");
  }
}
