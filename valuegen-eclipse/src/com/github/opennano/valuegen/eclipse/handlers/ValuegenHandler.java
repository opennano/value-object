package com.github.opennano.valuegen.eclipse.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

/**
 * <b>Warning</b> : As explained in <a
 * href="http://wiki.eclipse.org/Eclipse4/RCP/FAQ#Why_aren.27t_my_handler_fields_being_re-injected.3F">this
 * wiki page</a>, it is not recommended to define @Inject fields in a handler. <br>
 * <br>
 * <b>Inject the values in the @Execute methods</b>
 */
public class ValuegenHandler {

  @Execute
  public void execute(
      @Named(IServiceConstants.ACTIVE_SELECTION) IStructuredSelection selection,
      @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

    ICompilationUnit selectedClass = (ICompilationUnit) selection.getFirstElement();
    selectedClass.accept(new ASTVisitor() {
		public boolean visit(FieldDeclaration node) {
			field = node;
			return true;
		}
	});
    
    
    IType primaryType;
    try {
      primaryType = selectedClass.getAllTypes()[0];
      IField[] fields = primaryType.getFields();
      SourceField field = (SourceField) fields[0];
      field.
      MessageDialog.openInformation(shell, "Valuegen-eclipse", "Selected=" + fields);
    } catch (JavaModelException e) {
      MessageDialog.openInformation(shell, "Valuegen-eclipse", e.getMessage());
    }
  }
}
