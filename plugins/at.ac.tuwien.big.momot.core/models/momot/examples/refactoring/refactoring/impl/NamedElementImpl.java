/**
 */
package momot.examples.refactoring.refactoring.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import momot.examples.refactoring.refactoring.NamedElement;
import momot.examples.refactoring.refactoring.RefactoringPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Named Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link momot.examples.refactoring.refactoring.impl.NamedElementImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NamedElementImpl extends MinimalEObjectImpl.Container implements NamedElement {
   /**
    * The default value of the '{@link #getName() <em>Name</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getName()
    * @generated
    * @ordered
    */
   protected static final String NAME_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getName()
    * @generated
    * @ordered
    */
   protected String name = NAME_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected NamedElementImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
      switch(featureID) {
         case RefactoringPackage.NAMED_ELEMENT__NAME:
            return getName();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public boolean eIsSet(final int featureID) {
      switch(featureID) {
         case RefactoringPackage.NAMED_ELEMENT__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eSet(final int featureID, final Object newValue) {
      switch(featureID) {
         case RefactoringPackage.NAMED_ELEMENT__NAME:
            setName((String) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return RefactoringPackage.Literals.NAMED_ELEMENT;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eUnset(final int featureID) {
      switch(featureID) {
         case RefactoringPackage.NAMED_ELEMENT__NAME:
            setName(NAME_EDEFAULT);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setName(final String newName) {
      final String oldName = name;
      name = newName;
      if(eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, RefactoringPackage.NAMED_ELEMENT__NAME, oldName, name));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String toString() {
      if(eIsProxy()) {
         return super.toString();
      }

      final StringBuffer result = new StringBuffer(super.toString());
      result.append(" (name: ");
      result.append(name);
      result.append(')');
      return result.toString();
   }

} // NamedElementImpl
