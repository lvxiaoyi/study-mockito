# study-mockito
视频地址：https://www.bilibili.com/video/BV1jJ411A7Sv/?spm_id_from=333.337.search-card.all.click&vd_source=ff64eaedd3a12904e96d15c6c99dd21d

## 细节

mock默认底层会使用一种default的方式，如果使用mock的话不会报错，但是会返回一个默认值，根据不同的返回类型返回不同的默认值ReturnEmptyValue.returnValueFor



## mock方式

**项目中常用的还是使用@mock和@InjectMocks**

可以使用MockitoAnnotations.initMocks(this)/@Rule(junit提供的方法)





## 常用注解

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



## API

### doNothing

```java
doNothing().when(list).clear();
```

当执行到list.clear方法的时候什么都做

### doThrow

```java
doThrow(RuntimeException.class).when(list).clear();
try {
     list.clear();
} catch (Exception e) {
     assertThat(e,instanceOf(RuntimeException.class));
}
```

### verify

```java
verify(list,times(1)).clear();
```

验证没有返回值的方法执行的次数
