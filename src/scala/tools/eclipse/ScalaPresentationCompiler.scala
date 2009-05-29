/*
 * Copyright 2005-2009 LAMP/EPFL
 */
// $Id$

package scala.tools.eclipse

import scala.concurrent.SyncVar

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.ConsoleReporter

import scala.tools.eclipse.javaelements.{ ScalaIndexBuilder, ScalaJavaMapper, ScalaStructureBuilder }

class ScalaPresentationCompiler(settings : Settings)
  extends Global(settings, new ConsoleReporter(settings))
  with ScalaStructureBuilder with ScalaIndexBuilder with ScalaJavaMapper with ScalaWordFinder {
  
  override def logError(msg : String, t : Throwable) =
    ScalaPlugin.plugin.logError(msg, t)
    
  def loadTree(file : AbstractFile) = {
    val sFile = getSourceFile(file)
    var nscCu = unitOf(sFile)
    if (nscCu.status == NotLoaded) {
      val reloaded = new SyncVar[Either[Unit, Throwable]]
      askReload(List(sFile), reloaded)
      reloaded.get.right.toOption match {
        case Some(thr) => throw thr
        case _ =>
      }
      nscCu = unitOf(sFile)
    }
    nscCu.body
  }
}