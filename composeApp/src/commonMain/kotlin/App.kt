import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ktor.Client
import ktor.RocketLaunch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(countries: List<Country> = countries()) {
    val client = Client()
    MaterialTheme {
        var showCountries by remember { mutableStateOf(false) }
        var timeAtLocation by remember { mutableStateOf("No location selected") }
        var responseData: List<RocketLaunch> by remember { mutableStateOf(emptyList()) }

        LaunchedEffect(true) {
            println("JWH- start")
            delay(1000)
            responseData = client.request()
            println("JWH- end")
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                timeAtLocation,
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )
            Row(modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
                DropdownMenu(
                    expanded = showCountries,
                    onDismissRequest = { showCountries = false }
                ) {
                    countries.forEach { (name, zone, image) ->
                        DropdownMenuItem(
                            onClick = {
                                timeAtLocation = currentTimeAt(name, zone)
                                showCountries = false
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource(image),
                                    modifier = Modifier.size(50.dp).padding(end = 10.dp),
                                    contentDescription = "$name flag"
                                )
                                Text(name)
                            }
                        }
                    }
                }
            }

            Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                onClick = { showCountries = !showCountries }) {
                Text("Select Location")
            }

            Divider()

            LazyColumn {
                items(responseData) { launch ->
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        backgroundColor = Color.Yellow
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(10.dp)

                        ) {
                            Text(launch.missionName)
                            Text(launch.launchYear.toString())
                            Text(launch.details ?: "")

                            if (launch.launchSuccess == null) {
                                Text("No data")
                            } else {
                                if (launch.launchSuccess) {
                                    Text("SUCCESS")
                                } else {
                                    Text("FAIL")
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

fun currentTimeAt(location: String, zone: TimeZone): String {
    fun LocalTime.formatted() = "$hour:$minute:$second"

    val time = Clock.System.now()
    val localTime = time.toLocalDateTime(zone).time

    return "The time in $location is ${localTime.formatted()}"
}

data class Country(
    val name: String,
    val zone: TimeZone,
    val img: String
)

fun countries() = listOf(
    Country("Korea", TimeZone.of("Asia/Seoul"), "kr.png"),
    Country("Japan", TimeZone.of("Asia/Tokyo"), "jp.png"),
    Country("France", TimeZone.of("Europe/Paris"), "fr.png"),
    Country("Mexico", TimeZone.of("America/Mexico_City"), "mx.png"),
    Country("Indonesia", TimeZone.of("Asia/Jakarta"), "id.png"),
    Country("Egypt", TimeZone.of("Africa/Cairo"), "eg.png")
)
