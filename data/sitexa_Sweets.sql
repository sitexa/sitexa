INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (1, 'xnpeng', '2017-05-17 09:02:09.016000', null, null, '人物介绍：李和平，世界和平组织总干事。韩国籍，英国牛津大学法学博士。曾任联合国教科文组织专员，致力于消除落后国家的贫穷和疾病，在非州国家建立了33所希望学校和100所慈善医院，让以万计的失学儿童重返课堂，救治过数以万计的病人的生命。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (2, 'xnpeng', '2017-05-19 09:11:28.333000', 1, null, 'ok');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (3, 'xnpeng', '2017-05-19 11:16:41.804000', 1, null, 'good');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (4, 'xnpeng', '2017-06-12 11:01:52.518000', null, null, 'hello');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (5, 'xnpeng', '2017-06-17 18:37:43.169000', null, null, '为什么要实现这么个功能，当然不是我闲得慌，当然是产品的需求。身为码农你只能想方设法去实现，即使留给你的时间已经不多了，想起一句话：这个需求很简单，怎么实现我不管，月底上线.');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (6, 'xnpeng', '2017-06-17 18:42:50.374000', null, null, '初一看，恩9种样式，宫格布局的，这个应该很简单，还要实现拖拽，RecyclerView + GridLayoutManager设置spanSize + ItemTouchHelper 一波带走；
再一看，我擦这3张的和6张的怎么这么是这样的？');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (7, 'xnpeng', '2017-06-17 18:45:58.045000', null, null, '首先，时间上，规定时间需要上线版本，这个布局留给我的时间有且仅有充裕的1天，对于实现过类似功能的人来说，一天确实很充裕；
其次，功能上，逻辑并不复杂，条理也很清晰，就是9张图，9种排列方式，用到的地方两处：
1）发布的时候需要拖拽;
2）显示详情的时候需要展示，不能拖拽；
方案有：
1.写9种静态布局;
2.addview的方式动态添加布局;
3.万能的recyclerView

最后排除1、2方案，采用方案3
时间上，自定义LayoutManager可能来不及，不知里面有什么坑，只好去找轮子.');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (8, 'xnpeng', '2017-06-17 18:48:40.665000', null, null, '让我们看看有什么轮子：
FreeSizeDraggableLayout
Android 布局之GridLayout
我发现这两个，都不是我想要的,具体可去看源码和实现
然后又找到两个关于自定义recyclerview的库
two-way
vlayout
找到以上库的时候，半天已经过去，只剩下半天“充裕”的时间了.

当时导入two-way库的时候出现了问题，一直build不起，只好选择vlayout，毕竟时间不等人；上面的demo.gif是用vlayout实现的;后面我试了two-way库，也实现了这个效果，喜欢的朋友可以去试试

贴上最终实现效果：');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (9, 'xnpeng', '2017-06-17 18:50:16.905000', null, null, '1.
git clone https://github.com/wobiancao/ImageNice9Layout.git
然后依赖
compile project('':imagenice9lib'')
2.属性：
app:nice9_itemMargin="5dp"//每个图片之间的间距
app:nice9_candrag="false"//是否支持拖拽，默认false
3.Item点击接口:
mImageNice9Layout.setItemDelegate(new ImageNice9Layout.ItemDelegate());
4.使用，直接xml布局就行：

<wobiancao.nice9.lib.ImageNice9Layout
        android:id="@+id/item_nice9_image"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        app:nice9_itemMargin="5dp"
        app:nice9_candrag="false"/>');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (10, 'xnpeng', '2017-06-17 18:53:08.651000', null, null, '我有个同学，出身贫寒，毕业前问他想做什么，他说没什么太想做的，毕业后的目标是先给自己家盖个房子，以后的事情以后再说，校招的时候去了华为，其实就是普通的一线工。他说非常辛苦整天工作得人都精神麻木了，但是 仅仅两年他的目标就实现了，已经攒到十多万，一部分拿出来给家里盖房，剩下几万，他说直接颓废一年。也许才这点钱知乎很多人都瞧不上，但对于一个出身贫寒又不算聪明没啥技术能力的人，只要进到华为肯吃苦肯坚持就能拿得到，如果去别的地方你根本不用想。华为做的很多事情我都不喜欢，但他帮一个一无所有的年轻人实现了人生的小目标，凭这点我就服华为。毒瘤？   你们这帮TM不毒瘤的倒是给我同学十万盖房子啊————————————————————
鉴于那么多人以为我们这学校是好学校我只好暴露一下学历，大专唉，丢人但凡拿毒贩出来比的，我在这里统一回复：我不知道你们这帮人为什么会XX到拿毒贩跟华为对比，华为是靠违法赚的钱吗？ 滑坡滑到了地心 到底让我回你们什么？ 我只能说 那你们跟卖笔也差不多 毕竟你们这么XX');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (11, 'xnpeng', '2017-06-17 18:53:57.785000', null, null, '时下，运动社交软件和当初的团购网一样扎堆涌现，仅跑步APP就有50多种，同质化非常严重，从现在市场上的运动社交软件来看，可以说几乎没有一家找到运动社交软件的真正目标群体和核心驱动力，这是一片巨大的盲区，也是一个难得的机会。  

 从目标群体的定位来看，所有运动社交软件都以运动者为中心，为其提供技术和社交服务，这不能说是错，但至少是不准确。举个例子：建一个汽车训练场，通常的运营思维是做好硬件建设和配套服务，为了提高用户体验，留住客户，在服务上提供了诸多帮助学员互动社交的手段，在技术上不断创新和建设（比如增加红外线探侧、增加模拟驾驶系统等等），总之，一切目的都是为了更好地吸引和服务学员。当前的运动社交软件就如同这个汽车训练场，所有经营者的目光都聚集在围墙里，聚集在拓展学员和服务学员的目标上。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (12, 'xnpeng', '2017-06-19 18:24:17.569000', null, null, 'Today it’s hard to imagine any serious Web development without NodeJS. Even though Gradle plugins performing frontend related tasks exist, they usually lag behind the original libraries written for Node. Besides, we are going to use NodeJS only during development and installing it on production servers is completely unnecessary.

When people talk about NodeJS in the context of Web development, they usually mean two things:

node - the Node JavaScript interpreter based on V8 engine
npm - the Node Package Manager, which comes with Node and helps with downloading and installing node-packages, running JS scripts
Fortunately, nvm manages the versions of both tools.');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (13, 'xnpeng', '2017-06-19 18:25:53.288000', null, null, 'React37 is a JavaScript library developed by Facebook and open-sourced in 2013. It combines several unconventional approaches to typical frontend problems, which appeals to many developers. In particular, React promotes the idea of writing HTML-tags alongside JavaScript code. It may look crazy at first, but many agree that this approach gives a lot of flexibility.

Unlike more feature-rich solutions like AngularJS, React is only concerned with the View in MVC (Model-View-Controller), which can be both a good thing or bad thing. On the bright side, it means that React is very unopinionated about the technology stack you are going to use and thus compatible with almost everything. The bad news is that many familiar components of a typical JavaScript framework are not provided out of the box.');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (14, 'xnpeng', '2017-06-19 18:26:26.362000', null, null, 'Axios is promise based HTTP client for the browser and node.js .

Make XMLHttpRequests from the browser
Make http requests from node.js
Supports the Promise API
Intercept request and response
Transform request and response data
Cancel requests
Automatic transforms for JSON data
Client side support for protecting against XSRF');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (15, 'xnpeng', '2017-06-19 18:27:17.059000', null, null, 'OkHttp Authenticator in Ktor

Ktor提供了多种认证机制，Basic,OAuth1a,OAuth2. 测试一种简单的认证机制BasicAuthentication,使用场景是 AppClient访问AppServer时，需要进行认证。通常是登记一个AppID,分配一个AppKey.客户端在请求中携带这一对字 符串，服务端用其与数据库中保存的认证串比对以进行核实。

服务端

Ktor有一个类UserHashedTableAuth(Map<String,ByteArray>,Digester)，Map<String,ByteArray>是name,password 健值对，是保存在服务端的用户名和密码对。该类有一处方法authenticate(UserPasswordCredential),返回值是UserIdPrincipal.

val hashedUserTable = UserHashedTableAuth(table = mapOf(
        "appId" to decodeBase64("VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=") // sha256 for "appkey"
))

authentication { basicAuthentication("ktor") { hashedUserTable.authenticate(it) } }
 
普通表单(contentType:application/form-data)可以通过class Download(val id:Int)来接收表单项， 文件表单(multipart/form-data)不能通过class Upload接收表单项，这些表单项(FormItem,FileItem)需要从multipart中解码出来.

客户端

第一步，定义Authenticator实例，写入Credential,将其放进request的header里面； 第二步，创建客户端，okClient,放入authenticator. 第三步，发送请求到服务端，如果认证错误，则请求不到所需要的数据，如果通过认证，则执行请求的动作。


val authenticator = Authenticator { _, response ->
    fun responseCount(response: Response): Int {
        var result = 1
        while ((response.priorResponse()) != null) result++
        return result
    }

    if (responseCount(response) >= 3) return@Authenticator null

    val credential = Credentials.basic("appId", "appKey")
    response.request().newBuilder().header("Authorization", credential).build()
}

val okClient = OkHttpClient().newBuilder()
            .authenticator(authenticator)
            .build()

val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

private val sweetApi = retrofit.create(SweetApi::class.java)

fun getSweet(id: Int): Sweet = sweetApi.singleSweet(id).execute().body()

val sweet = getSweet(id:Int)');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (16, 'xnpeng', '2017-06-19 18:28:22.736000', null, null, 'Gson组件配合OkHttp,Retrofit在网络访问对Json串进行转化时，与Moshi组件有区别。前者是在组件内容进行的， 后者是在组件外部进行的。从编码角度看，Moshi很简单；从代码的优雅度看，Gson更胜一筹。Moshi的TypeAdapter 写法简单明了，而Gson的TypeAdapter就要复杂得多。下面代码中，JodaTimeAdapter是Moshi写法，JodaTypeAdapter 是Gson写法。结合本文与上一文，对照一下两者的区别。

RestFul服务接口,
OkHttpClient，网络访问客户端
Retrofit,RestFul接口操作组件，根据接口生成实例代码，不用手工写实例代码
Gson,JSON转换组件
DateTypeAdapter,类型转换适配器
与另一文章中的Api接口的返回值类型不同：

上文的返回类型：Call<ResponseBody>,接口规范不明确，不知道其中对象类型；
本方的返回类型：Call<Sweet>,Call<List<Sweet>>，接口规范明确。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (17, 'xnpeng', '2017-06-20 10:02:23.025000', 1, null, '李和平，世界和平组织总干事。韩国籍，英国牛津大学法学博士。曾任联合国教科文组织专员，致力于消除落后国家的贫穷和疾病，在非州国家建立了33所希望学校和100所慈善医院，让以万计的失学儿童重返课堂，救治过数以万计的病人的生命。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (18, 'xnpeng', '2017-06-20 10:03:39.462000', 1, null, 'Hello from Hugo! If you’re reading this in your browser, good job! The file content/post/hello-hugo.md has been converted into a complete HTML document by Hugo. Isn’t that pretty nifty? A Section Here’s a simple titled section where you can place whatever information you want. You can use inline HTML if you want, but really there’s not much that Markdown can’t do. Showing off with Markdown A full cheat sheet can be found here or through Google.');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (19, 'xnpeng', '2017-06-20 11:12:20.263000', 15, null, 'private val sweetApi = retrofit.create(SweetApi::class.java)
fun getSweet(id: Int): Sweet = sweetApi.singleSweet(id).execute().body()
val sweet = getSweet(id:Int)');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (20, 'xnpeng', '2017-06-20 11:12:49.168000', 15, null, '第一步，定义Authenticator实例，写入Credential,将其放进request的header里面； 第二步，创建客户端，okClient,放入authenticator. 第三步，发送请求到服务端，如果认证错误，则请求不到所需要的数据，如果通过认证，则执行请求的动作。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (21, 'xnpeng', '2017-06-20 11:13:15.673000', 15, null, 'Ktor提供了多种认证机制，Basic,OAuth1a,OAuth2. 测试一种简单的认证机制BasicAuthentication,使用场景是 AppClient访问AppServer时，需要进行认证。通常是登记一个AppID,分配一个AppKey.客户端在请求中携带这一对字 符串，服务端用其与数据库中保存的认证串比对以进行核实。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (22, 'xnpeng', '2017-06-20 11:14:04.643000', 15, null, '李和平，世界和平组织总干事。韩国籍，英国牛津大学法学博士。曾任联合国教科文组织专员，致力于消除落后国家的贫穷和疾病，在非州国家建立了33所希望学校和100所慈善医院，让以万计的失学儿童重返课堂，救治过数以万计的病人的生命。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (23, 'xnpeng', '2017-06-20 11:17:22.756000', 15, null, '臣闻朋党之说，自古有之，惟幸人君辨其君子小人而已。大凡君子与君子以同道为朋，小人与小人以同利为朋，此自然之理也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (24, 'xnpeng', '2017-06-20 11:18:06.050000', 15, null, '然臣谓小人无朋，惟君子则有之。其故何哉？小人所好者禄利也，所贪者财货也。当其同利之时，暂相党引以为朋者，伪也；及其见利而争先，或利尽而交疏，则反相贼害，虽其兄弟亲戚，不能自保。故臣谓小人无朋，其暂为朋者，伪也。君子则不然。所守者道义，所行者忠信，所惜者名节。以之修身，则同道而相益；以之事国，则同心而共济；终始如一，此君子之朋也。故为人君者，但当退小人之伪朋，用君子之真朋，则天下治矣。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (25, 'xnpeng', '2017-06-20 15:35:24.822000', 16, null, '尧之时，小人共工、驩兜等四人为一朋，君子八元、八恺十六人为一朋。舜佐尧，退四凶小人之朋，而进元、恺君子之朋，尧之天下大治。及舜自为天子，而皋、夔、稷、契等二十二人并列于朝，更相称美，更相推让，凡二十二人为一朋，而舜皆用之，天下亦大治。《书》曰：“纣有臣亿万，惟亿万心；周有臣三千，惟一心。”纣之时，亿万人各异心，可谓不为朋矣，然纣以亡国。周武王之臣，三千人为一大朋，而周用以兴。后汉献帝时，尽取天下名士囚禁之，目为党人。及黄巾贼起，汉室大乱，后方悔悟，尽解党人而释之，然已无救矣。唐之晚年，渐起朋党之论。及昭宗时，尽杀朝之名士，或投之黄河，曰：“此辈清流，可投浊流。”而唐遂亡矣。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (26, 'xnpeng', '2017-06-20 15:35:52.294000', 16, null, '夫前世之主，能使人人异心不为朋，莫如纣；能禁绝善人为朋，莫如汉献帝；能诛戮清流之朋，莫如唐昭宗之世；然皆乱亡其国。更相称美推让而不自疑，莫如舜之二十二臣，舜亦不疑而皆用之；然而后世不诮舜为二十二人朋党所欺，而称舜为聪明之圣者，以能辨君子与小人也。周武之世，举其国之臣三千人共为一朋，自古为朋之多且大，莫如周；然周用此以兴者，善人虽多而不厌也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (27, 'xnpeng', '2017-06-20 15:36:03.001000', 26, null, '嗟呼！兴亡治乱之迹，为人君者，可以鉴矣。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (28, 'xnpeng', '2017-06-23 11:49:05.283000', null, null, '臣闻老子曰：“以正理国，以奇用兵，以无事取天下。”荀卿曰：“人主者，以官人为能者也；匹夫者，以自能为能者也。”傅子曰：“士大夫分职而听，诸侯之君分土而守，三公总方而议，则天子拱己而正矣。”何以明其然耶？当尧之时，舜为司徒，契为司马，禹为司空，后稷为田畴，夔为乐正，倕为工师，伯夷为秩宗，皋陶为理官，益掌驱禽。尧不能为一焉，奚以为君，而九子者为臣，其故何也？尧知九赋之事，使九子各授其事，皆胜其任以成九功。尧遂乘成功以王天下。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (29, 'xnpeng', '2017-06-23 11:49:21.235000', null, null, '汉高帝曰：“夫运筹策于帏幄之中，决胜于千里之外，吾不如子房；镇国家，抚百姓，给饷馈，不绝粮道，吾不如萧何；连百万之军，战必胜，攻必取，吾不如韩信。三人者，皆人杰也。吾能用之，此吾所以有天下也。”

故曰：知人者，王道也；知事者，臣道也。无形者，物之君也；无端者，事之本也。鼓不预五音，而为五音主；有道者，不为五官之事，而为理事之主。君守其道，官知其事，有自来矣。

先王知其如此也，故用非其有，如己有之，通乎君道者也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (30, 'xnpeng', '2017-06-23 11:50:04.753000', null, null, '人主不通主道者，则不然。自为之，则不能任贤；不能任贤，则贤者恶之。此功名之所以伤，国家之所以危。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (31, 'xnpeng', '2017-06-23 11:50:23.227000', null, null, '汤武日而尽有夏商之财，以其地封，而天下莫敢不悦服；以其财赏，而天下皆竞劝，通乎用非其有也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (32, 'xnpeng', '2017-06-23 11:50:45.254000', null, null, '故称：设官分职，君之体也；委任责成，君之体也；好谋无倦，君之体也；宽以得众，君之体也；含垢藏疾，君之体也。君有君人之体，其臣畏而爱之，此帝王所以成业也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (33, 'xnpeng', '2017-06-23 12:11:57.790000', null, null, '臣闻料才核能，治世之要。自非圣人，谁能兼兹百行、备贯众理乎？故舜合群司，随才授位；汉述功臣，三杰异称。况非此俦，而可备责耶？

　　(《人物志》曰：夫刚略之人，不能理微，故论其大体，则宏略而高远；历纤理微，则宕往而疏越。亢厉之人，不能回挠，其论法直，则括据而公正；说变通，则否戾而不入。宽恕之人，不能速捷，论仁义，则宏详而长雅；趋时务，则迟后而不及。好奇之人，横逆而求异，造权谲，则倜傥而瑰壮；案清道，则诡常而恢迂。

　　又曰：王化之政，宜于统大，以之理小则迂；策术之政，宜于理难，以之理平则无奇；矫亢之政，宜于治侈，以之治弊则残；公刻之政，宜于纠奸，以之治边则失其众；威猛之政，宜于讨乱，以之治善则暴；伎俩之政，宜于治富，以之治贫则劳而下困。此已上皆偏材也。)');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (34, 'xnpeng', '2017-06-23 18:55:12.651000', null, null, '昔伊尹之兴土工也，强脊者使之负土，眇者使之推，伛者使之涂，各有所宜，而人性齐矣。管仲曰：“升降揖让，进退闲习，臣不如隰朋，请立以为大行；辟土聚粟，尽地之利，臣不如宁戚，请立以为司田；平原广牧，车不结辙，士不旋踵，鼓之而三军之士视死如归，臣不如王子城父，请立以为大司马；决狱折中，不杀不辜，不诬不罪，臣不如宾胥无，请立以为大理；犯君颜色，进谏必忠，不避死亡，不挠富贵，臣不如东郭牙，请立以为大谏。君若欲治国强兵，则五子者存焉；若欲霸王，则夷吾在此。”');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (35, 'xnpeng', '2017-06-24 12:45:01.630000', null, null, '黄石公曰：“使智，使勇，使贪，使愚。智者乐立其功，勇者好行其志，贪者决取其利，愚者不爱其死。因其至情而用之，此军之微权也。”

　　《淮南子》曰：“天下之物莫凶于溪毒(附子也)，然而良医橐而藏之，有所用也。麋之上山也，大章不能企；及其下也，牧竖能追之。才有修短也。胡人便于马，越人便于舟。异形殊类，易事则悖矣。”

　　魏武诏曰：“进取之士，未必能有行。有行之士，未必能进取。陈平岂笃行，苏秦岂守信耶？而陈平定汉业、苏秦济弱燕者，任其长也。”

　　由此观之，使韩信下帏，仲舒当戎，于公驰说，陆贾听讼，必无曩时之勋，而显今日之名也。故任长之道，不可不察。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (36, 'xnpeng', '2017-06-24 12:52:29.450000', null, null, '议曰：魏·桓范云：“帝王用人，度世授才。争夺之时，书策为先。分定之后，忠义为首。故晋文行咎犯之计而赏雍季之言，高祖用陈平之智而托后于周勃。”古语云：“守文之代，德高者位尊；仓卒之时，功多者赏厚。”诸葛亮曰：“老子长于养性，不可以临危难；商鞅长于理法，不可以从教化；苏、张长于驰辞，不可以结盟誓；白起长于攻取，不可以广众；子胥长于图敌，不可以谋身；尾生长于守信，不可以应变；王嘉长于遇明君，不可以事暗主；许子将长于明臧否，不可以养人物。”此任长之术者也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (37, 'xnpeng', '2017-06-24 13:35:41.673000', 1, null, '夫天下重器，王者大统，莫不劳聪明于品材，获安逸于任使。故孔子曰：“人有五仪：有庸人，有士人，有君子，有圣，有贤。审此五者，则治道毕矣。”');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (38, 'xnpeng', '2017-06-24 13:36:09.579000', 1, null, '所谓庸人者，心不存慎终之规，口不吐训格之言(格，法)；不择贤以托身，不力行以自定；见小暗大而不知所务，从物如流而不知所执。此则庸人也。');
INSERT INTO sitexa.Sweets (id, user_id, date, reply_to, direct_reply_to, text) VALUES (39, 'xnpeng', '2017-06-24 13:36:27.612000', 1, null, '所谓士人者，心有所定，计有所守。虽不能尽道术之本，必有率也(率，犹述也)；虽不能遍百善之美，必有处也。是故智不务多，务审其所知；言不务多，务审其所谓(所谓，言之要也)；行不务多，务审其所由。智既知之，言既得之(得其要也)，行既由之，则若性命形骸之不可易也。富贵不足以益，贫贱不足以损，此则士人也。');