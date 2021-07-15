package at.ac.tuwien.big.momot.lang.ui.launch

import org.eclipse.core.resources.IContainer
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.CoreException
import org.eclipse.debug.core.DebugPlugin
import org.eclipse.debug.core.ILaunchConfiguration
import org.eclipse.debug.ui.DebugUITools
import org.eclipse.debug.ui.ILaunchShortcut
import org.eclipse.debug.ui.RefreshTab
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.ISelection
import org.eclipse.ui.IEditorPart
import org.eclipse.ui.IFileEditorInput
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.util.Strings
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*
import static org.eclipse.jface.dialogs.MessageDialog.*

class MOMoTLaunchShortcut implements ILaunchShortcut {

	public static val String BUNDLE_ID = "at.ac.tuwien.big.momot.lang.ui"
	public static val String LAUNCH_CONFIGURATION_TYPE = BUNDLE_ID + ".MOMoTLaunchConfigurationType"
	
	override launch(ISelection selection, String mode) {
		MessageDialog.openError(null, "Unsupported Launch Selection.", 
			"Please open the file inside an editor to launch a search."
		)
	}
	
	def javaClassType(Resource it) {
		contents.filter(JvmDeclaredType).get(0)
	}
	
	def javaClassIdentifier(Resource it) {
		javaClassType?.identifier
	}
	
	def javaClassFileName(Resource it) {
		javaClassIdentifier.replace('.', '/') + '.java'
	}
	
	def IResource findJavaClass(IResource it, String javaClassName) {
		if(it.fullPath.toString.endsWith(javaClassName))
		  return it
		if(it instanceof IContainer) {
			for(member : it.members) {
				val javaClass = member.findJavaClass(javaClassName)
				if(javaClass != null)
					return javaClass
			}
		}
		return null
	}
	
	override launch(IEditorPart editor, String mode) {
		if (editor instanceof XbaseEditor) {
			if (editor.editorInput instanceof IFileEditorInput) {
				val momotFile = (editor.editorInput as IFileEditorInput).file
				val project = momotFile.project
				val projectName = project.name
				val fileExists = editor.document.readOnly [
					val fileExists = project.findJavaClass(javaClassFileName) != null
					if(!fileExists) {
						openError(null, "Launch Error", "Cannot find Java source file: " + javaClassFileName)
					}
					fileExists
				]
				if(!fileExists)
					return
				val info = editor.document.readOnly [
					new LaunchConfigurationInfo(projectName, javaClassIdentifier)
				]
				launch(mode, info)
				return
			}
		} 
		openError(null, "Wrong editor kind.", "Wrong editor?")
	}

	def launch(String mode, LaunchConfigurationInfo info) {
		if (info.clazz.nullOrEmpty)    
			openError(null, "Launch Error", "Could not determine the class that should be executed.")
		else if (info.project.nullOrEmpty)  
			openError(null, "Launch Error", "Could not determine the project that should be executed.")
		else try {
			val configs = DebugPlugin.getDefault.launchManager.launchConfigurations
			val config = configs.findFirst[info.configEquals(it)] ?: info.createConfiguration 
			DebugUITools.launch(config, mode)
		} catch (CoreException e) {
			openError(null, "Problem running workflow.", e.message)
		}
	}
}

@Data class LaunchConfigurationInfo {
	val String project
	val String clazz
	
	def getName() {
		Strings.lastToken(clazz, ".")
	}
	
	def createConfiguration()  {
		val launchManager = DebugPlugin.getDefault.launchManager
		val configType = launchManager.getLaunchConfigurationType(MOMoTLaunchShortcut.LAUNCH_CONFIGURATION_TYPE)
		val wc = configType.newInstance(null, launchManager.generateLaunchConfigurationName(name))
		wc.setAttribute(ATTR_PROJECT_NAME, project)
		wc.setAttribute(ATTR_MAIN_TYPE_NAME, clazz)
		wc.setAttribute(ATTR_STOP_IN_MAIN, false)
		wc.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${workspace}")
		wc.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true)
		wc.doSave
	}

	def configEquals(ILaunchConfiguration a) {
		a.getAttribute(ATTR_MAIN_TYPE_NAME, "X") == clazz && 
		a.getAttribute(ATTR_PROJECT_NAME, "X") == project &&
		a.type.identifier == MOMoTLaunchShortcut.LAUNCH_CONFIGURATION_TYPE
	}
	
	def isValid() {
		!clazz.nullOrEmpty && !project.nullOrEmpty
	}
}
