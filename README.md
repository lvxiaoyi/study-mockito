# study-mockito
## 建议

**如果对mockito完全没有概念可以可以先看视频P1-P6，这部分看完整体已经知道mockito在做什么，怎么使用，接下来可以跳过视频直接查看文档**

p8-P10没有在跟视频敲代码，仅个人感觉文档更全面



## 项目中常用

### @Mock

@Mock 注解被往往用来创建以及注入模拟实例。我们会用 mockito 框架创建一个模拟的实例类，而不是去真的创建需要的对象。

@Mock 注解也可以用 `var somethingYouWantMock = Mockito.mock(classToMock)` 这种函数方式赋值来替代，它们的效果是一样的，而用 @Mock 注解通常能能看起来代码 "整洁" 一点，因为我们不会想用一堆看起来一样的 Mockito.mock 函数, 在代码里重复得到处都是。

**@Mock 注解的优势：**

- 能够快速创建测试所需的对象
- 重复的模拟创建代码很少
- 测试类可读性更好
- 更容易看懂验证报错，因为注解的模拟类直接是类的属性，可以用字段名去赋予含义。

在下面给出的示例中，我们模拟了HashMap类。在实际测试中，我们将模拟实际的应用类。我们在map中放入一个键值对，然后验证，确认方法调用是在模拟的map实例上执行的。

```java
@Mock
HashMap<String, Integer> mockHashMap;

@Test
public void saveTest()
{
    mockHashMap.put("A", 1);

    Mockito.verify(mockHashMap, times(1)).put("A", 1);
    Mockito.verify(mockHashMap, times(0)).get("A");

    assertEquals(0, mockHashMap.size());
}
```

> 译者补充：
> mock意思就是造一个假的模拟对象，不会去调用这个真正对象的方法。
> 这个mock对象里的所有行为都是未定义的，属性也不会有值，需要你自己去定义它的行为。
> 比如说，你可以mock一个假的size(), 使其返回100，但实际上并没有真的创建一个 size 为100的 Map

```java
...
    when(mockHashMap.size())
        .thenReturn(100);

    assertEquals(100, mockHashMap.size());
...
```

### @Spy

@Spy注释用于创建一个真实对象并监视这个真实对象。@Spy对象能够调用所监视对象的所有正常方法，同时仍然跟踪每一次交互，就像我们使用mock一样，可以自己定义行为。

可以看到，在下面给的示例中，由于我们向它添加了一个 key-value 键值对，size 变成了 1。我们也能够得到真正的通过键 key 去拿到 value 值的结果。这在 Mock 注解的例子中是不可能的。

> 译者补充:
> 因为mock是模拟整个生成一个假对象，spy像是间谍潜伏在真实对象里去篡改行为。

```java
@Spy
HashMap<String, Integer> hashMap;

@Test
public void saveTest()
{
    hashMap.put("A", 10);

    Mockito.verify(hashMap, times(1)).put("A", 10);
    Mockito.verify(hashMap, times(0)).get("A");

    assertEquals(1, hashMap.size());
    assertEquals(new Integer(10), (Integer) hashMap.get("A"));
}
```

**@Mock 和@Spy 的区别**

在使用@Mock时，mockito创建了类的一个基础套壳实例，完全用于跟踪与它的全部交互行为。这不是一个真正的对象，并且不维护状态，不存在更改。

当使用@Spy时，mockito创建一个类的真实实例，可以跟踪与它的每个交互行为，这个真实类能维护类状态的变化。

### @Captor

@Captor注释用于创建ArgumentCaptor实例，该实例用于捕获方法参数值，来用于进一步做断言验证。

注意mockito使用参数类的equals()方法验证参数值是否相同。

```java
@Mock
HashMap<String, Integer> hashMap;

@Captor
ArgumentCaptor<String> keyCaptor;

@Captor
ArgumentCaptor<Integer> valueCaptor;

@Test
public void saveTest() 
{
    hashMap.put("A", 10);

    Mockito.verify(hashMap).put(keyCaptor.capture(), valueCaptor.capture());

    assertEquals("A", keyCaptor.getValue());
    assertEquals(new Integer(10), valueCaptor.getValue());
}
```

### @InjectMocks

在mockito中，我们需要创建被测试的类对象，然后插入它的依赖项(mock)来完全测试行为。因此，我们要用到 **@InjectMocks** 注释。

@InjectMocks 标记了一个应该执行注入的字段。Mockito会按照下面优先级通过**构造函数注入**、**setter注入**或**属性注入**，来尝试注入你标识的mock。如果上面三种任何给定的注入策略注入失败了，Mockito不会报错。

更多信息: [Mock和@ initmock注释的区别](https://link.zhihu.com/?target=https%3A//howtodoinjava.com/mockito/mockito-mock-injectmocks/)

> 译者补充：
> @InjectMocks 一般是你要测的类，他会把要测类的mock属性自动注入进去。
> @Mock 则是你要造假模拟的类。



## 常用方法

| **方法名**                                                   | **描述**                                  |
| ------------------------------------------------------------ | ----------------------------------------- |
| Mockito.mock(classToMock)                                    | 模拟对象                                  |
| Mockito.mock(classToMock, defaultAnswer)                     | 使用默认Answer模拟对象                    |
| Mockito.verify(mock)                                         | 验证行为是否发生                          |
| Mockito.when(methodCall).thenReturn(value)                   | 设置方法预期返回值                        |
| Mockito.when(methodCall).thenReturn(value1).thenReturn(value2) //等价于`Mockito.when(methodCall).thenReturn(value1, value2)` | 触发时第一次返回value1，第n次都返回value2 |
| Mockito.when(methodCall).thenAnswer(answer))                 | 预期回调接口生成期望值                    |
| Mockito.doThrow(toBeThrown).when(mock).[method]              | 模拟抛出异常。                            |
| Mockito.doReturn(toBeReturned).when(mock).[method]           | 设置方法预期返回值（直接执行不判断）      |
| Mockito.doAnswer(answer).when(methodCall).[method]           | 预期回调接口生成期望值（直接执行不判断）  |
| Mockito.doNothing().when(mock).[method]                      | 不做任何返回                              |
| Mockito.doCallRealMethod().when(mock).[method]<br /> //等价于`Mockito.when(mock.[method] ).thenCallRealMethod()`; | 调用真实的方法                            |
| Mockito.spy(Object)                                          | 用spy监控真实对象,设置真实对象行为        |
| Mockito.inOrder(Object… mocks)                               | 创建顺序验证器                            |
| Mockito.reset(mock)                                          | 重置mock                                  |

## 参数匹配器列表

`org.mockito.ArgumentMatchers`中定义了所有的内置匹配器

| 函数名                              | 说明                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| any()                               | 任意类型                                                     |
| any(Class<T> type)                  | 任意指定的Class类型，除了null                                |
| isA(Class<T> type)                  | 指定类型的实现对象                                           |
| anyInt()                            | 任何int或者non-null Integer                                  |
| anyXxx()                            | 其他类似还有（Boolean、Byte、Char、Int、Long、Float、Double、Short、String、List、Set、Map、Collection、Iterable）同样必须非空 |
| eq(value)                           | 等于给定的值                                                 |
| same(value)                         | 和给定的值是同一个对象                                       |
| isNull()                            | null值                                                       |
| notNull()                           | 非null                                                       |
| nullable(Class<T> clazz)            | null 或者给定的类型                                          |
| contains(String substring)          | 包含指定的字符串                                             |
| matches(String regex)               | 匹配正则表达式                                               |
| endsWith(String suffix)             | 以xx结尾                                                     |
| startsWith(String prefix)           | 以xx开头                                                     |
| argThat(ArgumentMatcher<T> matcher) | 自定义匹配器                                                 |

## 验证精确调用次数

verify()默认验证方法被调用1次，可以传入times()方法，匹配精确的次数，或者其他类似方法

- times(n)，匹配n次
- never()，没被调用，等于times(0)
- atMostOnce()，最多1次
- atLeastOnce()，最少1次
- atLeast(n)，最少n次
- atMost(n)，最多n次

## 验证执行顺序

可以通过`Mockito.inOrder(Object... mocks)`创建顺序验证器

```java
// A. 单个mock对象调用顺序验证
List singleMock = mock(List.class);//using a single mock
singleMock.add("was added first");
singleMock.add("was added second");//创建顺序验证器，使用单个mock
InOrder inOrder = inOrder(singleMock);//以下代码验证先调用 "was added first", 然后调用 "was added second"
inOrder.verify(singleMock).add("was added first");
inOrder.verify(singleMock).add("was added second");// B. 组合 mocks 调用顺序验证
List firstMock = mock(List.class);
List secondMock = mock(List.class);//using mocks
firstMock.add("was called first");
secondMock.add("was called second");//创建顺序验证器，使用多个mock
InOrder inOrder = inOrder(firstMock, secondMock);//以下代码验证 firstMock 在 secondMock 之前调用
inOrder.verify(firstMock).add("was called first");
inOrder.verify(secondMock).add("was called second");
```



## 监控真实对象

可以为真实对象创建一个监控(spy)对象。当你使用这个spy对象时真实的对象也会也调用，除非它的函数被stub了。

```java
List list = new LinkedList();
List spy = spy(list);//optionally, you can stub out some methods:
when(spy.size()).thenReturn(100);//using the spy calls *real* methods
spy.add("one");
spy.add("two");//prints "one" - the first element of a list
System.out.println(spy.get(0));//size() method was stubbed - 100 is printed
System.out.println(spy.size());//optionally, you can verify
verify(spy).add("one");
verify(spy).add("two");
```

## 自定义验证失败信息

```java
    @Test
    public void test() {
        ArrayList arrayList = mock(ArrayList.class);
        arrayList.add("one");
        arrayList.add("two");
        verify(arrayList, description("size()没有调用")).size();
        // org.mockito.exceptions.base.MockitoAssertionError: size()没有调用
        verify(arrayList, timeout(200).times(3).description("验证失败")).add(anyString());
        // org.mockito.exceptions.base.MockitoAssertionError: 验证失败
    }
```



## 参数捕捉器

`ArgumentCaptor argument = ArgumentCaptor.forClass(Class clazz)` 创建指定类型的参数捕获器

`argument.capture()` 捕获方法参数

`argument.getValue()` 获取方法参数值，如果方法进行了多次调用，它将返回最后一个参数值

`argument.getAllValues()` 方法进行多次调用后，返回多个参数值

```java
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
```

## doAnswer回调函数

```java
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

```

## 设置mock默认行为

```java
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
```





## 参考：

视频地址：

https://www.bilibili.com/video/BV1jJ411A7Sv/?spm_id_from=333.337.search-card.all.click&vd_source=ff64eaedd3a12904e96d15c6c99dd21d

文档地址：

* https://www.xjx100.cn/news/336930.html?action=onClick

- https://site.mockito.org/
- https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- https://juejin.cn/post/7202666869965520952
