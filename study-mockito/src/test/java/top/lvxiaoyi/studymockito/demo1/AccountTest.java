package top.lvxiaoyi.studymockito.demo1;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import top.lvxiaoyi.studymockito.api.database.Account;
import top.lvxiaoyi.studymockito.service.controller.AccountController;
import top.lvxiaoyi.studymockito.service.impl.SubbingServiceImpl;
import top.lvxiaoyi.studymockito.service.mapper.AccountMapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        assertEquals(list.size(), 1);
        assertEquals(list.size(), 2);
        assertEquals(list.size(), 3);
        assertEquals(list.size(), 4);

//        这个就异常了
//        assertEquals(list.size(),5);
    }

    @Test
    public void answerTest() {
        when(list.get(anyInt())).thenAnswer(mock -> {
            Integer index = (Integer) mock.getArgument(0);
            return String.valueOf(index * 10);
        });
        assertEquals(list.get(0), "0");
        assertEquals(list.get(10), "100");
    }

    @Test
    @DisplayName("设置方法回调函数")
    /**
     * 当mockedList调用get方法的时候，直接更改内部的函数，doAnswer和thAnswer两种方式都可以实现
     */

    public void mockListAnswer() {
        List mockedList = mock(List.class);
        Mockito.when(mockedList.get(Mockito.anyInt())).thenAnswer(invocationOnMock -> {
            System.out.println("哈哈哈，被我逮到了吧");
            Object[] arguments = invocationOnMock.getArguments();
            System.out.println("参数为:" + Arrays.toString(arguments));
            Method method = invocationOnMock.getMethod();
            System.out.println("方法名为:" + method.getName());
            return "结果由我决定";
        });
        Mockito.doAnswer(invocationOnMock -> {
            System.out.println("哈哈哈，被我逮到了吧");
            Object[] arguments = invocationOnMock.getArguments();
            System.out.println("参数为:" + Arrays.toString(arguments));
            Method method = invocationOnMock.getMethod();
            System.out.println("方法名为:" + method.getName());
            return "结果由我决定";
        }).when(mockedList).get(anyInt());
        System.out.println(mockedList.get(0));
    }

    @Test
    public void subbingRalCallTest() {
        // 相当于InjectMocks注解，调用真正的方法而不是mock方法
        when(subbingService.getI()).thenCallRealMethod();

        assertEquals(10, (int) subbingService.getI());
    }

    @Test
    public void spyTest() {
        // spy是对部分方法的mock，设置了mock方法则走mock方法否则走真正的方法
        ArrayList<String> realList = new ArrayList<>();

        /**
         *
         * 等价于@spy注解
         *
         * @Spy
         * private ArrayList<String> arrayList = new ArrayList<>();
         *
         */

        ArrayList<String> arrayList = spy(realList);
        arrayList.add("lvxiaoyi");
        arrayList.add("top");

        assertEquals(arrayList.get(0), "lvxiaoyi");
        assertEquals(arrayList.get(1), "top");
        assertTrue(2 == arrayList.size());

        when(arrayList.isEmpty()).thenReturn(true);
        when(arrayList.size()).thenReturn(1);

        assertEquals(arrayList.get(0), "lvxiaoyi");
        assertEquals(arrayList.get(1), "top");
        assertTrue(arrayList.isEmpty());
        assertTrue(1 == arrayList.size());
    }

    @Test
    public void test() {
        ArrayList arrayList = mock(ArrayList.class);
        arrayList.add("one");
        arrayList.add("two");
        verify(arrayList, description("size()没有调用")).size();  // org.mockito.exceptions.base.MockitoAssertionError: size()没有调用
        verify(arrayList, timeout(200).times(3).description("验证失败")).add(anyString()); // org.mockito.exceptions.base.MockitoAssertionError: 验证失败
    }

    @Test
    @DisplayName("参数捕捉器")
    public void argumentCaptor() {
        List mockList1 = mock(List.class);
        List mockList2 = mock(List.class);
        mockList1.add("John");
        mockList2.add("Brian");
        mockList2.add("Jim");// 获取方法参数
        ArgumentCaptor argument = ArgumentCaptor.forClass(String.class);
        verify(mockList1).add(argument.capture());
        System.out.println(argument.getValue());    //John
        // 多次调用获取最后一次
        ArgumentCaptor argument1 = ArgumentCaptor.forClass(String.class);
        verify(mockList2, times(2)).add(argument1.capture());
        System.out.println(argument1.getValue());    //Jim
        // 获取所有调用参数
        System.out.println(argument1.getAllValues());    //[Brian, Jim]
    }

    @Test
    @DisplayName("mock的默认行为")
    public void mockDefault() {
        // 创建mock对象、使用默认返回
        final ArrayList mockList = mock(ArrayList.class);
        System.out.println(mockList.get(0));    //null
        // 这个实现首先尝试全局配置,如果没有全局配置就会使用默认的回答,它返回0,空集合,null,等等。
        // 参考返回配置：ReturnsEmptyValues
        mock(ArrayList.class, Answers.RETURNS_DEFAULTS);


        // ReturnsSmartNulls首先尝试返回普通值(0,空集合,空字符串,等等)然后它试图返回SmartNull。
        // 如果最终返回对象，那么会简单返回null。一般用在处理遗留代码。
        // 参考返回配置：ReturnsMoreEmptyValues
        mock(ArrayList.class, Answers.RETURNS_SMART_NULLS);


        // 未stub的方法，会调用真实方法。
        //    注1:存根部分模拟使用时(mock.getSomething ()) .thenReturn (fakeValue)语法将调用的方法。对于部分模拟推荐使用doReturn语法。
        //    注2:如果模拟是序列化反序列化,那么这个Answer将无法理解泛型的元数据。
        mock(ArrayList.class, Answers.CALLS_REAL_METHODS);

        // 深度stub，用于嵌套对象的mock。参考：https://www.cnblogs.com/Ming8006/p/6297333.html
        mock(ArrayList.class, Answers.RETURNS_DEEP_STUBS);


        // ReturnsMocks首先尝试返回普通值(0,空集合,空字符串,等等)然后它试图返回mock。
        // 如果返回类型不能mocked(例如是final)然后返回null。
        mock(ArrayList.class, Answers.RETURNS_MOCKS);


        //  mock对象的方法调用后，可以返回自己（类似builder模式）
        mock(ArrayList.class, Answers.RETURNS_SELF);

        // 自定义返回
        final Answer<String> answer = invocation -> "test_answer";
        final ArrayList mockList1 = mock(ArrayList.class, answer);
        System.out.println(mockList1.get(0));   //test_answer
    }

}
