package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiTypes
import com.intellij.psi.util.PsiTypesUtil
import kotlinx.serialization.builtins.UByteArraySerializer
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
    private val BOUNDINGBOX_CLASSPATHS = setOf(
        "org.bukkit.util.BoundingBox",
        "net.minestom.server.collision.BoundingBox"
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

    fun matchPsiToBoundingBox(element: PsiElement): Boolean {
        val call = element.toUElementOfType<UCallExpression>() ?: return false
        val resolve = call.resolve() ?: return false

        if(call.kind == UastCallKind.CONSTRUCTOR_CALL) {
            val classpath = resolve.containingClass?.qualifiedName ?: return false
            return matchClassToBoundingBox(classpath)
        }else if(call.kind == UastCallKind.METHOD_CALL) {
            val classpath = resolve.containingClass?.qualifiedName ?: return false
            if(!matchClassToBoundingBox(classpath)) return false

            if(!resolve.hasModifierProperty(PsiModifier.STATIC)) return false

            val returnType = call.returnType ?: return false
            val returnClasspath = PsiTypesUtil.getPsiClass(returnType)?.qualifiedName ?: return false
            return matchClassToBoundingBox(returnClasspath)
        }

        return false;
    }

    fun matchClassToVector(classpath: String) = classpath in VECTOR_CLASSPATHS
    fun matchClassToLocation(classpath: String) = classpath in LOCATION_CLASSPATHS
    fun matchClassToBoundingBox(classpath: String) = classpath in BOUNDINGBOX_CLASSPATHS
}