package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.ssts.IStatement;
import cc.kave.commons.model.ssts.blocks.*;
import cc.kave.commons.model.ssts.declarations.*;
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
public class IndexCreatorVisitor extends AbstractTraversingNodeVisitor<List<IStatement>, Void> {

  private static Logger logger = LoggerFactory.getLogger(IndexCreatorVisitor.class);

  protected void handleCache(IStatement statement, List<IStatement> cache) {
    cache.add(statement);
  }

  @Override
  public Void visit(IInvocationExpression expr, List<IStatement> cache) {
    logger.info("Items in cache since last invocation: {}", cache.size());
    cache.clear();
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IMethodDeclaration decl, List<IStatement> cache) {
    logger.info("Invoked method: {}", decl.getName().getName());
    return super.visit(decl, cache);
  }

  @Override
  public Void visit(IDoLoop block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(ILockBlock stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IForLoop block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(ITryBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IAssignment stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IWhileLoop block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IUsingBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IForEachLoop block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IIfElseBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(ISwitchBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IUnsafeBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IEventReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IFieldReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IGotoStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IBreakStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(ICastExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IMethodReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(INullExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IThrowStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IReturnStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IUnaryExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IUncheckedBlock block, List<IStatement> cache) {
    handleCache(block, cache);
    return super.visit(block, cache);
  }

  @Override
  public Void visit(IUnknownReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IBinaryExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IEventDeclaration stmt, List<IStatement> cache) {
    //handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IFieldDeclaration stmt, List<IStatement> cache) {
    //handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IIfElseExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(ILambdaExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IPropertyReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IVariableReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IContinueStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(ILabelledStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IComposedExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IDelegateDeclaration stmt, List<IStatement> cache) {
    //handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IExpressionStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IIndexAccessReference ref, List<IStatement> cache) {
    //handleCache(ref, cache);
    return super.visit(ref, cache);
  }

  @Override
  public Void visit(IPropertyDeclaration decl, List<IStatement> cache) {
    //handleCache(decl, cache);
    return super.visit(decl, cache);
  }

  @Override
  public Void visit(IReferenceExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(ITypeCheckExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IVariableDeclaration stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }

  @Override
  public Void visit(IIndexAccessExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(ICompletionExpression entity, List<IStatement> cache) {
    //handleCache(entity, cache);
    return super.visit(entity, cache);
  }

  @Override
  public Void visit(IConstantValueExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IUnknownStatement unknownStmt, List<IStatement> cache) {
    handleCache(unknownStmt, cache);
    return super.visit(unknownStmt, cache);
  }

  @Override
  public Void visit(IUnknownExpression unknownExpr, List<IStatement> cache) {
    //handleCache(unknownExpr, cache);
    return super.visit(unknownExpr, cache);
  }

  @Override
  public Void visit(ILoopHeaderBlockExpression expr, List<IStatement> cache) {
    //handleCache(expr, cache);
    return super.visit(expr, cache);
  }

  @Override
  public Void visit(IEventSubscriptionStatement stmt, List<IStatement> cache) {
    handleCache(stmt, cache);
    return super.visit(stmt, cache);
  }
}

