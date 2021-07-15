/**
 */
package momot.examples.refactoring.refactoring.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import momot.examples.refactoring.refactoring.Entity;
import momot.examples.refactoring.refactoring.Generalization;
import momot.examples.refactoring.refactoring.NamedElement;
import momot.examples.refactoring.refactoring.Property;
import momot.examples.refactoring.refactoring.RefactoringModel;
import momot.examples.refactoring.refactoring.RefactoringPackage;
import momot.examples.refactoring.refactoring.Type;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * 
 * @see momot.examples.refactoring.refactoring.RefactoringPackage
 * @generated
 */
public class RefactoringAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected static RefactoringPackage modelPackage;

   /**
    * The switch that delegates to the <code>createXXX</code> methods.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected RefactoringSwitch<Adapter> modelSwitch = new RefactoringSwitch<Adapter>() {
      @Override
      public Adapter caseEntity(final Entity object) {
         return createEntityAdapter();
      }

      @Override
      public Adapter caseGeneralization(final Generalization object) {
         return createGeneralizationAdapter();
      }

      @Override
      public Adapter caseNamedElement(final NamedElement object) {
         return createNamedElementAdapter();
      }

      @Override
      public Adapter caseProperty(final Property object) {
         return createPropertyAdapter();
      }

      @Override
      public Adapter caseRefactoringModel(final RefactoringModel object) {
         return createRefactoringModelAdapter();
      }

      @Override
      public Adapter caseType(final Type object) {
         return createTypeAdapter();
      }

      @Override
      public Adapter defaultCase(final EObject object) {
         return createEObjectAdapter();
      }
   };

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public RefactoringAdapterFactory() {
      if(modelPackage == null) {
         modelPackage = RefactoringPackage.eINSTANCE;
      }
   }

   /**
    * Creates an adapter for the <code>target</code>.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param target
    *           the object to adapt.
    * @return the adapter for the <code>target</code>.
    * @generated
    */
   @Override
   public Adapter createAdapter(final Notifier target) {
      return modelSwitch.doSwitch((EObject) target);
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link momot.examples.refactoring.refactoring.Entity <em>Entity</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.Entity
    * @generated
    */
   public Adapter createEntityAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for the default case.
    * <!-- begin-user-doc -->
    * This default implementation returns null.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @generated
    */
   public Adapter createEObjectAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link momot.examples.refactoring.refactoring.Generalization <em>Generalization</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.Generalization
    * @generated
    */
   public Adapter createGeneralizationAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link momot.examples.refactoring.refactoring.NamedElement <em>Named Element</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.NamedElement
    * @generated
    */
   public Adapter createNamedElementAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link momot.examples.refactoring.refactoring.Property <em>Property</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.Property
    * @generated
    */
   public Adapter createPropertyAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link momot.examples.refactoring.refactoring.RefactoringModel <em>Model</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.RefactoringModel
    * @generated
    */
   public Adapter createRefactoringModelAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class '{@link momot.examples.refactoring.refactoring.Type
    * <em>Type</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see momot.examples.refactoring.refactoring.Type
    * @generated
    */
   public Adapter createTypeAdapter() {
      return null;
   }

   /**
    * Returns whether this factory is applicable for the type of the object.
    * <!-- begin-user-doc -->
    * This implementation returns <code>true</code> if the object is either the model's package or is an instance object
    * of the model.
    * <!-- end-user-doc -->
    * 
    * @return whether this factory is applicable for the type of the object.
    * @generated
    */
   @Override
   public boolean isFactoryForType(final Object object) {
      if(object == modelPackage) {
         return true;
      }
      if(object instanceof EObject) {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

} // RefactoringAdapterFactory
