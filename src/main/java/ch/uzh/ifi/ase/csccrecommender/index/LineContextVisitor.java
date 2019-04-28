package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.codeelements.IParameterName;
import cc.kave.commons.model.ssts.IReference;
import cc.kave.commons.model.ssts.blocks.IForEachLoop;
import cc.kave.commons.model.ssts.blocks.IUncheckedBlock;
import cc.kave.commons.model.ssts.blocks.IUnsafeBlock;
import cc.kave.commons.model.ssts.blocks.IUsingBlock;
import cc.kave.commons.model.ssts.expressions.ISimpleExpression;
import cc.kave.commons.model.ssts.expressions.assignable.*;
import cc.kave.commons.model.ssts.expressions.simple.INullExpression;
import cc.kave.commons.model.ssts.expressions.simple.IReferenceExpression;
import cc.kave.commons.model.ssts.impl.visitor.AbstractTraversingNodeVisitor;
import cc.kave.commons.model.ssts.references.*;
import cc.kave.commons.model.ssts.statements.*;

@SuppressWarnings({"squid:S1185", "squid:S1135", "squid:CommentedOutCodeLine"})
public class LineContextVisitor extends AbstractTraversingNodeVisitor<LineContext, Void> {

  @Override
  public Void visit(IVariableDeclaration stmt, LineContext context) {
    context.addToken(stmt.getType().getName());
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IAssignment stmt, LineContext context) {
    stmt.getReference().accept(this, context);
    // context.addToken(" = "); // TODO: Include this as a token?
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
    context.addToken(block.getDeclaration().getType().getName());
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

  // TODO: What is this actually...?
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

    if (methodName.isConstructor()) {
      context.addToken("new");
      context.addToken(methodName.getDeclaringType().getName());
      context.addInvocation(expr.getMethodName().getDeclaringType().getFullName(), methodName.getDeclaringType().getName());
    } else {
      if (methodName.isStatic()) {
        context.addToken(methodName.getDeclaringType().getName());
      } else {
        expr.getReference().accept(this, context);
      }
      context.addInvocation(expr.getMethodName().getDeclaringType().getFullName(), methodName.getName());
      context.addToken(methodName.getName());
    }

    for (ISimpleExpression parameter : expr.getParameters()) {
      parameter.accept(this, context);
    }

    return null;
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
  public Void visit(INullExpression expr, LineContext context) {
    return null;
  }

  @Override
  public Void visit(IReferenceExpression expr, LineContext context) {
    expr.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IEventReference eventRef, LineContext context) {
    eventRef.getReference().accept(this, context);
    context.addToken(eventRef.getEventName().getName()); // TODO: should this be included?
    return null;
  }

  @Override
  public Void visit(IMethodReference methodRef, LineContext context) {
    context.addInvocation(methodRef.getMethodName().getDeclaringType().getFullName(), methodRef.getMethodName().getName());
    context.addToken(methodRef.getMethodName().getName());
    return null;
  }

  @Override
  public Void visit(IPropertyReference propertyRef, LineContext context) {
    context.addToken(propertyRef.getPropertyName().getName());
    return null;
  }

  @Override
  public Void visit(IVariableReference varRef, LineContext context) {
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
      context.addToken(" as ");
      context.addToken(expr.getTargetType().getName());
    } else {
      context.addToken(expr.getTargetType().getName());
      expr.getReference().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(ITypeCheckExpression expr, LineContext context) {
    expr.getReference().accept(this, context);
    context.addToken(" instanceof ");
    context.addToken(expr.getType().getName());
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
        context.addToken(" && ");
        break;
      case BitwiseAnd:
        context.addToken(" & ");
        break;
      case BitwiseOr:
        context.addToken(" | ");
        break;
      case BitwiseXor:
        context.addToken(" ^ ");
        break;
      case Divide:
        context.addToken(" / ");
        break;
      case Equal:
        context.addToken(" == ");
        break;
      case GreaterThan:
        context.addToken(" > ");
        break;
      case GreaterThanOrEqual:
        context.addToken(" >= ");
        break;
      case LessThan:
        context.addToken(" < ");
        break;
      case LessThanOrEqual:
        context.addToken(" <= ");
        break;
      case Minus:
        context.addToken(" - ");
        break;
      case Modulo:
        context.addToken(" % ");
        break;
      case Multiply:
        context.addToken(" * ");
        break;
      case NotEqual:
        context.addToken(" != ");
        break;
      case Or:
        context.addToken(" || ");
        break;
      case Plus:
        context.addToken(" + ");
        break;
      case ShiftLeft:
        context.addToken(" << ");
        break;
      case ShiftRight:
        context.addToken(" >> ");
        break;
      default:
        context.addToken(" ?? ");
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
        context.addToken(" += ");
        break;
      case Remove:
        context.addToken(" -= ");
        break;
      default:
        context.addToken(" ?? ");
    }
    stmt.getExpression().accept(this, context);
    return null;
  }
}
