package com.example.composeintegration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cv = findViewById<ComposeView>(R.id.cv_compose)
        cv.apply {
            // 뷰가 윈도우에서 분리될 때마다 컴포지션을 삭제하지 않고, 라이프사이클 종료시 컴포지션 삭제하는 옵션
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LiveDataTest()
            }
        }
    }

    // 기본 텍스트 띄워보기
    @Composable
    private fun StateText(text: String) {
        Text(text = "Current State is $text", color = Color.Red, fontSize = 30.sp)
    }

    // ViewModel 과 함께 사용 (StateFlow, LiveData)
    @Composable
    private fun StateFlowTest(
        viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ) {
        val uiState by viewModel.stateFlow.collectAsState()
        when(uiState) {
            is ScreenState.Loading -> { StateText(("Loading")) }
            is ScreenState.Success -> { StateText("Success") }
            is ScreenState.Error -> { StateText("Error")}
        }
    }
    @Composable
    private fun LiveDataTest(
        viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ) {
        val uiState by viewModel.liveData.observeAsState()
        when(uiState) {
            is ScreenState.Loading -> { StateText(("Loading")) }
            is ScreenState.Success -> { StateText("Success") }
            is ScreenState.Error -> { StateText("Error")}
            else -> {}
        }
    }


    // 아래는 연습용, 무시하셔도 됩니다.
    data class Message(val author: String, val body: String)
    @Composable
    fun MessageCard(msg: Message) {

        // Add padding around our message
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
            )

            // Add a horizontal space between the image and the column
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = msg.author, color = Color.White)
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = msg.body, color = Color.White)
            }
        }
    }




}