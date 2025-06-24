package pavlo.melnyk.accountservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pavlo.melnyk.accountservice.dto.AccountDto;
import pavlo.melnyk.accountservice.model.Account;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "birthDate", target = "birtDate")
    AccountDto toDto(Account account);
}
