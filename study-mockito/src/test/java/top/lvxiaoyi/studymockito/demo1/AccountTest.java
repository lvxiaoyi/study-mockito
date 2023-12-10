package top.lvxiaoyi.studymockito.demo1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import top.lvxiaoyi.studymockito.api.database.Account;
import top.lvxiaoyi.studymockito.service.controller.AccountController;
import top.lvxiaoyi.studymockito.service.impl.SubbingServiceImpl;
import top.lvxiaoyi.studymockito.service.mapper.AccountMapper;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @InjectMocks 需要测试的调用方的注解
 * @Mock 测试调用方中间调用方，就是我们拿不到真正数据需要mock的方法
 */

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountMapper accountMapper;

    @Mock
    private List<String> list;

    @Mock
    private SubbingServiceImpl subbingService;


    @Before
    public void setUp() {

    }

    @Test
    public void loginTest() {
        Mockito.when(request.getParameter("userName")).thenReturn("lvxiaoyi");
        Mockito.when(request.getParameter("passWord")).thenReturn("123456");
        Mockito.when(accountMapper.findAccount(any(), any())).thenReturn(new Account());

        String login = accountController.login(request);

        assertEquals("/index.hmtl", login);
    }

    @Test
    public void howToUseStubbing() {
        when(list.get(0)).thenReturn("first");
        assertEquals(list.get(0), "first");
    }

    /**
     * 验证void方法有没有执行过，或者跳过某个执行方法
     */

    @Test
    public void VoidMethodTest() {
        list.add(0, "first");
        doNothing().when(list).clear();
        list.clear();
        verify(list, times(1)).clear();
    }

    @Test
    public void exceptionMethodTest() {
        doThrow(RuntimeException.class).when(list).clear();
        try {
            list.clear();
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
        }

    }

    @Test
    public void iterateTest() {
        when(list.size()).thenReturn(1, 2, 3, 4);
//        等价于下面的方法
//        when(list.size()).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4);
        assertEquals(list.size(),1);
        assertEquals(list.size(),2);
        assertEquals(list.size(),3);
        assertEquals(list.size(),4);

//        这个就异常了
//        assertEquals(list.size(),5);
    }

    @Test
    public void answerTest(){
        when(list.get(anyInt())).thenAnswer(mock -> {
            Integer index = (Integer)mock.getArgument(0);
            return String.valueOf(index * 10);
        });
        assertEquals(list.get(0),"0");
        assertEquals(list.get(10),"100");
    }

    @Test
    public void subbingRalCallTest(){
        // 相当于InjectMocks注解，调用真正的方法而不是mock方法
        when(subbingService.getI()).thenCallRealMethod();

        assertEquals(10, (int) subbingService.getI());
    }

    @Test
    public void spyTest(){
        // spy是对部分方法的mock，设置了mock方法则走mock方法否则走真正的方法
        ArrayList<String> realList = new ArrayList<>();
        ArrayList<String> arrayList = spy(realList);
        arrayList.add("lvxiaoyi");
        arrayList.add("top");

        assertEquals(arrayList.get(0),"lvxiaoyi");
        assertEquals(arrayList.get(1),"top");
        assertTrue(2 == arrayList.size());

        when(arrayList.isEmpty()).thenReturn(true);
        when(arrayList.size()).thenReturn(1);

        assertEquals(arrayList.get(0),"lvxiaoyi");
        assertEquals(arrayList.get(1),"top");
        assertTrue(arrayList.isEmpty());
        assertTrue(1 == arrayList.size());
    }

}
