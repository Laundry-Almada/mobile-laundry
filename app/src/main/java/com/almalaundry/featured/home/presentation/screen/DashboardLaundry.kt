package com.almalaundry.featured.home.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almalaundry.R
import com.almalaundry.featured.home.presentation.viewmodels.HomeViewModel
import com.almalaundry.shared.commons.compositional.LocalNavController
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import com.google.accompanist.pager.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import androidx.compose.material3.Card
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.almalaundry.shared.presentation.ui.theme.onBackgroundDarkMediumContrast
import com.almalaundry.shared.presentation.ui.theme.onPrimaryContainerLight
import com.almalaundry.shared.presentation.ui.theme.onPrimaryLight
import com.almalaundry.shared.presentation.ui.theme.primaryContainerDarkMediumContrast
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator

@Composable
fun DashboardUser(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    //image slider
    val pagerState = rememberPagerState(initialPage = 0)
    val imageSlider = listOf(
        painterResource(id = R.drawable.img_banner1),
        painterResource(id = R.drawable.img_banner2),
        painterResource(id = R.drawable.img_banner3),
    )

    //font nunito
    val NunitoFont = FontFamily(
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_bold, FontWeight.Bold)
    )

    //auto scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(2600)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % imageSlider.size
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(onPrimaryLight),

        topBar = {
           CenterAlignedTopAppBar(
               title = {},
               colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                   containerColor = onPrimaryContainerLight
               ),
               windowInsets = WindowInsets(0.dp)
           )
        }
    ){ paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //welcome message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .offset(y = -10.dp)
                    .background(
                        color = onPrimaryContainerLight,
                        shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp)
                    ),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Welcome,Almada Laundry!",
                    fontFamily = NunitoFont,
                    fontWeight =FontWeight.Bold,
                    fontSize = 46.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                //row total tipe laundry dan total layanan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    //total type laundry
                    Column(
                        modifier = Modifier.weight(1f)
                    ){

                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier =Modifier
                                .width(184.dp)
                                .height(152.dp)
                                .background(primaryContainerDarkMediumContrast, RoundedCornerShape(20.dp))
                        ){
                            Text(
                                text = "Total Tipe Laundry",
                                fontFamily = NunitoFont,
                                fontWeight =FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            //chart
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    //total layanan
                    Column(
                        modifier = Modifier.weight(1f)
                    ){

                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier =Modifier
                                .width(184.dp)
                                .height(152.dp)
                                .background(primaryContainerDarkMediumContrast, RoundedCornerShape(20.dp))
                        ){
                            Text(
                                text = "Total Layanan",
                                fontFamily = NunitoFont,
                                fontWeight =FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            //chart
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Pendapatan Section

                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .width(383.dp)
                        .height(152.dp)
                        .background(primaryContainerDarkMediumContrast, RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "Total Pendapatan",
                        fontFamily = NunitoFont,
                        fontWeight =FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    //chart
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Spacer to push the image slider to the bottom
                Spacer(modifier = Modifier.weight(1f))

                //image slider
                HorizontalPager(
                    count = imageSlider.size,
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .height(233.dp)
                        .fillMaxWidth()
                ){page ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.graphicsLayer{
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                    ){
                        Image(
                            painter = imageSlider[page],
                            contentDescription = stringResource(R.string.image_slider),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                //indikator slider
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )

                //ikon add
                Box(
                    modifier = Modifier.fillMaxSize()
                ){
                    FloatingActionButton(
                        onClick = {/*TODO:*/},
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(onBackgroundDarkMediumContrast)
                    ){
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Add",
                            tint = Color.Black,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}
