//package com.chatguru.ai  // IMPORTANT: Must match your namespace
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.ui.Modifier
//import com.chatguru.ai.ui.navigation.ChatGuruNavHost
//import com.chatguru.ai.ui.theme.ChatGuruAITheme
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            ChatGuruAITheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    ChatGuruNavHost()
//                }
//            }
//        }
//    }
//}
package com.chatguru.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chatguru.ai.ui.navigation.ChatGuruNavHost
import com.chatguru.ai.ui.theme.ChatGuruAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGuruAITheme {
                ChatGuruNavHost()
            }
        }
    }
}
