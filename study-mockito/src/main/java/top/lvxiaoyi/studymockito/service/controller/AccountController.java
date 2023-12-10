package top.lvxiaoyi.studymockito.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import top.lvxiaoyi.studymockito.api.database.Account;
import top.lvxiaoyi.studymockito.service.impl.AccountImpl;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AccountController {

    @Autowired
    private AccountImpl accountImpl;

    public String login(HttpServletRequest request) {
        final String userName = request.getParameter("userName");
        final String passWord = request.getParameter("passWord");
        String isSuccess = "111";
        try {
            isSuccess = accountImpl.findAccount(userName, passWord);
        } catch (Exception e) {
            e.printStackTrace();
            return "/login";
        }
        return "fail".equals(isSuccess) ? "/login" : "/index.html";
    }
}
