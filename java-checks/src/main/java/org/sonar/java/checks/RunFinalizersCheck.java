/*
 * SonarQube Java
 * Copyright (C) 2012-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks;

import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.java.JavaVersionAwareVisitor;
import org.sonar.java.checks.methods.AbstractMethodDetection;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.model.ExpressionUtils;
import org.sonar.plugins.java.api.JavaVersion;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;

@Rule(key = "S2151")
public class RunFinalizersCheck extends AbstractMethodDetection implements JavaVersionAwareVisitor {

  @Override
  protected List<MethodMatcher> getMethodInvocationMatchers() {
    return Arrays.asList(
      MethodMatcher.create().typeDefinition("java.lang.Runtime").name("runFinalizersOnExit").addParameter("boolean"),
      MethodMatcher.create().typeDefinition("java.lang.System").name("runFinalizersOnExit").addParameter("boolean"));
  }

  @Override
  protected void onMethodInvocationFound(MethodInvocationTree mit) {
    reportIssue(ExpressionUtils.methodName(mit), "Remove this call to \"" + mit.symbol().owner().name() + ".runFinalizersOnExit()\".");
  }

  @Override
  public boolean isCompatibleWithJavaVersion(JavaVersion version) {
    return version.isNotSet() || version.asInt() < 11;
  }

}
