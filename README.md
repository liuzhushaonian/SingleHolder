
# 2018.6.19更新

虽然我知道根本没有人使用过这个库，但我还是对它进行了更新，因为我发现了一个重大bug，这货居然无法正常使用！

知道真相的我赶紧修复了这个bug，趁着还没人注意到……

 # SingleHolder——小巧的滑动返回库

这是一个在Android上实现的滑动返回库。它可以做到很优雅地滑动返回上一个界面，且不会造成性能损耗，所有的实现都在一个类里。

好吧上面都是我瞎编的，这是一个仿and_swipeback的项目，我已经很尽力地将它封装成可用的第三方类库了，然而还是有点缺陷。

受到and_swipeback的启发，与之使用了一样的管理Activity的方式，以及显示上一个Activity。

由于个人能力不足，无法看懂and_swipeback的源码，所以并未对它进行复制式抄袭，仅仅是受到启发，代码全是个人书写，信不信由你们。

 # 解析教程：[打造一个一只手就能hold住手机的滑动返回库](https://www.legic.xyz/article/wheel-by-legend-singleholder)

# 仿微信

![image](https://github.com/Android-wheel-by-legend/SingleHolder/blob/master/snapshot/yidong.gif)

# 仿酷安（但是现在酷安也仿微信了，立场真不坚定）

![image](https://github.com/Android-wheel-by-legend/SingleHolder/blob/master/snapshot/shadow.gif)

# 优点：

1、无需对Activity设置透明主题，因此不会消耗性能。

2、由外部一个类管理所有Activity，Activity只需传入实例即可。

3、不影响同等手势滑动操作，大概……

4、可以设置两种返回类型，一种是仿微信返回（底部有偏移），另一种是仿酷安返回（底部不偏移，但是有阴影）

5、小巧，所有操作都在一个类里，八百行代码，让你的APP从此拥有滑动返回功能，一只手就能hold得住

# 不足：

<S>1、暂时无法做到禁止某Activity使用滑动返回（这条貌似做到了，不好意思走错片场了）</S>

2、暂时无法改变默认触摸距离（内设为16dp）

3、暂时需要一个xml文件作为退出动画（这也是上面所说的缺陷）

# 使用方法：


# 导入依赖

``


在Application的onCreate里传入实例给SlideHelper类

`SlideHelper.setApplication(this);`

可设置全局BaseActivity，在其onCreate方法里获取SlideHelper实例

`slideHelper=SlideHelper.getInstance();`

也可以设置是否滑动底部view，实现两种返回类型之一（默认不滑动）

`slideHelper.isScroll=true;`

true表示底部会随之偏移，为仿微信返回。

false则表示底部不会偏移，仿酷安。

在想要进行滑动返回的Activity里加入这句话，即可开启滑动返回

`slideHelper.setSlideActivity(this);`

为保证退出不闪烁，还需注意一个fade.xml的动画文件，放入anim资源文件夹下即可

# 感谢：

<a href="https://github.com/XBeats/and_swipeback">and_swipeback</a>

<a href="http://chaosleong.github.io/2017/05/03/Comparison-of-Android-swipe-back-libraries/">Android 平台滑动返回库对比</a>

