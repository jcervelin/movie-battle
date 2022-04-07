package io.jcervelin.moviebattle.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateSessionImpl.class)
/**
 * Uma sessao e considerada valida when ela existe, esta linkada ao usuario logado e nao esta
 * encerrada, ou seja, quando ({@link GameSessionEntity#getEnd()} nao esta nulo)
 */
public @interface ValidateSession {
  String message() default "Invalid session id: ${validatedValue}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
