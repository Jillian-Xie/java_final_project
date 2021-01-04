package annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)   //Annotations are to be recorded in the class file by the compiler but need not be retained by the VM at run time. This is the default behavior.
public @interface ClassInfo {
    String CompleteTime() default "XXXX-XX-XX XX:XX:XX";  //完成时间
    String Author() default "fsq xlj";      //作者
    String Description() default "";      //类描述
}

