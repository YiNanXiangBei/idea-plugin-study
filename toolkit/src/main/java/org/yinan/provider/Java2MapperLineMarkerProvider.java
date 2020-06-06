package org.yinan.provider;

import com.google.common.collect.Collections2;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.yinan.model.Mapper;
import org.yinan.model.MapperIdentifiableStatement;
import org.yinan.utils.IconUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author yinan
 * @date 2020/5/31
 */
public class Java2MapperLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private static final Function<DomElement, XmlTag> FUN = DomElement::getXmlTag;

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof PsiNameIdentifierOwner && isElementWithInterface(element)) {
            CommonProcessors.CollectProcessor<MapperIdentifiableStatement> processor = process(element);
            Collection<MapperIdentifiableStatement> results = processor.getResults();
            if (!results.isEmpty()) {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                        .create(IconUtils.NAVIGATE_TO_XML)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setTargets(Collections2.transform(results, FUN::apply))
                        .setTooltipTitle("Navigation to target in mapper xml");
                result.add(builder.createLineMarkerInfo(Objects
                        .requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier())));
            }
        }
    }


    private CommonProcessors.CollectProcessor<MapperIdentifiableStatement> process(PsiElement element) {
        CommonProcessors.CollectProcessor<MapperIdentifiableStatement> processor =
                new CommonProcessors.CollectProcessor<>();
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            if (null != psiClass) {
                Project project = psiMethod.getProject();
                List<DomFileElement<Mapper>> fileElements = DomService.getInstance()
                .getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
                String qualfiedName = psiClass.getQualifiedName();
                String methodName = psiMethod.getName();
                for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                    Mapper mapper = mapperDomFileElement.getRootElement();
                    List<MapperIdentifiableStatement> daoElements = mapper.getIdentifiableStatements();
                    for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                        String namespace = mapper.getNamespace().getStringValue();
                        String xmlDomElementId = statement.getId().getRawText();
                        if (Objects.equals(qualfiedName, namespace) & methodName.equals(xmlDomElementId)) {
                            processor.process(statement);
                        }
                    }
                }
            }
        } else if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            // 当前项目
            Project project = psiClass.getProject();
            // 当前项目的所有元素 mapper
            List<DomFileElement<Mapper>> fileElements = DomService.getInstance().getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
            // 只需要判断namespace
            String qualifiedName = psiClass.getQualifiedName();

            for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                Mapper mapper = mapperDomFileElement.getRootElement();
                for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                    String namespace = mapper.getNamespace().getStringValue();
                    if (Objects.equals(qualifiedName, namespace)) {
                        processor.process(statement);
                    }
                }
            }
        }
        return processor;
    }




    private boolean isElementWithInterface(PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }

        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }
}
