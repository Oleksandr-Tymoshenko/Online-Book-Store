package book.store.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String[] fields;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        fields = constraintAnnotation.fields();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object clazz, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
        for (int i = 0; i < fields.length - 1; i++) {
            if (!Objects.equals(beanWrapper.getPropertyValue(fields[i]),
                    beanWrapper.getPropertyValue(fields[i + 1]))) {
                return false;
            }
        }
        return true;
    }
}
