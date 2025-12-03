# 获取电池信息示例（Kotlin + Jetpack Compose）

下面的示例展示了如何在 Android 上获取电池电量、当前容量和预估满电容量(当前容量/当前百分比)，并在 Compose 中显示：

```kotlin
import android.content.Context
import android.os.BatteryManager
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryInfoView(context: Context) {
    val batteryInfo = getBatteryInfo(context)

    Text(
        text = "Battery: ${batteryInfo.first}% (${batteryInfo.second} mAh)",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF4CAF50)
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Total Capacity: ${batteryInfo.third} mAh",
        fontSize = 14.sp,
        color = Color.Gray
    )
}

private fun getBatteryInfo(context: Context): Triple<Int, Int, Int> {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val percentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    
    // 获取当前电量（微安时）并转换为毫安时
    val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    val mAh = chargeCounter / 1000
    
    // 计算总容量：当前毫安时 / 百分比 * 100
    val totalCapacity = if (percentage > 0) (mAh * 100) / percentage else 0
    
    return Triple(percentage, mAh, totalCapacity)
}
```
