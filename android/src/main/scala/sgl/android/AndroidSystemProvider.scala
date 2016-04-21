package sgl
package android

import _root_.android.content.Intent
import _root_.android.net.Uri
import _root_.android.content.ActivityNotFoundException
import java.net.URI

trait AndroidSystemProvider extends SystemProvider {
  this: AndroidWindowProvider =>

  override def exit(): Unit = {
    mainActivity.finish()
  }

  override def loadTextResource(path: String): Iterator[String] = {
    ???
  }

  override def openWebpage(uri: URI): Unit = {
    val browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString))
    mainActivity.startActivity(browserIntent)
  }

  override def openGooglePlayApp(id: String): Unit = {
    try {
      val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s"market://details?id=$id"))
      mainActivity.startActivity(intent)
    } catch {
      case (ex: ActivityNotFoundException) => {
        openWebpage(new URI(s"https://play.google.com/store/apps/details?id=$id"))
      }
    }
  }

}
