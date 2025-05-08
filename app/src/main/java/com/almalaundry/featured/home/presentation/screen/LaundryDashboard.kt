package com.almalaundry.featured.home.presentation.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.home.data.models.DailyStatistic
import com.almalaundry.featured.home.data.models.MonthlyStatistic
import com.almalaundry.featured.home.presentation.viewmodels.LaundryDashboardViewModel
import com.almalaundry.featured.order.commons.OrderRoutes
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.almalaundry.shared.commons.compositional.LocalSessionManager
import com.almalaundry.shared.presentation.components.BannerHeader
import com.almalaundry.shared.presentation.components.shimmer.ShimmerPlaceholder
import com.almalaundry.shared.presentation.ui.theme.primaryLight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LaundryDashboard(
    viewModel: LaundryDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    val sessionManager = LocalSessionManager.current

    // State untuk pull-to-refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = {
            viewModel.loadStatistics() // Refresh charts and stats
            viewModel.refreshSession(sessionManager)
        }
    )

    // Ambil nama laundry dari session
    var userName by remember { mutableStateOf("User") }
    var laundryName by remember { mutableStateOf("Loading...") }
    LaunchedEffect(Unit) {
        userName = sessionManager.getSession()?.name ?: "User"
        laundryName = sessionManager.getSession()?.laundryName ?: "Laundry"
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(OrderRoutes.Create.route) }
            ) {
                Icon(Lucide.Plus, contentDescription = "Create Order")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Banner Header
                BannerHeader(
                    title = "Welcome, $userName",
                    subtitle = "Dashboard $laundryName",
                    imageResId = R.drawable.header_basic2,
                    titleAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-40).dp)
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    // Laundry Type Chart
                    item {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .padding(16.dp)
                                .width(383.dp)
                                .height(190.dp)
                                .shadow(10.dp, RoundedCornerShape(20.dp))
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            if (state.isLoading) {
                                // Shimmer untuk OrderStatsChart
                                ShimmerPlaceholder()
                            } else {
                                Text(
                                    text = "Total Laundry",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OrderStatsChart(
                                    monthlyStats = state.monthlyStats,
                                    dailyStats = state.dailyStats
                                )

                            }
                        }
                    }

                    // Revenue Chart
                    item {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .padding(16.dp)
                                .width(383.dp)
                                .height(190.dp)
                                .shadow(10.dp, RoundedCornerShape(20.dp))
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            if (state.isLoading) {
                                // Shimmer untuk RevenueChart
                                ShimmerPlaceholder()
                            } else {
                                Text(
                                    text = "Total Pendapatan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                RevenueChart(
                                    monthlyStats = state.monthlyStats,
                                    dailyStats = state.dailyStats
                                )

                            }
                        }
                    }

                    // Image Slider
                    item {
                        val filteredImages = listOfNotNull(
                            runCatching { painterResource(id = R.drawable.img_banner1) }.getOrNull(),
                            runCatching { painterResource(id = R.drawable.img_banner2) }.getOrNull(),
                            runCatching { painterResource(id = R.drawable.img_banner3) }.getOrNull()
                        )

                        val pagerState =
                            rememberPagerState(pageCount = { filteredImages.size }, initialPage = 0)

                        LaunchedEffect(pagerState) {
                            while (true) {
                                delay(2600)
                                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                                pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 16.dp), // Padding on the sides
                            pageSpacing = 16.dp, // Space between pages to ensure no overlap
                            modifier = Modifier
                                .height(152.dp)
                                .fillMaxWidth()
                        ) { page ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp)
                                    .graphicsLayer {
                                        val pageOffset = pagerState.currentPageOffsetFraction
                                        val absOffset = abs(pageOffset).coerceIn(0f, 1f)
                                        val scale = lerp(
                                            start = 0.85f,
                                            stop = 1f,
                                            fraction = 1f - absOffset
                                        )
                                        scaleX = scale
                                        scaleY = scale
                                        alpha =
                                            lerp(start = 0.5f, stop = 1f, fraction = 1f - absOffset)
                                    }
                            ) {
                                Image(
                                    painter = filteredImages[page],
                                    contentDescription = stringResource(R.string.image_slider),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Pull Refresh Indicator
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun OrderStatsChart(
    modifier: Modifier = Modifier,
    columnColor: Color = primaryLight,
    monthlyStats: List<MonthlyStatistic>,
    dailyStats: List<DailyStatistic>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    // gabungkan dan filter data
    val monthlyFromDaily = dailyStats.groupBy { it.date.substring(0, 7) }
        .map { (month, stats) ->
            MonthlyStatistic(
                month = month,
                count = stats.sumOf { it.count },
                revenue = stats.sumOf { it.revenue }
            )
        }

    val allStats = (monthlyFromDaily + monthlyStats)
        .groupBy { it.month }
        .map { (_, stats) -> stats.maxByOrNull { it.count } ?: stats.first() }

    // ambil 4 bulan terakhir (3 bulan sebelumnya + bulan ini)
    val last4Months = allStats
        .sortedByDescending { it.month }
        .takeWhile {
            val monthsDiff = monthDifference(it.month, currentMonth)
            monthsDiff in 0..3 // Ambil bulan ini + 3 bulan sebelumnya
        }
        .sortedBy { it.month } // Urutkan dari terlama ke terbaru

    // format data dan label
    val monthlyData = last4Months.map { it.count.toDouble() }
    val monthlyLabels = last4Months.map {
        try {
            val (year, month) = it.month.split("-")
            val date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(
                "$year-${
                    month.padStart(
                        2,
                        '0'
                    )
                }"
            )!!
            SimpleDateFormat("MMM yyyy", Locale("id", "ID")).format(date)
        } catch (e: Exception) {
            "Invalid"
        }
    }

    // cek data kosong
    val allDataEmpty = last4Months.isEmpty()

    LaunchedEffect(monthlyStats, dailyStats) {
        modelProducer.runTransaction {
            if (allDataEmpty) return@runTransaction

            columnSeries {
                series(monthlyData)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (allDataEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada data laundry tersedia", color = Color.Gray)
            }
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            listOf(
                                rememberLineComponent(
                                    fill = fill(columnColor),
                                    thickness = 8.dp
                                )
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            monthlyLabels.getOrNull(value.toInt()) ?: value.toString()
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun RevenueChart(
    modifier: Modifier = Modifier,
    lineColor: Color = primaryLight,
    monthlyStats: List<MonthlyStatistic>,
    dailyStats: List<DailyStatistic>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    // gabungkan data harian dan bulanan
    val monthlyFromDaily = dailyStats.groupBy { it.date.substring(0, 7) }
        .map { (month, stats) ->
            MonthlyStatistic(
                month = month,
                count = stats.size,
                revenue = stats.sumOf { it.revenue }
            )
        }
        .filter { it.revenue > 0 }

    val allStatsRevenue = (monthlyFromDaily + monthlyStats)
        .groupBy { it.month }
        .map { (_, stats) -> stats.maxByOrNull { it.revenue } ?: stats.first() }

    // ambil 4 bulan terakhir (3 bulan sebelumnya + bulan ini)
    val last4Months = allStatsRevenue
        .sortedByDescending { it.month }
        .takeWhile {
            val monthsDiff = monthDifference(it.month, currentMonth)
            monthsDiff in 0..3 // Ambil bulan ini + 3 bulan sebelumnya
        }
        .sortedBy { it.month } // Urutkan dari terlama ke terbaru

    // format data dan label
    val monthlyData = last4Months.map { it.revenue }
    val monthlyLabels = last4Months.map {
        try {
            val (year, month) = it.month.split("-")
            val date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(
                "$year-${
                    month.padStart(
                        2,
                        '0'
                    )
                }"
            )!!
            SimpleDateFormat("MMM yyyy", Locale("id", "ID")).format(date)
        } catch (e: Exception) {
            "Invalid"
        }
    }

    // cek data kosong
    val allDataEmpty = last4Months.isEmpty()

    LaunchedEffect(monthlyStats, dailyStats) {
        modelProducer.runTransaction {
            if (allDataEmpty) return@runTransaction

            lineSeries {
                series(monthlyData)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (allDataEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada data pendapatan tersedia", color = Color.Gray)
            }
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            monthlyLabels.getOrNull(value.toInt()) ?: value.toString()
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}


// untuk menghitung selisih bulan
fun monthDifference(month1: String, month2: String): Int {
    return try {
        val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val date1 = format.parse(month1)!!
        val date2 = format.parse(month2)!!

        val diff = (date2.time - date1.time).toDouble()
        val months = diff / (30.44 * 24 * 60 * 60 * 1000)
        months.roundToInt()
    } catch (e: Exception) {
        Int.MAX_VALUE
    }
}

