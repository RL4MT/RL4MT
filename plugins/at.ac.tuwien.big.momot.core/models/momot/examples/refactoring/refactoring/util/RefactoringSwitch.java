/**
 */
package momot.examples.refactoring.refactoring.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import momot.examples.refactoring.refactoring.Entity;
import momot.examples.refactoring.refactoring.Generalization;
import momot.examples.refactoring.refactoring.NamedElement;
import momot.examples.refactoring.refactoring.Property;
import momot.examples.refactoring.refactoring.RefactoringModel;
import momot.examples.refactoring.refactoring.RefactoringPackage;
import momot.examples.refactoring.refactoring.Type;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * 
 * @see momot.examples.refactoring.refactoring.RefactoringPackage
 * @generated
 */
public class RefactoringSwitch<T> extends Switch<T> {
   /**
    * The cached model package
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected static RefactoringPackage modelPackage;

   /**
    * Creates an instance of the switch.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public RefactoringSwitch() {
      if(modelPackage == null) {
         modelPackage = RefactoringPackage.eINSTANCE;
      }
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Entity</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Entity</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseEntity(final Entity object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Generalization</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Generalization</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseGeneralization(final Generalization object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Named Element</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Named Element</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseNamedElement(final NamedElement object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Property</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Property</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseProperty(final Property object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Model</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseRefactoringModel(final RefactoringModel object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>Type</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>Type</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
    * @generated
    */
   public T caseType(final Type object) {
      return null;
   }

   /**
    * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
    * <!-- begin-user-doc -->
    * This implementation returns null;
    * returning a non-null result will terminate the switch, but this is the last case anyway.
    * <!-- end-user-doc -->
    * 
    * @param object
    *           the target of the switch.
    * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
    * @see #doSwitch(org.eclipse.emf.ecore.EObject)
    * @generated
    */
   @Override
   public T defaultCase(final EObject object) {
      return null;
   }

   /**
    * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @return the first non-null result returned by a <code>caseXXX</code> call.
    * @generated
    */
   @Override
   protected T doSwitch(final int classifierID, final EObject theEObject) {
      switch(classifierID) {
         case RefactoringPackage.PROPERTY: {
            final Property property = (Property) theEObject;
            T result = caseProperty(property);
            if(result == null) {
               result = caseNamedElement(property);
            }
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case RefactoringPackage.ENTITY: {
            final Entity entity = (Entity) theEObject;
            T result = caseEntity(entity);
            if(result == null) {
               result = caseNamedElement(entity);
            }
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case RefactoringPackage.GENERALIZATION: {
            final Generalization generalization = (Generalization) theEObject;
            T result = caseGeneralization(generalization);
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case RefactoringPackage.NAMED_ELEMENT: {
            final NamedElement namedElement = (NamedElement) theEObject;
            T result = caseNamedElement(namedElement);
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case RefactoringPackage.TYPE: {
            final Type type = (Type) theEObject;
            T result = caseType(type);
            if(result == null) {
               result = caseNamedElement(type);
            }
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         case RefactoringPackage.REFACTORING_MODEL: {
            final RefactoringModel refactoringModel = (RefactoringModel) theEObject;
            T result = caseRefactoringModel(refactoringModel);
            if(result == null) {
               result = defaultCase(theEObject);
            }
            return result;
         }
         default:
            return defaultCase(theEObject);
      }
   }

   /**
    * Checks whether this is a switch for the given package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param ePackage
    *           the package in question.
    * @return whether this is a switch for the given package.
    * @generated
    */
   @Override
   protected boolean isSwitchFor(final EPackage ePackage) {
      return ePackage == modelPackage;
   }

} // RefactoringSwitch
