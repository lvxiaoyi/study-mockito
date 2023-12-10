package top.lvxiaoyi.studymockito.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.lvxiaoyi.studymockito.api.database.Account;
import top.lvxiaoyi.studymockito.service.mapper.AccountMapper;

@Component
public class AccountImpl {

    @Autowired
    private AccountMapper accountMapper;

    public String findAccount(String userName, String passWord) {
        Account account = null;

        try {
            account =accountMapper.findAccount(userName, passWord);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return account == null ? "fail" : "success";
    }
}
