package com.almalaundry.featured.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.almalaundry.shared.presentation.ui.theme.onPrimaryContainerLight
import com.almalaundry.shared.presentation.ui.theme.onPrimaryLight
import com.almalaundry.shared.presentation.ui.theme.primaryLight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
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
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun LaundryDashboard(
    viewModel: LaundryDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    // image slider
    val pagerState = rememberPagerState(initialPage = 0)
    val imageSlider =
        listOf(
            painterResource(id = R.drawable.img_banner1),
            painterResource(id = R.drawable.img_banner2),
            painterResource(id = R.drawable.img_banner3),
        )

    // auto scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(2600)
            pagerState.animateScrollToPage(page = (pagerState.currentPage + 1) % imageSlider.size)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(onPrimaryLight),
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = onPrimaryContainerLight
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },

        // floatingActionButton
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(OrderRoutes.Create.route) }
            ) { Icon(Lucide.Plus, contentDescription = "Create Order") }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // welcome message
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .offset(y = -10.dp)
                    .background(
                        color = onPrimaryContainerLight,
                        shape =
                        RoundedCornerShape(
                            bottomStart = 45.dp,
                            bottomEnd = 45.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Welcome,", fontWeight = FontWeight.Bold, fontSize = 40.sp)
                    Text(text = "Almada Laundry!", fontWeight = FontWeight.Bold, fontSize = 40.sp)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // tipe laundry
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier =
                    Modifier
                        .padding(16.dp)
                        .width(383.dp)
                        .height(152.dp)
                        .shadow(10.dp, RoundedCornerShape(20.dp))
                        .background(onPrimaryLight, RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "Total Laundry",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LaundryTypeChart(
                        monthlyStats = state.monthlyStats,
                        dailyStats = state.dailyStats
                    )
                }

                // total pendapatan
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier =
                    Modifier
                        .padding(16.dp)
                        .width(383.dp)
                        .height(152.dp)
                        .shadow(10.dp, RoundedCornerShape(20.dp))
                        .background(onPrimaryLight, RoundedCornerShape(20.dp))
                ) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // image slider
                HorizontalPager(
                    count = imageSlider.size,
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .height(152.dp)
                        .fillMaxWidth()
                ) { page ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier =
                        Modifier.graphicsLayer {
                            val pageOffset =
                                calculateCurrentOffsetForPage(page).absoluteValue

                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                                .also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                            alpha =
                                lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                        }
                    ) {
                        Image(
                            painter = imageSlider[page],
                            contentDescription = stringResource(R.string.image_slider),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LaundryTypeChart(
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
        .map { (month, stats) -> stats.maxByOrNull { it.count } ?: stats.first() }

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
            val date = SimpleDateFormat("yyyy-MM").parse("$year-${month.padStart(2, '0')}")!!
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
                            listOf(rememberLineComponent(fill = fill(columnColor), thickness = 8.dp))
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = object : CartesianValueFormatter {
                            override fun format(
                                context: CartesianMeasuringContext,
                                value: Double,
                                verticalAxisPosition: Axis.Position.Vertical?
                            ): String {
                                return monthlyLabels.getOrNull(value.toInt()) ?: value.toString()
                            }
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

    val allStats = (monthlyFromDaily + monthlyStats)
        .groupBy { it.month }
        .map { (month, stats) -> stats.maxByOrNull { it.revenue } ?: stats.first() }

    // ambil 4 bulan terakhir (3 bulan sebelumnya + bulan ini)
    val last4Months = allStats
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
            val date = SimpleDateFormat("yyyy-MM").parse("$year-${month.padStart(2, '0')}")!!
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
                                    fill(ShaderProvider.verticalGradient(
                                        arrayOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
                                    ))
                                )
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = object : CartesianValueFormatter {
                            override fun format(
                                context: CartesianMeasuringContext,
                                value: Double,
                                verticalAxisPosition: Axis.Position.Vertical?
                            ): String {
                                return monthlyLabels.getOrNull(value.toInt()) ?: value.toString()
                            }
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
        val format = SimpleDateFormat("yyyy-MM")
        val date1 = format.parse(month1)!!
        val date2 = format.parse(month2)!!

        val diff = (date2.time - date1.time).toDouble()
        val months = diff / (30.44 * 24 * 60 * 60 * 1000)
        months.roundToInt()
    } catch (e: Exception) {
        Int.MAX_VALUE
    }
}

