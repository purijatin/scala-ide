package org.scalaide.ui.internal.preferences

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.core.runtime.preferences.DefaultScope
import scala.tools.nsc.Settings
import org.scalaide.core.ScalaPlugin
import org.scalaide.util.internal.SettingConverterUtil._
import org.scalaide.util.internal.Utils

/**
 * This is responsible for initializing Scala Compiler
 * Preferences to their default values.
 */
class ScalaCompilerPreferenceInitializer extends AbstractPreferenceInitializer {

  /** Actually initializes preferences */
  def initializeDefaultPreferences() : Unit = {
    Utils.tryExecute {
      val node = DefaultScope.INSTANCE.getNode(ScalaPlugin.plugin.pluginId)
      val store = ScalaPlugin.plugin.getPluginPreferences

      def defaultPreference(s: Settings#Setting) {
        val preferenceName = convertNameToProperty(s.name)
        val default = s match {
            case bs : Settings#BooleanSetting => "false"
            case is : Settings#IntSetting => is.default.toString
            case ss : Settings#StringSetting => ss.default
            case ms : Settings#MultiStringSetting => ""
            case cs : Settings#ChoiceSetting => cs.default
          }
        node.put(preferenceName, default)
      }

      IDESettings.shownSettings(ScalaPlugin.defaultScalaSettings()).foreach {_.userSettings.foreach (defaultPreference)}
      IDESettings.buildManagerSettings.foreach {_.userSettings.foreach(defaultPreference)}
      store.setDefault(convertNameToProperty(ScalaPluginSettings.stopBuildOnErrors.name), true)
      store.setDefault(convertNameToProperty(ScalaPluginSettings.relationsDebug.name), false)
      store.setDefault(convertNameToProperty(ScalaPluginSettings.apiDiff.name), false)
      store.setDefault(convertNameToProperty(ScalaPluginSettings.withVersionClasspathValidator.name), true)
    }
  }
}
