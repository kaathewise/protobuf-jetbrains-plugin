package io.protostuff.jetbrains.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User-defined proto type that can be used as a field - message or enum.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class DataType
        extends AbstractNamedNode
        implements ScopeNode, KeywordsContainer, ProtoType {

    private static final String ERROR_ELEMENT = ".__ERROR_ELEMENT";

    public DataType(@NotNull ASTNode node) {
        super(node);
    }

    /**
     * Returns fully qualified name of this message (starting with dot).
     */
    @NotNull
    public String getQualifiedName() {
        PsiElement parent = getParent();
        if (parent instanceof ProtoRootNode) {
            ProtoRootNode proto = (ProtoRootNode) parent;
            String packageName = proto.getPackageName();
            if (packageName.isEmpty()) {
                return "." + getName();
            }
            return "." + packageName + "." + getName();
        }
        if (parent instanceof OneOfNode) {
            OneOfNode oneOfNode = (OneOfNode) parent;
            MessageNode parentMessage = (MessageNode) oneOfNode.getParent();
            String parentMessageQualifiedName = parentMessage.getQualifiedName();
            return parentMessageQualifiedName + "." + getName();
        }
        if (parent instanceof MessageNode) {
            MessageNode parentMessage = (MessageNode) parent;
            String parentMessageQualifiedName = parentMessage.getQualifiedName();
            return parentMessageQualifiedName + "." + getName();
        }
        if (parent instanceof PsiErrorElement) {
            return ERROR_ELEMENT;
        }
        throw new IncorrectOperationException("Could not detect qualified name in given context: "
                + parent.getClass().getName());
    }

    /**
     * Returns full name of this type without leading dot.
     */
    @NotNull
    public String getFullName() {
        return getQualifiedName().substring(1);
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return null;
    }
}
