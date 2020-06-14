package org.yinan.core.view.tojson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.activation.DataHandler;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yinan
 * @date 2020/6/11
 */
public class CopyAsJsonAction extends AnAction {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);


    @NonNls
    private static final Map<String, Object> PROPERTIES_TYPES = new HashMap<>(16);

    @NonNls
    private static final Set<String> ANNOTATION_TYPES = new HashSet<>();

    static {
        PROPERTIES_TYPES.put("Byte", 0);
        PROPERTIES_TYPES.put("Short", 0);
        PROPERTIES_TYPES.put("Integer", 0);
        PROPERTIES_TYPES.put("Long", 0L);
        PROPERTIES_TYPES.put("Float", 0.0F);
        PROPERTIES_TYPES.put("Double", 0.0D);
        PROPERTIES_TYPES.put("BOOLEAN", false);
        // 其他
        PROPERTIES_TYPES.put("String", "");
        PROPERTIES_TYPES.put("BigDecimal", null);
        PROPERTIES_TYPES.put("Date", null);
        PROPERTIES_TYPES.put("LocalDate", null);
        PROPERTIES_TYPES.put("LocalTime", null);
        PROPERTIES_TYPES.put("LocalDateTime", null);

        // 注解过滤
        ANNOTATION_TYPES.add("javax.annotation.Resource");
        ANNOTATION_TYPES.add("org.springframework.beans.factory.annotation.Autowired");
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //当前项目
        Project project = e.getData(PlatformDataKeys.PROJECT);
        //当前文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //编辑器实例
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        //其中一个为空都不行
        if (project == null || psiFile == null || editor == null) {
            return;
        }

        if (!(psiFile instanceof PsiJavaFile)) {
            String message = "Convert to JSON failed. Please choose correct file!";
            Notification notification = NOTIFICATION_GROUP.createNotification(message, NotificationType.WARNING);
            Notifications.Bus.notify(notification);
            return;
        }

        //editor.getCaretModel()描述光标插入，比如获取光标插入位置，获取光标数量等等
        //下面就是获取光标位置偏移量
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());

        if (referenceAt == null) {
            String message = "Convert to JSON failed. Please make sure the current page is selected!";
            Notification notification = NOTIFICATION_GROUP.createNotification(message, NotificationType.WARNING);
            Notifications.Bus.notify(notification);
            return;
        }

        //这里应该是获取当前光标对应的文件，这里指的是class文件
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        try {
            //将文件中的字段映射到map
            Map fieldMap = getFields(selectedClass);
            Gson gson = new GsonBuilder().create();
            //字段转为json字符串
            String json = GsonFormatUtil.gsonFormat(gson, fieldMap);
            StringSelection selection = new StringSelection(json);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            String message = "Convert " + selectedClass.getName() + " to JSON success, copied to clipboard.";
            Notification success = NOTIFICATION_GROUP.createNotification(message, NotificationType.INFORMATION);
            Notifications.Bus.notify(success, project);
        } catch (IOException ex) {
            Notification error = NOTIFICATION_GROUP.createNotification("Convert to JSON failed.", NotificationType.ERROR);
            Notifications.Bus.notify(error, project);
        }
    }

    public static Map getFields(PsiClass psiClass) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        //只找class文件
        if (psiClass != null && psiClass.getClassKind() == JvmClassKind.CLASS) {
            //获取当前类所有字段
            PsiField[] fields = psiClass.getAllFields();
            for (PsiField field : fields) {
                PsiType type = field.getType();
                String name = field.getName();

                PsiAnnotation[] annotations = field.getAnnotations();
                //有注解，拿注解的字段
                if (annotations.length > 0 && containsAnnotation(annotations)) {
                    fieldMap.put(name, "");
                    //java基本类型
                } else if (type instanceof PsiPrimitiveType) {
                    fieldMap.put(name, PsiTypesUtil.getDefaultValue(type));
                } else {
                    //既不是基本类型，也没有注解
                    String fieldTypeName = type.getPresentableText();
                    if (PROPERTIES_TYPES.containsKey(fieldTypeName)) {
                        fieldMap.put(name, PROPERTIES_TYPES.get(fieldTypeName));
                        //数组类型
                    } else if (type instanceof PsiArrayType) {
                        java.util.List<Object> list = new ArrayList<>();
                        //这里可能就是数组的真实类型
                        PsiType deepType = type.getDeepComponentType();
                        //
                        String deepTypeName = deepType.getPresentableText();
                        if (deepType instanceof PsiPrimitiveType) {
                            //
                            list.add(PsiTypesUtil.getPsiClass(deepType));
                        } else if (PROPERTIES_TYPES.containsKey(deepTypeName)) {
                            list.add(PROPERTIES_TYPES.get(deepTypeName));
                        } else {
                            //其他类型递归
                            list.add(getFields(PsiUtil.resolveClassInType(deepType)));
                        }
                        fieldMap.put(name, list);
                    } else if (fieldTypeName.startsWith("List") | fieldTypeName.startsWith("ArrayList")
                            || fieldTypeName.startsWith("Set") || fieldTypeName.startsWith("HashSet")) {
                        List<Object> list = new ArrayList<>();
                        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                        PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                        if (iterableClass != null) {
                            list.add(PROPERTIES_TYPES.getOrDefault(iterableClass.getName(), getFields(iterableClass)));
                            fieldMap.put(name, list);
                        } else if (fieldTypeName.startsWith("HashMap") || fieldTypeName.startsWith("Map")) {
                            fieldMap.put(name, "");
                        } else {
                            fieldMap.put(name, getFields(PsiUtil.resolveClassInType(type)));
                        }
                    }
                }
            }
        }
        return fieldMap;
    }

    private static boolean containsAnnotation(PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            if (ANNOTATION_TYPES.contains(annotation.getQualifiedName())) {
                return true;
            }
        }

        return false;
    }
}
