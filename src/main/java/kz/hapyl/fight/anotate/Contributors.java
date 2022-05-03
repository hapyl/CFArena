package kz.hapyl.fight.anotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.TYPE,
		ElementType.FIELD,
		ElementType.METHOD,
		ElementType.MODULE,
		ElementType.PACKAGE,
		ElementType.TYPE_USE,
		ElementType.PARAMETER,
		ElementType.CONSTRUCTOR,
		ElementType.LOCAL_VARIABLE,
		ElementType.TYPE_PARAMETER,
		ElementType.ANNOTATION_TYPE,
		ElementType.RECORD_COMPONENT
})
public @interface Contributors {

	// represents contributors of parts of the code
	String[] names();

}
