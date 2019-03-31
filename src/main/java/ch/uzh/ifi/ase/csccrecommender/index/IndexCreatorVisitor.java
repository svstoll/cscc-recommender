package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.ssts.IStatement;
import cc.kave.commons.model.ssts.blocks.*;
import cc.kave.commons.model.ssts.declarations.*;
import cc.kave.commons.model.ssts.expressions.ISimpleExpression;
import cc.kave.commons.model.ssts.expressions.assignable.*;
import cc.kave.commons.model.ssts.expressions.loopheader.ILoopHeaderBlockExpression;
import cc.kave.commons.model.ssts.expressions.simple.IConstantValueExpression;
import cc.kave.commons.model.ssts.expressions.simple.INullExpression;
import cc.kave.commons.model.ssts.expressions.simple.IReferenceExpression;
import cc.kave.commons.model.ssts.expressions.simple.IUnknownExpression;
import cc.kave.commons.model.ssts.impl.visitor.AbstractTraversingNodeVisitor;
import cc.kave.commons.model.ssts.references.*;
import cc.kave.commons.model.ssts.statements.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// TODO svtoll: Implement correct cache maintenance for all visit methods. For example, we need to
//              insert 'if' and 'else' keywords when we visit an 'IIfElseBlock'.

@SuppressWarnings({"squid:S1185", "squid:S1135", "squid:CommentedOutCodeLine"})
public class IndexCreatorVisitor extends AbstractTraversingNodeVisitor<List<Object>, Void> {

  private static Logger logger = LoggerFactory.getLogger(IndexCreatorVisitor.class);

  protected void handleCache(IStatement statement, List<Object> cache) {
    cache.add(statement);
  }
  
  @Override
  public Void visit(IDelegateDeclaration stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IEventDeclaration stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IFieldDeclaration stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IMethodDeclaration decl, List<Object> context) {
    visit(decl.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IPropertyDeclaration decl, List<Object> context) {
    visit(decl.getGet(), context);
    visit(decl.getSet(), context);
    return null;
  }

  @Override
  public Void visit(IVariableDeclaration stmt, List<Object> context) {
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IAssignment stmt, List<Object> context) {
    stmt.getReference().accept(this, context);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IBreakStatement stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IContinueStatement stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IEventSubscriptionStatement stmt, List<Object> context) {
    stmt.getReference().accept(this, context);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IExpressionStatement stmt, List<Object> context) {
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IGotoStatement stmt, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(ILabelledStatement stmt, List<Object> context) {
    stmt.getStatement().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IReturnStatement stmt, List<Object> context) {
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IThrowStatement stmt, List<Object> context) {
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IDoLoop block, List<Object> context) {
    block.getCondition().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IForEachLoop block, List<Object> context) {
    block.getDeclaration().accept(this, context);
    block.getLoopedReference().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IForLoop block, List<Object> context) {
    visit(block.getInit(), context);
    block.getCondition().accept(this, context);
    visit(block.getStep(), context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IIfElseBlock block, List<Object> context) {
    block.getCondition().accept(this, context);
    visit(block.getThen(), context);
    visit(block.getElse(), context);
    return null;
  }

  @Override
  public Void visit(ILockBlock stmt, List<Object> context) {
    stmt.getReference().accept(this, context);
    visit(stmt.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ISwitchBlock block, List<Object> context) {
    block.getReference().accept(this, context);
    for (ICaseBlock cb : block.getSections()) {
      cb.getLabel().accept(this, context);
      visit(cb.getBody(), context);
    }
    visit(block.getDefaultSection(), context);
    return null;
  }

  @Override
  public Void visit(ITryBlock block, List<Object> context) {
    context.add("try");
    visit(block.getBody(), context);
    for (ICatchBlock cb : block.getCatchBlocks()) {
      context.add("catch");
      context.add(cb.getKind().getDeclaringClass().getName());
      visit(cb.getBody(), context);
    }
    if (!block.getFinally().isEmpty()){
      context.add("finally");
    }
    visit(block.getFinally(), context);
    return null;
  }

  @Override
  public Void visit(IUncheckedBlock block, List<Object> context) {
    context.add("unchecked");
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IUnsafeBlock block, List<Object> context) {
    context.add("unsafe");
    // TODO svstoll: why no getBody()?
    return null;
  }

  @Override
  public Void visit(IUsingBlock block, List<Object> context) {
    context.add("using");
    context.add(block.getReference());
//    block.getReference().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IWhileLoop block, List<Object> context) {
    context.add("while");
    context.add(block.getCondition());
    block.getCondition().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ICompletionExpression entity, List<Object> context) {
    // TODO sst: add something to the cache?
    if (entity.getVariableReference() != null) {
      entity.getVariableReference().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IComposedExpression expr, List<Object> context) {
    for (IVariableReference varRef : expr.getReferences()) {
      varRef.accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(IIfElseExpression expr, List<Object> context) {
    expr.getCondition().accept(this, context);
    context.add("if");
    expr.getThenExpression().accept(this, context);
    context.add("else");
    expr.getElseExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IInvocationExpression expr, List<Object> context) {
    context.add(expr.getMethodName());
    expr.getReference().accept(this, context);
    for (ISimpleExpression p : expr.getParameters()) {
      p.accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(ILambdaExpression expr, List<Object> context) {
    context.add(expr.getName().getExplicitMethodName()); // TODO svstoll: is this correct?
    context.add(expr.getName().getReturnType()); // TODO svstoll: is this correct?
    visit(expr.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ILoopHeaderBlockExpression expr, List<Object> context) {
    visit(expr.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IConstantValueExpression expr, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(INullExpression expr, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IReferenceExpression expr, List<Object> context) {
    expr.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(ICastExpression expr, List<Object> context) {
    context.add(expr.getTargetType());
    context.add(expr.getOperator()); // TODO svstoll: what is this in C#?
    expr.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IIndexAccessExpression expr, List<Object> context) {
    expr.getReference().accept(this, context);
    for (ISimpleExpression idx : expr.getIndices()) {
      idx.accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(ITypeCheckExpression expr, List<Object> context) {
    context.add(expr.getType());
    expr.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IBinaryExpression expr, List<Object> context) {
    expr.getLeftOperand().accept(this, context);
    expr.getRightOperand().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IUnaryExpression expr, List<Object> context) {
    expr.getOperand().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IEventReference ref, List<Object> context) {
    ref.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IFieldReference ref, List<Object> context) {
    ref.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IMethodReference ref, List<Object> context) {
    context.add(ref.getMethodName());
    ref.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IPropertyReference ref, List<Object> context) {
    ref.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IVariableReference ref, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IIndexAccessReference ref, List<Object> context) {
    ref.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IUnknownReference ref, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IUnknownExpression unknownExpr, List<Object> context) {
    return null;
  }

  @Override
  public Void visit(IUnknownStatement unknownStmt, List<Object> context) {
    return null;
  }
}

