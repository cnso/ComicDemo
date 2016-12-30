ComicDemo
===================
看漫画的一个小例子
##用到的一些库
+ Jsoup
+ Retrofit
+ RxJava
+ Design
+ RecyclerView
+ CardView
+ Percent
+ GreenDao
+ RetroLambda
+ DataBinding
+ Fresco
##遇到的一些问题
1. Lambda + DataBinding

>>Android原生要用Lambda在API 24(太高了), 用Jack倒是可以,可惜Jack和DataBinding不合,还有一条出路是用Kotlin,而Kotlin和GreenDao又不合,选来选去还是RetroLambda冲突少一点.

2. Fresco + ShareElement

>>想做个动画发现Fresco和ShareElement又冲突了,用的[官方解决方案](http://frescolib.org/docs/shared-transitions.html)还是不好用,后来发现一个很奇葩的事,只要`targetSdkVersion`降到`23`就好了,希望Fresco能够完美解决吧.
>>这个好象是因为DraweeView是继承自ImageView所以才出现的,不过Fresco说不再继承自ImageView了,那应该会好了.