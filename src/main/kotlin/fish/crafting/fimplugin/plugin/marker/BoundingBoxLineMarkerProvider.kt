package fish.crafting.fimplugin.plugin.marker

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import fish.crafting.fimplugin.plugin.assets.FIMAssets
import fish.crafting.fimplugin.plugin.util.DataKeys
import fish.crafting.fimplugin.plugin.util.LineMarkerUtil
import fish.crafting.fimplugin.plugin.util.MatcherUtil
import fish.crafting.fimplugin.plugin.util.isLeafIdentifier
import java.awt.Component
import java.awt.event.MouseEvent

class BoundingBoxLineMarkerProvider : LineMarkerProviderDescriptor(), GutterIconNavigationHandler<PsiElement> {
    override fun getName() = "BoundingBox actions"
    override fun getIcon() = FIMAssets.BOUNDING_BOX

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if(!element.isLeafIdentifier()) return null

        val constructor = LineMarkerUtil.getConstructorFromLeaf(element) ?: return null
        if(!MatcherUtil.matchPsiToBoundingBox(constructor)) return null

        return LineMarkerInfo(
            element,
            element.textRange,
            icon,
            { "BoundingBox actions" },
            this,
            GutterIconRenderer.Alignment.LEFT,
            { "boundingbox actions" },
        )
    }

    override fun navigate(e: MouseEvent?, elt: PsiElement?) {
        if(elt == null) return

        val constructor = LineMarkerUtil.getConstructorFromLeaf(elt) ?: return

        if(e != null){
            val action = ActionManager.getInstance().getAction("fim.boundingbox_group") as ActionGroup
            val c: Component? = e.component

            if(c != null){
                val popup = ActionManager.getInstance().createActionPopupMenu(
                    ActionPlaces.EDITOR_POPUP,
                    action
                )
                val builder = SimpleDataContext.builder()
                    .setParent(DataManager.getInstance().getDataContext(c, e.x, e.y))
                    .add(DataKeys.PASSED_ELEMENT, constructor)

                popup.setDataContext {
                    builder.build()
                }

                popup.component.show(c, e.x, e.y)
            }

        }
    }
}