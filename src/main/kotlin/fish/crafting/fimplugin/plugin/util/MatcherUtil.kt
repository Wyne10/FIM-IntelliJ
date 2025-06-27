package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiElement
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
    private val MINIMESSAGE_CLASSPATHS = setOf(
        "net.kyori.adventure.text.minimessage.MiniMessage",
        "net.kyori.adventure.text.serializer.ComponentSerializer"
    )
    private val ADVENTURE_COMPONENT_CLASSPATHS = setOf(
        "net.kyori.adventure.text.Component"
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

    fun matchPsiToMiniMessage(element: PsiElement): Boolean {
        val call = element.toUElementOfType<UCallExpression>() ?: return false
        if(call.kind != UastCallKind.METHOD_CALL) return false

        //If the method parent class is NOT MiniMessage, return false.
        val classpath = call.resolve()?.containingClass?.qualifiedName ?: return false
        if(!matchClassToMiniMessage(classpath)) return false

        //If the return type of the method is not Component, return false.
        val returnType = call.returnType ?: return false
        val returnClasspath = PsiTypesUtil.getPsiClass(returnType)?.qualifiedName ?: return false
        if(!matchClassToComponent(returnClasspath)) return false

        //If the first argument isn't a String, return false
        if(call.valueArgumentCount < 1) return false
        val firstArg = call.valueArguments[0]

        val argClass = PsiTypesUtil.getPsiClass(firstArg.getExpressionType()) ?: return false

        return argClass.isString()
    }

    fun matchClassToVector(classpath: String) = classpath in VECTOR_CLASSPATHS
    fun matchClassToLocation(classpath: String) = classpath in LOCATION_CLASSPATHS
    fun matchClassToMiniMessage(classpath: String) = classpath in MINIMESSAGE_CLASSPATHS
    fun matchClassToComponent(classpath: String) = classpath in ADVENTURE_COMPONENT_CLASSPATHS
}