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

    Android应用程序的所有交互操作和响应，都是通过主线程的消息机制来进行的。例如当用户点击了某个Button