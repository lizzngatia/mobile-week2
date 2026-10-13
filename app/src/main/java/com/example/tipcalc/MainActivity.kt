
package com.example.tipcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TipCalcApp()
            }
        }
    }
}

private data class Tab(
    val route: String,
    val label: String,
    val symbol: String
)

private val tabs = listOf(
    Tab("tip", "Tip Calc", "％"),
    Tab("examples", "Examples", "☷"),
    Tab("about", "About", "ⓘ")
)

@Composable
fun TipCalcApp() {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(
                                    navController.graph.startDestinationId
                                ) {
                                    saveState = true
                                }
                            }
                        },
                        icon = {
                            Text(tab.symbol, fontSize = 20.sp)
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "tip",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable("tip") {
                TipScreen()
            }

            composable("examples") {
                ExamplesScreen()
            }

            composable("about") {
                AboutScreen()
            }
        }
    }
}

@Composable
fun TipScreen() {
    var billText by rememberSaveable { mutableStateOf("") }
    var tipPercent by rememberSaveable {
        mutableFloatStateOf(15f)
    }

    // Safely convert text to a number.
    val parsedBill = billText.toDoubleOrNull()

    val error = when {
        billText.isBlank() -> null
        parsedBill == null ->
            "Enter a valid number, e.g. 1500."
        !parsedBill.isFinite() ->
            "Enter a finite number."
        parsedBill < 0.0 ->
            "Bill cannot be negative."
        else -> null
    }

    val validBill = parsedBill != null &&
            parsedBill.isFinite() && parsedBill >= 0.0

    val bill = if (validBill) parsedBill ?: 0.0 else 0.0
    val tip = bill * tipPercent / 100.0
    val total = bill + tip

    fun money(amount: Double): String =
        "KSh " + String.format(Locale.US, "%,.2f", amount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Tip Calculator",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = billText,
            onValueChange = { billText = it },
            label = { Text("Bill amount (KSh)") },
            placeholder = { Text("e.g. 1500") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            supportingText = {
                if (error != null) Text(error)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Tip: ${tipPercent.toInt()}%",
            style = MaterialTheme.typography.titleLarge
        )

        Slider(
            value = tipPercent,
            onValueChange = { tipPercent = it },
            valueRange = 0f..30f,
            steps = 5
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%")
            Text("15%")
            Text("30%")
        }

        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ResultRow("Bill", money(bill))
                ResultRow("Tip", money(tip))
                HorizontalDivider()
                ResultRow("Total (bill + tip)", money(total))
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                billText = ""
                tipPercent = 15f
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExamplesScreen() {
    val examples = listOf(
        "KSh 500 at 10% — tip KSh 50, total KSh 550",
        "KSh 1,000 at 10% — tip KSh 100, total KSh 1,100",
        "KSh 1,500 at 15% — tip KSh 225, total KSh 1,725",
        "KSh 2,000 at 15% — tip KSh 300, total KSh 2,300",
        "KSh 2,500 at 20% — tip KSh 500, total KSh 3,000",
        "KSh 3,000 at 20% — tip KSh 600, total KSh 3,600",
        "KSh 3,500 at 10% — tip KSh 350, total KSh 3,850",
        "KSh 4,000 at 15% — tip KSh 600, total KSh 4,600",
        "KSh 4,500 at 20% — tip KSh 900, total KSh 5,400",
        "KSh 5,000 at 10% — tip KSh 500, total KSh 5,500"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Calculation Examples",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(examples) { example ->
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        example,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "About TipCalc",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Text("TipCalc calculates tips and total bills.")

        Spacer(Modifier.height(16.dp))

        Text("Features", fontWeight = FontWeight.Bold)
        Text("• Bill input and tip slider")
        Text("• Automatic tip and total calculation")
        Text("• Empty and invalid input handling")
        Text("• State preservation during rotation")
        Text("• Three navigation screens")
        Text("• Scrollable examples using LazyColumn")
    }
}
