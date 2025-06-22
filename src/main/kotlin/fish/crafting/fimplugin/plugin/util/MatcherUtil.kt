package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiElement
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UastCallKind
import org.jetbrains.uast.toUElementOfType

object MatcherUtil {

    private val VECTOR_CLASSPATHS = setOf(
        "org.bukkit.util.Vector",
        "net.minestom.server.coordinate.Vec"
    )
    private val LOCATION_CLASSPATHS = setOf(
        "org.bukkit.Location",
        "net.minestom.server.coordinate.Pos"
    )

    fun matchPsiToVector(element: PsiElement): Boolean {
        val call = element.toUElementOfType<UCallExpression>() ?: return false
        if(call.kind != UastCallKind.CONSTRUCTOR_CALL) return false

        val classpath = call.resolve()?.containingClass?.qualifiedName ?: return false

        return matchClassToVector(classpath)
    }

    fun matchPsiToLocation(element: PsiElement): Boolean {
        val call = element.toUElementOfType<UCallExpression>() ?: return false
        if(call.kind != UastCallKind.CONSTRUCTOR_CALL) return false

        val classpath = call.resolve()?.containingClass?.qualifiedName ?: return false

        return matchClassToLocation(classpath)
    }

    fun matchClassToVector(classpath: String) = classpath in VECTOR_CLASSPATHS
    fun matchClassToLocation(classpath: String) = classpath in LOCATION_CLASSPATHS
}