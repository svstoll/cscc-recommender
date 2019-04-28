package ch.uzh.ifi.ase.csccrecommender.index;

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

@SuppressWarnings({"squid:S1185", "squid:S1135", "squid:CommentedOutCodeLine"})
public class IndexCreatorVisitor extends AbstractTraversingNodeVisitor<CsccContext, Void> {

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
    return null;
  }

  @Override
  public Void visit(IAssignment stmt, CsccContext context) {
    context.addLineContext(stmt);
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
    return null;
  }

  @Override
  public Void visit(IExpressionStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
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
    return null;
  }

  @Override
  public Void visit(IReturnStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    return null;
  }

  @Override
  public Void visit(IThrowStatement stmt, CsccContext context) {
    context.addLineContext(stmt);
    return null;
  }

  @Override
  public Void visit(IDoLoop block, CsccContext context) {
    context.addLineContext(null, "do");
    visit(block.getBody(), context);
    context.addLineContext(block.getCondition(), "while");
    return null;
  }

  @Override
  public Void visit(IForEachLoop block, CsccContext context) {
    context.addLineContext(block);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IForLoop block, CsccContext context) {
    context.addLineContext(block);
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IIfElseBlock block, CsccContext context) {
    context.addLineContext(block.getCondition(), "if");
    visit(block.getThen(), context);
    visit(block.getElse(), context);
    return null;
  }

  @Override
  public Void visit(ILockBlock stmt, CsccContext context) {
    context.addLineContext(stmt.getReference(), "lock");
    visit(stmt.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ISwitchBlock block, CsccContext context) {
    context.addLineContext(block.getReference(), "switch");

    for (ICaseBlock caseBlock : block.getSections()) {
      context.addLineContext(caseBlock.getLabel(), "case");
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
    // TODO: Why has this block no body?
    return null;
  }

  @Override
  public Void visit(IUsingBlock block, CsccContext context) {
    context.addLineContext(block.getReference(), "using");
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(IWhileLoop block, CsccContext context) {
    context.addLineContext(block.getCondition(), "while");
    visit(block.getBody(), context);
    return null;
  }

  @Override
  public Void visit(ICompletionExpression entity, CsccContext context) {
    // TODO sst: Are these completion expressions present in the normal data set?
    throw new IllegalStateException();
  }

  @Override
  public Void visit(IComposedExpression expr, CsccContext context) {
    throw new IllegalStateException();
  }

  @Override
  public Void visit(IIfElseExpression expr, CsccContext context) {
    throw new IllegalStateException();
  }

  @Override
  public Void visit(IInvocationExpression expr, CsccContext context) {
    throw new IllegalStateException();
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

  @Override
  public Void visit(IConstantValueExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(INullExpression expr, CsccContext context) {
    return null;
  }

  @Override
  public Void visit(IReferenceExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(ICastExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IIndexAccessExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(ITypeCheckExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IBinaryExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IUnaryExpression expr, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IEventReference ref, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IFieldReference ref, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IMethodReference ref, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IPropertyReference ref, CsccContext context) {
    throw new IllegalStateException("");
  }

  @Override
  public Void visit(IVariableReference ref, CsccContext context) {
    throw new IllegalStateException("No variable reference allowed here");
  }

  @Override
  public Void visit(IIndexAccessReference ref, CsccContext context) {
    throw new IllegalStateException("No index access reference allowed here");
  }

  @Override
  public Void visit(IUnknownReference ref, CsccContext context) {
    throw new IllegalStateException();
  }

  @Override
  public Void visit(IUnknownExpression unknownExpr, CsccContext context) {
    throw new IllegalStateException();
  }

  @Override
  public Void visit(IUnknownStatement unknownStmt, CsccContext context) {
    return null;
  }
}

