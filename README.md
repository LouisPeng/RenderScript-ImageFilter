RenderScript-ImageFilter
========================

Implements image filters using RenderScript.

在Note3上进行整数计算性能对比，设定RenderScript运算耗时为1单位时间，那么NDK需1.42单位时间，Java需6单位时间。
在Nexus5（开启ART）上进行整数计算性能对比，设定RenderScript运算耗时为1单位时间，那么NDK需1.42单位时间，Java需3.4单位时间。
官方数据，http://android-developers.blogspot.com/2013/01/evolution-of-renderscript-performance.html。

通过上面的数据，产生了通过RenderScript重写ImageFilter的想法，网络上RenderScript的资料非常少，通过这个项目也会总结RenderScript的技术细节，形成一系列教程。
