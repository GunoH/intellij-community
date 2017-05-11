/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.codeInspection.assignment;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.types.GrCodeReferenceElement;
import org.jetbrains.plugins.groovy.lang.psi.util.PsiUtil;

public class GrNewExpressionInfo extends ConstructorCallInfoBase<GrNewExpression> {

  public GrNewExpressionInfo(GrNewExpression expr) {
    super(expr);
  }

  @Nullable
  @Override
  protected PsiType[] inferArgTypes() {
    return PsiUtil.getArgumentTypes(getCall().getReferenceElement(), true);
  }

  @Nullable
  @Override
  public GrExpression getInvokedExpression() {
    return null;
  }

  @Nullable
  @Override
  public PsiType getQualifierInstanceType() {
    return null;
  }

  @NotNull
  @Override
  public PsiElement getHighlightElementForCategoryQualifier() {
    throw new UnsupportedOperationException("no categories are applicable to new expression");
  }

  @NotNull
  @Override
  public PsiElement getElementToHighlight() {
    GrNewExpression call = getCall();

    GrArgumentList argList = call.getArgumentList();
    if (argList != null) return argList;

    GrCodeReferenceElement ref = call.getReferenceElement();
    if (ref != null) return ref;

    throw new IncorrectOperationException("reference of new expression should exist if it is a constructor call");
  }
}
