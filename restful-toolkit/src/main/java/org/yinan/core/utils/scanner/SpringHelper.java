/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: SpringHelper
  Author:   ZhangYuanSheng
  Date:     2020/5/28 21:08
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package org.yinan.core.utils.scanner;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinan.core.annotation.SpringHttpMethodAnnotation;
import org.yinan.core.beans.HttpMethod;
import org.yinan.core.beans.Request;
import org.yinan.core.utils.RestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class SpringHelper {

    @NotNull
    public static Map<String, List<Request>> getSpringRequestByModule(@NotNull Project project, @NotNull Module module) {
        Map<String, List<Request>> moduleMap = new HashMap<>();

        //找到所有controler的类
        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleMap;
        }

        for (PsiClass controllerClass : controllers) {
            List<Request> parentRequests = new ArrayList<>(0);
            List<Request> childrenRequests = new ArrayList<>();
            //获取requestmapping这个注解
            PsiAnnotation psiAnnotation = controllerClass.getAnnotation(
                    SpringHttpMethodAnnotation.REQUEST_MAPPING.getQualifiedName()
            );
            //如果RequestMapping这个注解存在，那么需要拿到这个头
            //这里需要考虑清楚RequestMapping这个类是可以有多个path，也可以有多个method所以组合方式有m*n种
            if (psiAnnotation != null) {
                parentRequests = getRequests(psiAnnotation, null, null);
            }

            //当前类的所有方法
            PsiMethod[] psiMethods = controllerClass.getMethods();
            psiMethods = getAllParentMethods(controllerClass.getSuperClass(), psiMethods);
            for (PsiMethod psiMethod : psiMethods) {
                childrenRequests.addAll(getRequests(psiMethod, controllerClass.getQualifiedName()));
            }

            if (parentRequests.isEmpty()) {
                moduleMap.put(controllerClass.getQualifiedName(), childrenRequests);
            } else {
                Set<Request> moduleSet = new HashSet<>();
                //这里使用双重for循环肯定是有问题的，需要重构
                parentRequests.forEach(parentRequest ->
                        childrenRequests.forEach(childrenRequest -> {
                            Request request = childrenRequest.copyWithParent(parentRequest);
                            moduleSet.add(request);
                            //如果controller上的请求类型和当前方法上请求类型不同
                            if (parentRequest.getMethod() != null && parentRequest.getMethod() != childrenRequest.getMethod()) {
                                //需要为当前方法增加一个额外的请求类型
                                Request parentNotInChild = childrenRequest.copyWithParent(parentRequest);
                                parentNotInChild.setMethod(parentRequest.getMethod());
                                moduleSet.add(parentNotInChild);
                            }


                }));
                //key 类名全称
                moduleMap.put(controllerClass.getQualifiedName(), new ArrayList<>(moduleSet));
            }
        }
        return moduleMap;
    }

    /**
     * 获取所有的控制器类
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private static List<PsiClass> getAllControllerClass(@NotNull Project project, @NotNull Module module) {
        List<PsiClass> allControllerClass = new ArrayList<>();

        //全局搜索
        GlobalSearchScope moduleScope = RestUtil.getModuleScope(module);
        //从java注解索引中找名字是controller的
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(
                Control.Controller.getName(),
                project,
                moduleScope
        );
        pathList.addAll(JavaAnnotationIndex.getInstance().get(
                Control.RestController.getName(),
                project,
                moduleScope
        ));
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
            PsiElement psiElement = psiModifierList.getParent();

            if (!(psiElement instanceof PsiClass)) {
                continue;
            }

            PsiClass psiClass = (PsiClass) psiElement;
            allControllerClass.add(psiClass);
        }
        return allControllerClass;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     * @param annotation annotation
     * @return list
     * @see SpringHelper#getRequests(PsiMethod, String)
     */
    @NotNull
    private static List<Request> getRequests(@NotNull PsiAnnotation annotation,
                                             @Nullable PsiMethod psiMethod,
                                             @Nullable String classQualifiedName) {
        //判断注解是否在指定的请求集合中，如果是那么返回这个自定义的请求注解
        SpringHttpMethodAnnotation spring = SpringHttpMethodAnnotation.getByQualifiedName(
                annotation.getQualifiedName()
        );
        if (spring == null) {
            return Collections.emptyList();
        }
        Set<String> methods = new HashSet<>();
        //判断method属性，确定属于哪种请求，为空说明属于RequestMapping
        methods.add(spring.getMethod() == null ? "ALL" : spring.getMethod().name());
        List<String> paths = new ArrayList<>();

        // 是否为隐式的path（未定义value或者path）
        boolean hasImplicitPath = true;
        //这里是获取某个注解中的属性
        List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            //获取注解属性名称，这里只关注path和method
            String name = attribute.getAttributeName();
            //查看method属性，all表示这个请求是requestMapping
            if (methods.contains("ALL") && "method".equals(name)) {
                // method可能为数组
                Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    methods.add((String) value);
                } else if (value instanceof List) {
                    //noinspection unchecked,rawtypes
                    List<String> list = (List) value;
                    for (String item : list) {
                        if (item != null) {
                            item = item.substring(item.lastIndexOf(".") + 1);
                            methods.add(item);
                        }
                    }
                }
            }

            boolean flag = false;
            //从这段代码可以知道如果path和value同时存在，那么看读取顺序，确定那value还是path
            for (String path : new String[]{"value", "path"}) {
                if (path.equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                continue;
            }
            //这边是处理path的
            Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
            if (value instanceof String) {
                paths.add(((String) value));
            } else if (value instanceof List) {
                //noinspection unchecked,rawtypes
                List<Object> list = (List) value;
                list.forEach(item -> paths.add((String) item));
            }
            hasImplicitPath = false;
            //for 循环结束
        }
        if (hasImplicitPath) {
            if (psiMethod != null) {
                paths.add("/");
            }
        }
        List<Request> requests = new ArrayList<>(paths.size());

        paths.forEach(path -> {
            for (String method : methods) {
                PsiMethod finalPsiMethod = psiMethod;
                HttpMethod rm = null;
                if ("ALL".equals(method)) {
                    if (methods.size() > 1) {
                        continue;
                    }
                } else {
                    if (method != null) {
                        rm = HttpMethod.valueOf(method);
                    }
                }
                if (finalPsiMethod != null) {
                    Collection<PsiMethod> overrdingMethods = OverridingMethodsSearch.search(psiMethod).findAll();
                    if (!overrdingMethods.isEmpty()) {
                        for (PsiMethod overridingMethod : overrdingMethods) {
                            PsiClass psiClass = overridingMethod.getContainingClass();
                            if (psiClass != null) {
                                String className = psiClass.getQualifiedName();
                                if (className != null && className.equals(classQualifiedName)) {
                                    finalPsiMethod = overridingMethod;
                                }
                            }
                        }
                    }
                }
                requests.add(new Request(
                        rm,
                        path,
                        finalPsiMethod
                ));
            }
        });
        return requests;
    }

    /**
     * 获取方法中的参数请求，生成RequestBean
     *
     * @param method Psi方法
     * @return list
     */
    @NotNull
    private static List<Request> getRequests(@NotNull PsiMethod method, @Nullable String classQualifiedName) {
        List<Request> requests = new ArrayList<>();
        PsiAnnotation[] annotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            requests.addAll(getRequests(annotation, method, classQualifiedName));
        }

        return requests;
    }

    @NotNull
    private static PsiMethod[] getAllParentMethods(PsiClass psiClass, PsiMethod[] psiMethods) {
        //为空或者父类是Object，那么退出
        if (psiClass == null || (psiClass.getAnnotation(Control.QualifieldController.getName()) == null &&
                psiClass.getAnnotation(Control.QualifieldRestController.getName()) == null)) {
            return psiMethods;
        }



        PsiMethod[] tmpPsiMethods = psiClass.getMethods();
        int oldLength = psiMethods.length;
        int newLength = tmpPsiMethods.length;
        psiMethods = Arrays.copyOf(psiMethods, oldLength + newLength);
        System.arraycopy(tmpPsiMethods, 0, psiMethods, oldLength, newLength);
        return getAllParentMethods(psiClass.getSuperClass(), psiMethods);
    }

    enum Control {

        /**
         * <p>@Controller</p>
         */
        Controller("Controller"),

        /**
         * <p>@RestController</p>
         */
        RestController("RestController"),

        /**
         * 全称
         */
        QualifieldController("org.springframework.stereotype.Controller"),

        /**
         * 全称
         */
        QualifieldRestController("org.springframework.web.bind.annotation.RestController");

        private final String name;

        Control(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
