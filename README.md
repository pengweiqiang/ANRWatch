# Android ANR
## 一、什么是ANR
    ANR全名Application Not Responding，也就是"应用无响应".当操作在一段时间内系统无法处理，系统层面会弹出ANR对话框。

## 二、导致ANR原因
   在Android里，App的响应能力由Activity Manager和Window Manager系统服务来监控的，通常在如下情况会弹出ANR对话框
   1. 5s内无法响应用户输入时间（例如键盘输入，触摸屏幕等）
   2. BroadcastReceiver在10s内无法结束
   3. 主线程在执行Service的各个生命周期函数时20s内没有执行完毕
## 三、ANR检测
###     1. ANR监测原理
    判断ANR的方法其实很简单，我们在子线程里向主线程发消息，如果过了固定时间后，消息仍未处理，则说明已经发生ANR了。

    Android应用程序的所有交互操作和响应，都是通过主线程的消息机制来进行的。例如当用户点击了某个Button，系统会向主线程发送消息，主线程的Looper从主线程消息队列中取出消息并处理，处理完当前消息，主线程Looper再去取出下一个消息。当主线程做了耗时的任务，主线程的Looper就无法从消息队列中取出心的消息，所表现出的就是程序卡顿，甚至是ANR。同理，我们在子线程往主线程发送一个消息，要是消息无法得到及时处理，那说明程序发生了ANR了。

###     2. 定位耗时操作原理
    当程序ANR后，我们可以通过主线程Looper拿到主线程Thread，然后通过getStackTrace拿到主线程当前的调用栈，从而定位到发生ANR的地方，定位到耗时操作。

## 四、ANR分析
### 1.获取ANR产生的trace文件
    ANR产生时, 系统会生成一个traces.txt的文件放在/data/anr/下. 可以通过adb命令将其导出到本地:

    $adb pull data/anr/traces.txt .
### 2 分析traces.txt
## 2.2.1 普通阻塞导致的ANR
获取到的tracs.txt文件一般如下:

    如下以GithubApp代码为例, 强行sleep thread产生的一个ANR.


    ----- pid 2976 at 2016-09-08 23:02:47 -----
    Cmd line: com.anly.githubapp  // 最新的ANR发生的进程(包名)

    ...

    DALVIK THREADS (41):
    "main" prio=5 tid=1 Sleeping
      | group="main" sCount=1 dsCount=0 obj=0x73467fa8 self=0x7fbf66c95000
      | sysTid=2976 nice=0 cgrp=default sched=0/0 handle=0x7fbf6a8953e0
      | state=S schedstat=( 0 0 0 ) utm=60 stm=37 core=1 HZ=100
      | stack=0x7ffff4ffd000-0x7ffff4fff000 stackSize=8MB
      | held mutexes=
      at java.lang.Thread.sleep!(Native method)
      - sleeping on <0x35fc9e33> (a java.lang.Object)
      at java.lang.Thread.sleep(Thread.java:1031)
      - locked <0x35fc9e33> (a java.lang.Object)
      at java.lang.Thread.sleep(Thread.java:985) // 主线程中sleep过长时间, 阻塞导致无响应.
      at com.tencent.bugly.crashreport.crash.c.l(BUGLY:258)
      - locked <@addr=0x12dadc70> (a com.tencent.bugly.crashreport.crash.c)
      at com.tencent.bugly.crashreport.CrashReport.testANRCrash(BUGLY:166)  // 产生ANR的那个函数调用
      - locked <@addr=0x12d1e840> (a java.lang.Class<com.tencent.bugly.crashreport.CrashReport>)
      at com.anly.githubapp.common.wrapper.CrashHelper.testAnr(CrashHelper.java:23)
      at com.anly.githubapp.ui.module.main.MineFragment.onClick(MineFragment.java:80) // ANR的起点
      at com.anly.githubapp.ui.module.main.MineFragment_ViewBinding$2.doClick(MineFragment_ViewBinding.java:47)
      at butterknife.internal.DebouncingOnClickListener.onClick(DebouncingOnClickListener.java:22)
      at android.view.View.performClick(View.java:4780)
      at android.view.View$PerformClick.run(View.java:19866)
      at android.os.Handler.handleCallback(Handler.java:739)
      at android.os.Handler.dispatchMessage(Handler.java:95)
      at android.os.Looper.loop(Looper.java:135)
      at android.app.ActivityThread.main(ActivityThread.java:5254)
      at java.lang.reflect.Method.invoke!(Native method)
      at java.lang.reflect.Method.invoke(Method.java:372)
      at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:903)
      at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:698)

拿到trace信息, 一切好说.
如上trace信息中的添加的中文注释已基本说明了trace文件该怎么分析:

文件最上的即为最新产生的ANR的trace信息.
前面两行表明ANR发生的进程pid, 时间, 以及进程名字(包名).
寻找我们的代码点, 然后往前推, 看方法调用栈, 追溯到问题产生的根源.
以上的ANR trace是属于相对简单, 还有可能你并没有在主线程中做过于耗时的操作, 然而还是ANR了. 这就有可能是如下两种情况了:

### 2.2.2 CPU满负荷
这个时候你看到的trace信息可能会包含这样的信息:

    Process:com.anly.githubapp
    ...
    CPU usage from 3330ms to 814ms ago:
    6% 178/system_server: 3.5% user + 1.4% kernel / faults: 86 minor 20 major
    4.6% 2976/com.anly.githubapp: 0.7% user + 3.7% kernel /faults: 52 minor 19 major
    0.9% 252/com.android.systemui: 0.9% user + 0% kernel
    ...

    100%TOTAL: 5.9% user + 4.1% kernel + 89% iowait
最后一句表明了:

当是CPU占用100%, 满负荷了.
其中绝大数是被iowait即I/O操作占用了.
此时分析方法调用栈, 一般来说会发现是方法中有频繁的文件读写或是数据库读写操作放在主线程来做了.

### 2.2.3 内存原因
其实内存原因有可能会导致ANR, 例如如果由于内存泄露, App可使用内存所剩无几, 我们点击按钮启动一个大图片作为背景的activity, 就可能会产生ANR, 这时trace信息可能是这样的:

    // 以下trace信息来自网络, 用来做个示例
    Cmdline: android.process.acore

    DALVIK THREADS:
    "main"prio=5 tid=3 VMWAIT
    |group="main" sCount=1 dsCount=0 s=N obj=0x40026240self=0xbda8
    | sysTid=1815 nice=0 sched=0/0 cgrp=unknownhandle=-1344001376
    atdalvik.system.VMRuntime.trackExternalAllocation(NativeMethod)
    atandroid.graphics.Bitmap.nativeCreate(Native Method)
    atandroid.graphics.Bitmap.createBitmap(Bitmap.java:468)
    atandroid.view.View.buildDrawingCache(View.java:6324)
    atandroid.view.View.getDrawingCache(View.java:6178)

    ...

    MEMINFO in pid 1360 [android.process.acore] **
    native dalvik other total
    size: 17036 23111 N/A 40147
    allocated: 16484 20675 N/A 37159
    free: 296 2436 N/A 2732

可以看到free的内存已所剩无几.

    当然这种情况可能更多的是会产生OOM的异常...


## 五、避免ANR的方式
     记住一条原则：不要再主线程(UI线程)里面做繁重的操作。







