package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.*;

/**
 * TODO 说明
 *
 * @author kingdee
 * @since 2022/6/27 10:46
 */
@Rule(key = "CopyRightRule")
public class CopyRightRuleCheck extends IssuableSubscriptionVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Arrays.asList(Tree.Kind.PACKAGE);
  }

  @Override
  public void visitNode(Tree tree) {
    PackageDeclarationTree packageDeclarationTree = (PackageDeclarationTree) tree;
    List<SyntaxTrivia> trivias = packageDeclarationTree.packageKeyword().trivias();
    if(trivias.size() > 0 && trivias.get(0).startLine() == 1){
      if(trivias.get(0).comment().contains("copyright")){
        return;
      }
    }
    reportIssue(tree, "copyright of company is needed");
    super.visitNode(tree);
  }


}
