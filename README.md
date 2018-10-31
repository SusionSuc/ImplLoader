# ImplLoader

当一个Android工程中如果已经使用不同的module来做工程隔离。那我们就可能有这种需求，`module1`想实例化一个`module2`的类。一般要怎么解决呢？

1. `module1`依赖`module2`
2. 把`module2`的这个类沉到底层库，然后`module1`和`module2`都使用这个底层库。
3. ....等

## ImplLoader的解决方式

只需这样使用即可:

1. 使用`@Impl`标记需要被加载的类
```
//`module2`中的类:
@Impl(name = "module2__text_view")
public class CommonView extends AppCompatTextView {

}
```

2. 使用 `ImplLoader.getImpl("module2__text_view")` 来获取这个类
```
public class Module1Page extends LinearLayout {
    public Module1Page(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        //根据name，获取需要加载的类
        View module1Tv = ImplLoader.getView(getContext(), "common_view");
        addView(module1Tv);
    }
}
```

3. 初始化`ImplLoader`
```
    ImplLoader.init()
```

## ImplLoader的实现以及特性
- 使用 注解、gradle transform、asm、javapoet来实现
- 使用简单，一个`@Impl`注解标注即可
- 支持`kotlin`


## 引入ImplLoader
