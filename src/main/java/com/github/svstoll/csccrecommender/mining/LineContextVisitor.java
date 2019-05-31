/*
 *  Copyright 2019 Sven Stoll, Dingguang Jin, Tran Phan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.svstoll.csccrecommender.mining;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.codeelements.IParameterName;
import cc.kave.commons.model.ssts.IReference;
import cc.kave.commons.model.ssts.blocks.IForEachLoop;
import cc.kave.commons.model.ssts.blocks.IUncheckedBlock;
import cc.kave.commons.model.ssts.blocks.IUnsafeBlock;
import cc.kave.commons.model.ssts.blocks.IUsingBlock;
import cc.kave.commons.model.ssts.expressions.ISimpleExpression;
import cc.kave.commons.model.ssts.expressions.assignable.*;
import cc.kave.commons.model.ssts.expressions.simple.IConstantValueExpression;
import cc.kave.commons.model.ssts.expressions.simple.INullExpression;
import cc.kave.commons.model.ssts.expressions.simple.IReferenceExpression;
import cc.kave.commons.model.ssts.impl.visitor.AbstractTraversingNodeVisitor;
import cc.kave.commons.model.ssts.references.*;
import cc.kave.commons.model.ssts.statements.*;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.github.svstoll.csccrecommender.utility.SstUtility.*;
import static com.github.svstoll.csccrecommender.utility.StringUtility.isNullOrEmpty;

@SuppressWarnings({"squid:S1185"})
@Singleton
public class LineContextVisitor extends AbstractTraversingNodeVisitor<LineContext, Void> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LineContextVisitor.class);

  private final Map<String, String> variableDeclarations = new HashMap<>();

  @Override
  public Void visit(ICompletionExpression entity, LineContext context) {
    if (entity.getVariableReference() != null) {
      String type = variableDeclarations.get(entity.getVariableReference().getIdentifier());
      if (isValidToken(type)) {
        handleCompletionExpression(type, context.getCsccContext());
      }
      else {
        LOGGER.warn("Recommendation failed because the type of the variable reference could not " +
            "be resolved for the completion expression.");
      }
    }
    else if (entity.getTypeReference() != null) {
      String type = entity.getTypeReference().getFullName();
      if (isValidToken(type)) {
        handleCompletionExpression(entity.getTypeReference().getFullName(), context.getCsccContext());
      }
      else {
        LOGGER.warn("Recommendation failed because the type of the static reference could not " +
            "be resolved for the completion expression.");
      }
    }
    else if (context.getCsccContext().getCurrentMethodName() != null) {
      String type = context.getCsccContext().getCurrentMethodName().getDeclaringType().getFullName();
      if (isValidToken(type)) {
        handleCompletionExpression(type, context.getCsccContext());
      }
      else {
        LOGGER.warn("Recommendation failed because the declaring type of the method where the " +
            "completion expression occurred could not be resolved.");
      }
    }
    else {
      LOGGER.warn("Recommendation failed because there was neither a static type reference nor " +
          "a variable reference associated with the completion expression and the type of the " +
          "entity declaring the method was also unknown.");
    }
    return null;
  }

  protected void handleCompletionExpression(String invocationType, CsccContext csccContext) {
    // NOP
  }

  @Override
  public Void visit(IVariableDeclaration stmt, LineContext context) {
    variableDeclarations.put(stmt.getReference().getIdentifier(), stmt.getType().getFullName());
    context.addToken(resolveTypeNameToken(stmt.getType()));
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IAssignment stmt, LineContext context) {
    stmt.getReference().accept(this, context);
    context.addToken("=");
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IBreakStatement stmt, LineContext context) {
    context.addToken("break");
    return null;
  }

  @Override
  public Void visit(IContinueStatement stmt, LineContext context) {
    context.addToken("continue");
    return null;
  }

  @Override
  public Void visit(IExpressionStatement stmt, LineContext context) {
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IGotoStatement stmt, LineContext context) {
    context.addToken("goto");
    return null;
  }

  @Override
  public Void visit(ILabelledStatement stmt, LineContext context) {
    stmt.getStatement().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IReturnStatement stmt, LineContext context) {
    context.addToken("return");

    if (!stmt.isVoid()) {
      stmt.getExpression().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IThrowStatement stmt, LineContext context) {
    context.addToken("throw");
    if (!stmt.isReThrow()) {
      context.addToken("new");
      stmt.getReference().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IForEachLoop block, LineContext context) {
    context.addToken("foreach");
    context.addToken(resolveTypeNameToken(block.getDeclaration().getType()));
    block.getDeclaration().getReference().accept(this, context);
    context.addToken("in");
    block.getLoopedReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IUncheckedBlock block, LineContext context) {
    context.addToken("unchecked");
    return null;
  }

  @Override
  public Void visit(IUnsafeBlock block, LineContext context) {
    context.addToken("unsafe");
    return null;
  }

  @Override
  public Void visit(IUsingBlock block, LineContext context) {
    context.addToken("using");
    block.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IComposedExpression expr, LineContext context) {
    context.addToken("composed");
    for (IReference reference : expr.getReferences()) {
      reference.accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IIfElseExpression expr, LineContext context) {
    expr.getCondition().accept(this, context);
    context.addToken("?");
    expr.getThenExpression().accept(this, context);
    context.addToken(":");
    expr.getElseExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IInvocationExpression expr, LineContext context) {
    IMethodName methodName = expr.getMethodName();
    String type = methodName.getDeclaringType().getFullName();

    if (methodName.isConstructor()) {
      context.addToken("new");
      handleMethodInvocation(methodName.getName(), type, context.getCsccContext());
      context.addToken(methodName.getDeclaringType().getName());
    } else {
      if (methodName.isStatic()) {
        context.addToken(methodName.getDeclaringType().getName());
      } else {
        expr.getReference().accept(this, context);
      }

      handleMethodInvocation(methodName.getName(), type, context.getCsccContext());
      context.addToken(methodName.getName());
    }

    for (ISimpleExpression parameter : expr.getParameters()) {
      parameter.accept(this, context);
    }

    return null;
  }

  protected void handleMethodInvocation(String methodCall, String invocationType, CsccContext csccContext) {
    // NOP
  }

  @Override
  public Void visit(ILambdaExpression expr, LineContext context) {
    for (IParameterName parameterName : expr.getName().getParameters()) {
      if (parameterName.getValueType() != null) {
        context.addToken(parameterName.getValueType().getName());
      }
    }
    context.addToken("=>");
    return null;
  }

  @Override
  public Void visit(IConstantValueExpression expr, LineContext context) {
    String value = expr.getValue();
    if (!isNullOrEmpty(value) && (value.equals("false") || value.equals("true"))) {
      context.addToken(value);
    }
    return null;
  }

  @Override
  public Void visit(INullExpression expr, LineContext context) {
    context.addToken("null");
    return null;
  }

  @Override
  public Void visit(IReferenceExpression expr, LineContext context) {
    expr.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IEventReference eventRef, LineContext context) {
    if (isSelfReferenceToken(eventRef.getReference().getIdentifier())) {
      context.addToken(eventRef.getReference().getIdentifier());
    }
    context.addToken(eventRef.getEventName().getName());
    return null;
  }

  @Override
  public Void visit(IMethodReference methodRef, LineContext context) {
    if (isSelfReferenceToken(methodRef.getReference().getIdentifier())) {
      context.addToken(methodRef.getReference().getIdentifier());
    }

    String methodCall = methodRef.getMethodName().getName();
    String type = methodRef.getMethodName().getDeclaringType().getFullName();
    handleMethodInvocation(methodCall, type, context.getCsccContext());

    context.addToken(methodRef.getMethodName().getName());
    return null;
  }

  @Override
  public Void visit(IFieldReference fieldRef, LineContext context) {
    if (isSelfReferenceToken(fieldRef.getReference().getIdentifier())) {
      context.addToken(fieldRef.getReference().getIdentifier());
    }
    return null;
  }

  @Override
  public Void visit(IPropertyReference propertyRef, LineContext context) {
    if (isSelfReferenceToken(propertyRef.getReference().getIdentifier())) {
      context.addToken(propertyRef.getReference().getIdentifier());
    }
    return null;
  }

  @Override
  public Void visit(IVariableReference varRef, LineContext context) {
    if (isSelfReferenceToken(varRef.getIdentifier())) {
      context.addToken(varRef.getIdentifier());
    }
    return null;
  }

  @Override
  public Void visit(IIndexAccessReference indexAccessRef, LineContext context) {
    indexAccessRef.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(ICastExpression expr, LineContext context) {
    if (expr.getOperator() == CastOperator.SafeCast) {
      expr.getReference().accept(this, context);
      context.addToken("as");
      context.addToken(resolveTypeNameToken(expr.getTargetType()));
    } else {
      context.addToken(resolveTypeNameToken(expr.getTargetType()));
      expr.getReference().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(ITypeCheckExpression expr, LineContext context) {
    expr.getReference().accept(this, context);
    context.addToken("instanceof");
    context.addToken(resolveTypeNameToken(expr.getType()));
    return null;
  }

  @Override
  public Void visit(IIndexAccessExpression expr, LineContext context) {
    expr.getReference().accept(this, context);
    for (int i = 0; i < expr.getIndices().size(); i++) {
      expr.getIndices().get(i).accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IUnaryExpression expr, LineContext context) {
    switch (expr.getOperator()) {
      case Not:
        context.addToken("!");
        expr.getOperand().accept(this, context);
        break;
      case PreIncrement:
        context.addToken("++");
        expr.getOperand().accept(this, context);
        break;
      case PostIncrement:
        expr.getOperand().accept(this, context);
        context.addToken("++");
        break;
      case PreDecrement:
        context.addToken("--");
        expr.getOperand().accept(this, context);
        break;
      case PostDecrement:
        expr.getOperand().accept(this, context);
        context.addToken("--");
        break;
      case Plus:
        context.addToken("+");
        expr.getOperand().accept(this, context);
        break;
      case Minus:
        context.addToken("-");
        expr.getOperand().accept(this, context);
        break;
      case Complement:
        context.addToken("~");
        expr.getOperand().accept(this, context);
        break;
      default:
        context.addToken("?");
        expr.getOperand().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IBinaryExpression expr, LineContext context) {
    expr.getLeftOperand().accept(this, context);
    switch (expr.getOperator()) {
      case And:
        context.addToken("&&");
        break;
      case BitwiseAnd:
        context.addToken("&");
        break;
      case BitwiseOr:
        context.addToken("|");
        break;
      case BitwiseXor:
        context.addToken("^");
        break;
      case Divide:
        context.addToken("/");
        break;
      case Equal:
        context.addToken("==");
        break;
      case GreaterThan:
        context.addToken(">");
        break;
      case GreaterThanOrEqual:
        context.addToken(">=");
        break;
      case LessThan:
        context.addToken("<");
        break;
      case LessThanOrEqual:
        context.addToken("<=");
        break;
      case Minus:
        context.addToken("-");
        break;
      case Modulo:
        context.addToken("%");
        break;
      case Multiply:
        context.addToken("*");
        break;
      case NotEqual:
        context.addToken("!=");
        break;
      case Or:
        context.addToken("||");
        break;
      case Plus:
        context.addToken("+");
        break;
      case ShiftLeft:
        context.addToken("<<");
        break;
      case ShiftRight:
        context.addToken(">>");
        break;
      default:
        context.addToken("??");
        break;
    }
    expr.getRightOperand().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IEventSubscriptionStatement stmt, LineContext context) {
    stmt.getReference().accept(this, context);
    switch (stmt.getOperation()) {
      case Add:
        context.addToken("+=");
        break;
      case Remove:
        context.addToken("-=");
        break;
      default:
        context.addToken("??");
    }
    stmt.getExpression().accept(this, context);
    return null;
  }
}
