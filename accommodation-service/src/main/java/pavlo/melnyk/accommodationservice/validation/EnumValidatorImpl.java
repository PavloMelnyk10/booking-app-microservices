package pavlo.melnyk.accommodationservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, CharSequence> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidator annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumValue -> enumValue.name().equalsIgnoreCase(value.toString()));
    }
}
