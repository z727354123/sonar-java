package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO 说明
 *
 * @author kingdee
 * @since 2022/6/27 10:46
 */
@Rule(key = "KingdeeTipsRule")
public class KingdeeTipsRuleCheck extends IssuableSubscriptionVisitor {

  private final BaseTreeVisitor literalStatementVisitor = new LiteralStatementVisitor();

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Arrays.asList(Tree.Kind.METHOD_INVOCATION);
  }

  @Override
  public void visitNode(Tree tree) {
    MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
    MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) methodInvocationTree.methodSelect();
    String name = memberSelectExpressionTree.identifier().name();
    if("loadKDString".equals(name)){
      methodInvocationTree.arguments().get(0).accept(literalStatementVisitor);
      if(Objects.requireNonNull(methodInvocationTree.parent()).is(Tree.Kind.VARIABLE)){
        VariableTree variableTree = (VariableTree) methodInvocationTree.parent();
        Objects.requireNonNull(variableTree).modifiers().forEach(
          modifierTree -> {
            ModifierKeywordTree modifierKeywordTree = (ModifierKeywordTree) modifierTree;
            if(modifierKeywordTree.keyword().text().equals("static")){
              reportIssue(variableTree, "using kingdee tips rules to deal with the problem:https://developer.kingdee.com/article/241181198498975488?productLineId=29&isKnowledge=2");
            }
          }
        );
      }
    }
    super.visitNode(tree);
  }

  private class LiteralStatementVisitor extends BaseTreeVisitor {

    @Override
    public void visitLiteral(LiteralTree tree) {
      String tip = tree.value().replace("\"", "");
      if(tip.startsWith(" ") || tip.endsWith(" ") || checkTip(tip)){
        reportIssue(tree, "using kingdee tips rules to deal with the problem:https://developer.kingdee.com/article/241181198498975488?productLineId=29&isKnowledge=2");
      }
      super.visitLiteral(tree);
    }

    /**
     * 提示语是否满足规范
     *
     * @param tip 提示
     * @return 布尔值：是否正确
     */
    private boolean checkTip(String tip){
      String tipChange = tip.replace("$s", "s");
      String replace = tipChange.replaceAll("%[0-9]*s", "");
      return checkSuitableChar(tip) || checkIllegalChar(replace);
    }

    /**
     * 是否带有非法字符
     *
     * @param tip 提示
     * @return 布尔值：是否正确
     */
    private boolean checkIllegalChar(String tip){
      String regex1 = "<|>";
      String regex2 = ".+\\\\.+";
      return tip.matches(regex1) || tip.matches(regex2);
    }

    /**
     * 是否存在使用多个%s的情况
     *
     * @param tip 提示
     * @return 布尔值：是否正确
     */
    private boolean checkSuitableChar(String tip){
      int count = 0;
      Pattern p = Pattern.compile("%s");
      Matcher m = p.matcher(tip);
      while (m.find()) {
        count++;
      }
      return count > 1;
    }

  }
}
