package server.interpreter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	String[] args() default {};
	String info();
	String description() default "";
	
	// Access levels
	
	/** access level required to see the command
	 * @return
	 */
	int viewLevel() default 0;
	
	/** access level required to run the command
	 * @return
	 */
	int runLevel() default 0;

}
