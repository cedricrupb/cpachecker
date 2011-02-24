package org.sosy_lab.cpachecker.cfa.ast;

public final class IASTFunctionDefinition extends IASTDeclaration implements
    org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition {

  private final IASTDeclSpecifier      specifier;
  private final IASTFunctionDeclarator declarator;

  public IASTFunctionDefinition(final String pRawSignature,
      final IASTFileLocation pFileLocation, final IASTDeclSpecifier pSpecifier,
      final IASTFunctionDeclarator pDeclarator) {
    super(pRawSignature, pFileLocation);
    specifier = pSpecifier;
    declarator = pDeclarator;
  }

  @Override
  @Deprecated
  public org.eclipse.cdt.core.dom.ast.IASTStatement getBody() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IASTDeclSpecifier getDeclSpecifier() {
    return specifier;
  }

  @Override
  public IASTFunctionDeclarator getDeclarator() {
    return declarator;
  }
  
  @Override
  public IASTNode[] getChildren(){
    return new IASTNode[] {specifier, declarator};
  }

  @Override
  @Deprecated
  public org.eclipse.cdt.core.dom.ast.IScope getScope() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setBody(final org.eclipse.cdt.core.dom.ast.IASTStatement pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setDeclSpecifier(
      final org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setDeclarator(
      final org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IASTFunctionDefinition copy() {
    throw new UnsupportedOperationException();
  }
}
