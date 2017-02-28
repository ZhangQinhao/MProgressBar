# MProgressBar  
对于系统ProgressBar，对于其宽高，样式的固定不好掌控，故而自定义view实现了横向，纵向，环状进度条，尽力满足不同UI需求。今后会对UI兼容性以及绘制效率进行优化。  

### 引用

 - Android Studio  
 在build.gradle引入 `compile 'com.zhangmonke:MProgressBar:1.0.0'  `
 
 - Maven  
``` stylus
<dependency>
  <groupId>com.zhangmonke</groupId>
  <artifactId>MProgressBar</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```  
### 样式(Sample中可以看到对应属性值)  
![enter description here][1]  
  
![enter description here][2]  
  
![enter description here][3]
### 控件属性
MRingProgressBar暂时不支持滑动点击，后续会改进。
``` stylus
<declare-styleable name="MProgressBar">
    <attr name="maxprogress" format="float"/>    <!--设置最大值-->   <!--通用-->
    <attr name="durprogress" format="float"/>    <!--设置当前值-->  <!--通用-->
    <attr name="bgdrawable" format="color|reference"/>    <!--进度条背景颜色或者图片--> <!--通用-->
    <attr name="bgbordercolor" format="color"/>  <!--底部边框颜色 默认与底色相同-->   <!--通用-->
    <attr name="bgborderwidth" format="dimension"/> <!--底部边框宽度-->   <!--通用-->
    <attr name="fontdrawable" format="color|reference"/>    <!--进度颜色或者图片--> <!--通用-->
    <attr name="cursordrawable" format="reference"/>     <!--游标图标-->   <!--通用-->
    <attr name="cursordrawable_width" format="dimension"/>   <!--游标图标宽度-->  <!--通用-->
    <attr name="cursordrawable_height" format="dimension"/>   <!--游标图标高度-->   <!--通用-->
    <attr name="progresswidth" format="dimension"/>   <!--进度条宽度  默认最大-->  <!--通用-->  
	
    <attr name="fontdrawable_type">    <!--进度颜色或者图片显示类型-->  <!--MVerProgressBar MHorProgressBar-->
        <enum name="clamp" value="0"/>   <!--拉伸-->
        <enum name="repeat" value="1"/>   <!--重复-->
        <enum name="cover" value="2"/>  <!--覆盖  一般适用上层图片覆盖下层图片,通常bgdrawable_type=clamp  详见Sample - id:mpb_5-->
    </attr>

    <attr name="cantouch" format="boolean"/>  <!--是否可以点击-->   <!--MVerProgressBar MHorProgressBar-->

    <attr name="bgdrawable_type">    <!--进度条背景颜色或者图片显示类型-->  <!--MVerProgressBar MHorProgressBar-->
        <enum name="clamp" value="0"/>   <!--拉伸-->
        <enum name="repeat" value="1"/>   <!--重复-->
    </attr>

    <attr name="radius" format="dimension"/>    <!--进度条圆角半径--> <!--MVerProgressBar MHorProgressBar-->
    <attr name="startLeftOrRight">    <!--进度方向：从左开始/从右开始--><!--进度方向：逆时针/顺时针-->  <!--MHorProgressBar MRingProgressBar-->
        <enum name="left" value="0"/>
        <enum name="right" value="1"/>
    </attr>
    <attr name="startTopOrBottom">    <!--进度方向：从下开始/还是从上开始-->  <!--MVerProgressBar-->
        <enum name="bottom" value="0"/>
        <enum name="top" value="1"/>
    </attr>
    <attr name="startangle" format="integer"/>    <!--开始角度--> <!--MRingProgressBar-->
```


  [1]: ./images/a.gif "a.gif"
  [2]: ./images/b.gif "b.gif"
  [3]: ./images/c.gif "c.gif"