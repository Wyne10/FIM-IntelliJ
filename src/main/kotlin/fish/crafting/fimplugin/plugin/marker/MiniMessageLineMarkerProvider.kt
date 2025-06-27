package fish.crafting.fimplugin.plugin.marker

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import fish.crafting.fimplugin.plugin.assets.FIMAssets
import fish.crafting.fimplugin.plugin.util.MatcherUtil
import java.awt.event.MouseEvent

class MiniMessageLineMarkerProvider : LineMarkerProviderDescriptor(), GutterIconNavigationHandler<PsiElement> {
    override fun getName() = "MiniMessage preview"
    override fun getIcon() = FIMAssets.MINIMESSAGE

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if(!MatcherUtil.matchPsiToMiniMessage(element)) return null

        return LineMarkerInfo(
            element,
            element.textRange,
            icon,
            { "MiniMessage actions" },
            this,
            GutterIconRenderer.Alignment.LEFT,
            { "minimessage actions" },
        )
    }

    override fun collectSlowLineMarkers(elements: List<PsiElement?>, result: MutableCollection<in LineMarkerInfo<*>>) {
        super.collectSlowLineMarkers(elements, result)
    }

    override fun navigate(e: MouseEvent?, elt: PsiElement?) {

    }
}