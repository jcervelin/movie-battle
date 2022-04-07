package io.jcervelin.moviebattle.validators;

import io.jcervelin.moviebattle.usecases.GameSessionManagement;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidateSessionImpl implements ConstraintValidator<ValidateSession, String> {

  @Autowired private GameSessionManagement getGameSessionManagement;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return getGameSessionManagement.isValid(value);
  }
}
