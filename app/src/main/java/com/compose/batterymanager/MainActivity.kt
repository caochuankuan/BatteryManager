package com.compose.batterymanager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.batterymanager.ui.theme.BatteryManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryManagerTheme {
                BatteryManagerScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryManagerScreen() {
    val context = LocalContext.current
    val batteryParameters = remember { getBatteryParameters(context) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "电池管理器",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Battery Manager",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(batteryParameters.chunked(2)) { parameterPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    parameterPair.forEach { parameter ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 顶部圆形指示器
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getParameterInitial(parameter.name),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 参数名
                                Text(
                                    text = parameter.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // 参数值
                                Text(
                                    text = parameter.value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                
                                Spacer(modifier = Modifier.height(2.dp))
                                
                                // 描述
                                Text(
                                    text = parameter.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    minLines = 2,
                                    maxLines = 2,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                    
                    // 如果是奇数个参数，最后一行只有一个，需要填充空白
                    if (parameterPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun getParameterInitial(parameterName: String): String {
    return when {
        parameterName.contains("电量") -> "电"
        parameterName.contains("等级") -> "级"
        parameterName.contains("刻度") -> "度"
        parameterName.contains("充电") -> "充"
        parameterName.contains("健康") -> "健"
        parameterName.contains("电源") -> "源"
        parameterName.contains("电压") -> "压"
        parameterName.contains("温度") -> "温"
        parameterName.contains("技术") -> "技"
        parameterName.contains("存在") -> "存"
        parameterName.contains("循环") -> "循"
        parameterName.contains("容量") -> "容"
        parameterName.contains("计数器") -> "数"
        parameterName.contains("电流") -> "流"
        parameterName.contains("能量") -> "能"
        else -> "信"
    }
}

data class BatteryParameter(
    val name: String,
    val value: String,
    val description: String
)

fun getBatteryParameters(context: Context): List<BatteryParameter> {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val percentage = if (level >= 0 && scale > 0) (level * 100 / scale) else -1
    
    return listOf(
        BatteryParameter(
            "循环次数", 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                "${batteryStatus?.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1) ?: -1}"
            } else {
                "需要 API 34+"
            }, 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                "电池充电循环次数"
            } else {
                "电池充电循环次数 (需要 Android 14 或更高版本)"
            }
        ),
        BatteryParameter("电池电量", "$percentage%", "当前电池电量百分比"),
        BatteryParameter("电量等级", "$level", "当前电池电量等级"),
        BatteryParameter("电量刻度", "$scale", "电池电量最大刻度"),
        BatteryParameter("充电状态", getStatusText(batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1), "当前充电状态"),
        BatteryParameter("电池健康", getHealthText(batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1), "电池健康状况"),
        BatteryParameter("电源类型", getPluggedText(batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1), "当前电源连接类型"),
        BatteryParameter("电池电压", "${batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1} mV", "电池电压（毫伏）"),
        BatteryParameter("电池温度", "${(batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1) / 10.0}°C", "电池温度（摄氏度）"),
        BatteryParameter("电池技术", batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "未知", "电池技术类型"),
        BatteryParameter("电池存在", if (batteryStatus?.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) == true) "是" else "否", "电池是否存在"),
        BatteryParameter("设计容量", "${batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)}%", "电池设计容量百分比"),
        BatteryParameter("充电计数器", "${batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)} μAh", "电池充电计数器（微安时）"),
        BatteryParameter("瞬时电流", "${batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)} μA", "瞬时电池电流（微安）"),
        BatteryParameter("平均电流", "${batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)} μA", "平均电池电流（微安）"),
        BatteryParameter("能量计数器", "${batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)} nWh", "电池能量计数器（纳瓦时）")
    )
}

fun getStatusText(status: Int): String {
    return when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "充电中"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "放电中"
        BatteryManager.BATTERY_STATUS_FULL -> "已充满"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "未充电"
        BatteryManager.BATTERY_STATUS_UNKNOWN -> "未知"
        else -> "未知($status)"
    }
}

fun getHealthText(health: Int): String {
    return when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "良好"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "过热"
        BatteryManager.BATTERY_HEALTH_DEAD -> "损坏"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "过压"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "未指定故障"
        BatteryManager.BATTERY_HEALTH_COLD -> "过冷"
        BatteryManager.BATTERY_HEALTH_UNKNOWN -> "未知"
        else -> "未知($health)"
    }
}

fun getPluggedText(plugged: Int): String {
    return when (plugged) {
        0 -> "电池供电"
        BatteryManager.BATTERY_PLUGGED_AC -> "交流充电器"
        BatteryManager.BATTERY_PLUGGED_USB -> "USB充电"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "无线充电"
        BatteryManager.BATTERY_PLUGGED_DOCK -> "底座充电"
        else -> "未知($plugged)"
    }
}