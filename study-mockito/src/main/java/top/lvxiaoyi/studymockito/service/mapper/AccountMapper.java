package top.lvxiaoyi.studymockito.service.mapper;

import org.springframework.stereotype.Component;
import top.lvxiaoyi.studymockito.api.database.Account;


@Component
public class AccountMapper {
    public Account findAccount(String userName, String passWord){
        throw new RuntimeException();
    }
}
