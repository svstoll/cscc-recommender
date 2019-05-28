package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.ssts.blocks.*;
import cc.kave.commons.model.ssts.declarations.*;
import cc.kave.commons.model.ssts.expressions.assignable.ICompletionExpression;
import cc.kave.commons.model.ssts.expressions.assignable.ILambdaExpression;
import cc.kave.commons.model.ssts.expressions.loopheader.ILoopHeaderBlockExpression;
import cc.kave.commons.model.ssts.impl.visitor.AbstractTraversingNodeVisitor;
import cc.kave.commons.model.ssts.statements.*;
import cc.kave.commons.utils.ssts.SSTPrintingContext;
import cc.kave.commons.utils.ssts.SSTPrintingVisitor;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"squid:S1185"})
@Singleton
public class CsccContextVisitor extends AbstractTraversingNodeVisitor<CsccContext, Void> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsccContextVisitor.class);
  @Override
  public Void visit(IDelegateDeclaration stmt, CsccContext context) {
    return null;
  }

  @Override
  public Void visit(IEventDeclaration stmt, CsccContext context) {
    return null;
  }

  @Override
  public Void visit(IFieldDeclaration stmt, CsccContext context) {
    return null;
  }

  @Override
  public Void visit(IMethodDeclaration decl, CsccContext context) {
    context.clear();
    context.setCurrentMethodName(decl.getName());
    context.setCurrentlyWithinExtensionMethod(decl.getName().isExtensionMethod());

    if (LOGGER.isDebugEnabled()) {
      SSTPrintingContext printingContext = new SSTPrintingContext();
      decl.accept(new SSTPrintingVisitor(), printingContext);
      LOGGER.debug("Method currently traversed:\n{}", printingContext);
    }

    visit(decl.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IPropertyDeclaration decl, CsccContext context) {
    context.clear();
    visit(decl.getGet(), context);
    context.clear();
    visit(decl.getSet(), context);
    return null;
  }

  @Override
  public Void visit(IVariableDeclaration stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IAssignment stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getReference().accept(this, context);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IBreakStatement stmt, CsccContext context) {
    context.addLineContext(null, "break");
    return null;
  }

  @Override
  public Void visit(IContinueStatement stmt, CsccContext context) {
    context.addLineContext(null, "continue");
    return null;
  }

  @Override
  public Void visit(IEventSubscriptionStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getReference().accept(this, context);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IExpressionStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IGotoStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    return null;
  }

  @Override
  public Void visit(ILabelledStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getStatement().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IReturnStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getExpression().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IThrowStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    stmt.getReference().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IDoLoop block, CsccContext context) {
    context.addLineContext(null, "do");
    visit(block.getBody(), context);
    context.addLineContext(block.getCondition(), "while");
    block.getCondition().accept(this, context);
    return null;
  }

  @Override
  public Void visit(IForEachLoop block, CsccContext context) {
    context.addLineContext(block);
    block.getDeclaration().accept(this, context);
    block.getLoopedReference().accept(this, context);

    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IForLoop block, CsccContext context) {
    context.addLineContext(block);
    visit(block.getInit(), context);
    block.getCondition().accept(this, context);
    visit(block.getStep(), context);

    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IIfElseBlock block, CsccContext context) {
    context.addLineContext(block.getCondition(), "if");
    block.getCondition().accept(this, context);

    visit(block.getThen(), context);
    visit(block.getElse(), context);
    return null;
  }

  @Override
  public Void visit(ILockBlock stmt, CsccContext context) {
    context.addLineContext(stmt.getReference(), "lock");
    stmt.getReference().accept(this, context);

    visit(stmt.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ISwitchBlock block, CsccContext context) {
    context.addLineContext(block.getReference(), "switch");
    block.getReference().accept(this, context);

    for (ICaseBlock caseBlock : block.getSections()) {
      context.addLineContext(caseBlock.getLabel(), "case");
      caseBlock.getLabel().accept(this, context);
      visit(caseBlock.getBody(), context);
    }

    if (!block.getDefaultSection().isEmpty()) {
      context.addLineContext(null, "default");
      visit(block.getDefaultSection(), context);
    }
    return null;
  }

  @Override
  public Void visit(ITryBlock block, CsccContext context) {
    context.addLineContext(null, "try");
    visit(block.getBody(), context);

    for (ICatchBlock catchBlock : block.getCatchBlocks()) {
      if (catchBlock.getKind() == CatchBlockKind.General) {
        context.addLineContext(null, "catch");
      }
      else {
        context.addLineContext(null, "catch", catchBlock.getParameter().getValueType().getName());
      }

      visit(catchBlock.getBody(), context);
    }

    if (!block.getFinally().isEmpty()){
      context.addLineContext(null, "finally");
      visit(block.getFinally(), context);
    }
    return null;
  }

  @Override
  public Void visit(IUncheckedBlock block, CsccContext context) {
    context.addLineContext(null, "unchecked");
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IUnsafeBlock block, CsccContext context) {
    return null;
  }

  @Override
  public Void visit(IUsingBlock block, CsccContext context) {
    context.addLineContext(block.getReference(), "using");
    block.getReference().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IWhileLoop block, CsccContext context) {
    context.addLineContext(block.getCondition(), "while");
    block.getCondition().accept(this, context);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ICompletionExpression entity, CsccContext context) {
    if (entity.getVariableReference() != null) {
      entity.getVariableReference().accept(this, context);
    }
    return null;
  }

  @Override
  public Void visit(ILambdaExpression expr, CsccContext context) {
    context.addLineContext(expr);
    visit(expr.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ILoopHeaderBlockExpression expr, CsccContext context) {
    visit(expr.getBody(), context);
    return null;
  }
}
