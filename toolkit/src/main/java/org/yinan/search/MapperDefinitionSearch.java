package org.yinan.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.yinan.model.Mapper;
import org.yinan.model.MapperIdentifiableStatement;

import java.util.List;
import java.util.Objects;


/**
 * @author yinan
 * @date 2020/5/31
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement element, @NotNull Processor<? super XmlElement> consumer) {
        if (element instanceof PsiTypeParameterListOwner) {
            Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
            if (element instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) element;
                PsiClass psiClass = psiMethod.getContainingClass();
                if (null != psiClass) {
                    Project project = psiMethod.getProject();
                    List<DomFileElement<Mapper>>  fileElements = DomService.getInstance()
                            .getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
                    String qualifieldName = psiClass.getQualifiedName();
                    String methodName = psiMethod.getName();

                    for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                        Mapper mapper = mapperDomFileElement.getRootElement();
                        for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                            String namespace = mapper.getNamespace().getStringValue();
                            String xmlDomElementId = statement.getId().getRawText();
                            if (Objects.equals(qualifieldName, namespace) && methodName.equals(xmlDomElementId)) {
                                processor.process(statement);
                            }
                        }
                    }
                }
            } else if (element instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) element;
                Project project = psiClass.getProject();
                List<DomFileElement<Mapper>> fileElements = DomService
                        .getInstance()
                        .getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
                String quailfiledName = psiClass.getQualifiedName();
                for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                    Mapper mapper = mapperDomFileElement.getRootElement();
                    for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                        String nameSpace = mapper.getNamespace().getStringValue();
                        if (Objects.equals(quailfiledName, nameSpace)) {
                            processor.process(statement);
                        }
                    }
                }

            }
        }
    }
}
