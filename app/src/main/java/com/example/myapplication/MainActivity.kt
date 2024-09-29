package com.example.myapplication
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.painterResource
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavDeepLinkRequest.Builder.Companion.fromUri






class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // 實例化 NavController
                val navController = rememberNavController()
                // 創建屏幕對象列表
                val items = listOf(
                    Screen.Home,
                    Screen.PDF,
                    Screen.YouTube
                )
                // 使用 Scaffold 創建一個具有 BottomNavigation 的基本布局
                Scaffold(
                    bottomBar = { BottomNavigationBar(items, navController) }
                ) { innerPadding ->
                    // 使用 innerPadding 來調整 Column 的內容位置
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController, startDestination = Screen.Home.route) {
                            composable(Screen.Home.route) { HomeScreen(navController) }
                            composable(Screen.PDF.route) { PdfContentScreen(navController) }
                            composable(Screen.YouTube.route) { YoutubeContentScreen(navController) }
                            // 注意，QuestionnaireContentScreen 沒有添加到導航中
                        }
                    }
                }
            }
        }
    }
}

// 這裡是 Screen 類的代碼，它代表了每個導航項目
sealed class Screen(val route: String, val icon: Int, val label: String) {
    object Home : Screen("home", R.drawable.survey, "首頁和問卷")
    object PDF : Screen("pdf", R.drawable.family, "教養資源")
    object YouTube : Screen("youtube", R.drawable.youtube, "技術影片")
}
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current // 获取当前 Composable 的 Context

    Box(modifier = Modifier.fillMaxSize()) {
        // 首先，放置背景图片
        Image(
            painter = painterResource(id = R.drawable.background), // 替换为您的背景图片资源ID
            contentDescription = "Background",
            contentScale = ContentScale.Crop, // 根据需求调整缩放模式
            modifier = Modifier.fillMaxSize()
        )

        // 使用Box作为最外层的布局容器来居中显示Column
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // 使用LazyColumn来允许内容滚动
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 添加Greeting部分
                item {
                    Greeting()
                }
                // 添加分割线
                item {
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                }
                // 添加问题调查表单的按钮
                items(listOf(
                    "https://forms.gle/CEyU8hZA5qNNF6uT9" to "親職效能量表單",
                    "https://forms.gle/fWaLPoswY7e636VG6" to "問題諮詢表單",
                    "https://forms.gle/dGMktdfMsBisuEKT9" to "教養勝任感量表單"
                )) { (url, label) ->
                    Button(onClick = { openFormLink(context, url) }) {
                        Text(label)
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // 添加间隔
                }
            }
        }
    }
}




// 一个用于启动浏览器 Intent 的辅助函数
fun openFormLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Composable
fun ScreenIcon(screen: Screen) {
    Icon(
        painter = painterResource(id = screen.icon),
        contentDescription = null, // Provide a proper content description.
        modifier = Modifier.size(48.dp), // This sets the icon size to 48.dp.
        tint = Color.Unspecified // This ensures that the icon's original color is used.
    )
}

// 這裡是 BottomNavigationBar Composable 函數的代碼
@Composable
fun BottomNavigationBar(items: List<Screen>, navController: NavController) {
    BottomNavigation (backgroundColor = Color.White // 将背景颜色设置为白色
        //
        ){

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            BottomNavigationItem(
                icon = { ScreenIcon(screen = screen) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    // 當選中時，導航到對應的屏幕
                    navController.navigate(screen.route) {
                        // 重置回到起始目的地，避免導航回棧的累積
                        popUpTo(navController.graph.startDestinationId)
                        // 當重新選擇時避免在同一目的地上創建多個實例
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Text(
                text = "建構內容的目標為:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(listOf(
            "促進教養的勝任感",
            "促進情緒處理的教養知識",
            "促進尋求心理健康資源的知識",
            "減少教養壓力",
            "促進溝通 (Johnston & Mash, 1989)",
            "問與答 (Liet al., 2021)"
        )) { itemText ->
            Text(text = "($itemText)", modifier = Modifier.padding(bottom = 4.dp))
        }
        item {
            Text(
                text = "程式及網頁大綱：",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(listOf(
            "評估：主要照顧者教養型態、勝任感及身心狀態評估，兒童情緒狀態及氣質評估(表3)",
            "情緒發展及處理知識：情境舉例處理",
            "心理健康資源",
            "教養資源",
            "親子溝通互動促進知識",
            "問與答：包括問題討論：參考問題及刺激回應之例子，設計問題分類及回應內容(Kitsao-Wekulo, 2021)及簡訊回應(Kang, 2020)",
            "影片參考專區"
        )) { itemText ->
            Text(text = itemText, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}
data class PdfDocument(val title: String, val url: String)
@Composable
fun PdfContentScreen(navController: NavController) {

    // PDF 文档的列表
    val pdfDocuments = listOf(
        PdfDocument("教養資源", "https://drive.google.com/file/d/1FcrI8wpch-maQ8DeHsJjbYoqJaQ7-v4n/view"),
        PdfDocument("父母親如何照顧自己", "https://drive.google.com/file/d/1_LsSPLRswHk39iWhjNIB7l-g9AN8_6bL/view"),
        PdfDocument("嬰幼兒社會與情緒發展里程碑", "https://drive.google.com/file/d/1yT7cm3R4gehi5vNdp2jw_yNjjvt7Z_xl/view"),
        PdfDocument("幼兒情緒教養相關文獻整理", "https://drive.google.com/file/d/1WAMk317s65OMOUe1doEZlj2MJfUYqdDM/view"),
        PdfDocument("情緒發展及處理知識：情境舉例處理", "https://drive.google.com/file/d/1_-HguH6Ec7dwlWq-gg3HkUsAudrPwxHC/view"),
        PdfDocument("心理健康資源", "https://drive.google.com/file/d/1Xj7vDhjXTKIMbWVPZiyjs0ONpzY1DXBy/view"),
        PdfDocument("主要照顧者勝任感", "https://drive.google.com/file/d/1SnbZ7JbMGwYLzVo2pGVipokm8e-bWtoj/view"),
        PdfDocument("主要照顧者教養型態", "https://drive.google.com/file/d/1nP3W2zwKGPL86BwO0i-3bFuQLeglRgQ0/view"),
        PdfDocument("兒童氣質類型", "https://drive.google.com/file/d/1wvz32umfVYWgB89-Cgesx1PV1duD20LB/view"),
        PdfDocument("主要照顧者身心狀態評估", "https://drive.google.com/file/d/15bWIw7AIqPLhV1-iJQD5tlRgvE0YODMZ/view")
    )


    // 简单列表展示
    LazyColumn {
        items(pdfDocuments) { pdf ->
            PdfItem(pdf)
        }
    }
}

@Composable
fun PdfItem(pdf: PdfDocument) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { openPdfLink(context, pdf.url) }
        .padding(16.dp)
    ) {
        Text(pdf.title, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(pdf.url, color = Color.Blue) // 设置为蓝色
        Divider()
    }
}

// 打开外部浏览器查看 PDF 的辅助函数
fun openPdfLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

data class Video(val title: String, val url: String)

@Composable
fun YoutubeContentScreen(navController: NavController) {
    // YouTube视频的列表

    val videos = listOf(
        Video("追蹤", "https://youtu.be/zKSS-MY8ze8?si=UyqcoE7YJ2ogs2tx"),
        Video("1重述", "https://youtu.be/5Jn9b2ZJoP8?si=T8lPcn5V5TqP3WDf"),
        Video("2設限", "https://youtu.be/5fFhwZJmQGc?si=CtLkmyvbxNbQq-Hp"),
        Video("反應情感", "https://youtu.be/ix5L1Xo5h98?si=V9IT_GF2z4zIjpMO"),
        Video("回歸責任給兒童", "https://youtu.be/5s4FRO7UJm0?si=7f_5UqZfd1rGASVt"),
        Video("1個人化問題", "https://youtu.be/eBL5W_mBbu4?si=S0_mVu1PsAOhqTFh"),
        Video("2關係性問題", "https://youtu.be/TxWFTwqK-PY?si=Qj9705Zgz1h_Qzox"),
        Video("3歷程性回答", "https://youtu.be/k4lxICiodRE?si=zaV8oIJgQ2aebXhp"),
        Video("4兒童回答問題", "https://youtu.be/0kOAyzsgoy4?si=ouTcBhB4P6jbkmiq"),
        Video("總從自我介紹到所有技術混合使用", "https://youtu.be/UiwMdUS78os?si=F0HLdyTcDPXmW796"),
        Video("兒童心臟基金會「遊出我心途」計劃 - 遊戲治療師專訪", "https://youtu.be/gHgsQI7Mkq8?si=ySaNgyhLGSxJwKoq"),
        Video("林麗玲遊戲治療/九個治療性口語回應", "https://youtu.be/y8LH69J5U6s?si=fx8Cngt03K0T3k3N"),
        Video("親子溝通的技巧，最大關鍵，其實只有兩個字__", "https://youtu.be/rAZxbCqnOUk?si=MoVwzItYPDzlnaNV"),
        Video("為什麼孩子常常講不知道、沒事、還好？因為他不想跟你溝通", "https://youtu.be/J7BMcAvfplA?si=kcUwtUO5p95SlDQV")
    )

    // 列表展示
    LazyColumn {
        items(videos) { video ->
            VideoItem(video)
        }
    }
}

@Composable
fun VideoItem(video: Video) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { openVideoLink(context, video.url) }
        .padding(16.dp)
    ) {
        Text(video.title, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(video.url, color = Color.Blue) // 设置为蓝色
        Divider()
    }
}

// 打开外部浏览器查看视频的辅助函数
fun openVideoLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}


@Composable
fun QuestionnaireContentScreen(navController: NavController) {
    Column {
        Text(text = "這裡將顯示問卷表單連結")
        Button(onClick = { navController.navigate("pdf") }) {
            Text(text = "返回PDF页面")
        }
    }
}
