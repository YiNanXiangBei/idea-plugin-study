package org.yinan.core.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinan.core.beans.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ZhangYuanSheng
 */
public enum SpringHttpMethodAnnotation {

    /**
     * RequestMapping
     */
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", null),

    /**
     * GetMapping
     */
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", HttpMethod.GET),

    /**
     * PostMapping
     */
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", HttpMethod.POST),

    /**
     * PutMapping
     */
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", HttpMethod.PUT),

    /**
     * DeleteMapping
     */
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", HttpMethod.DELETE),

    /**
     * PatchMapping
     */
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", HttpMethod.PATCH),

    /**
     * RequestParam
     */
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam", null);

    private final String qualifiedName;
    private final HttpMethod method;
    private static final List<SpringHttpMethodAnnotation> METHOD_ANNOTATIONS;

    SpringHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    static {
        METHOD_ANNOTATIONS = Arrays.asList(SpringHttpMethodAnnotation.values());
    }

    /**
     * 判断指定方法是否在注解包含的集合内部
     * @param qualifiedName
     * @return
     */
    @Nullable
    public static SpringHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        Optional<SpringHttpMethodAnnotation> methodAnnotation = METHOD_ANNOTATIONS
                .stream()
                .filter(annotation -> qualifiedName.equals(annotation.getQualifiedName()))
                .findFirst();
        return methodAnnotation.orElse(null);

//        for (SpringHttpMethodAnnotation springRequestAnnotation : SpringHttpMethodAnnotation.values()) {
//            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
//                return springRequestAnnotation;
//            }
//        }
//        return null;
    }

    @Nullable
    public static SpringHttpMethodAnnotation getByShortName(String requestMapping) {
        for (SpringHttpMethodAnnotation springRequestAnnotation : SpringHttpMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @NotNull
    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }
}