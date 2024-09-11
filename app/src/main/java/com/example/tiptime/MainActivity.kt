package com.example.tiptime

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Switch
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipTimeTheme {
                TipTimeLayout()
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        //represents state of text box, mutableStateOf allows change based on user input
        var amountInput by remember {mutableStateOf("")}
        // ?: Elvis operator -- if input is null, set value to 0.0
        val amount = amountInput.toDoubleOrNull() ?: 0.0
        var tipInput by remember { mutableStateOf("")}
        val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
        //can also do mutableStateOf for booleans
        var roundUp by remember {mutableStateOf(false)}

        val tip = calculateTip(amount, tipPercent, roundUp)
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 40.dp)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.calculate_tip),
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 40.dp)
                    .align(alignment = Alignment.Start)
            )
            EditNumberField(
                //amountInput is a state of EditNumberField, need to do state hoisting
                //state hoisting: lift state from composable, makes stateless + usable in other composables
                label = R.string.bill_amount,
                leadingIcon = R.drawable.money,
                //make keyboard a number keyboard to input digits only
                //imeAction determines what the action button looks like (can do send, next, search, etc)
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                value = amountInput,
                onValueChange = {amountInput = it},
                modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth()
            )
            //make another textfield for a custom tip value
            EditNumberField(
                label = R.string.how_was_the_service,
                leadingIcon = R.drawable.percent,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done //different keyboards for different fields
                ),
                value = tipInput,
                onValueChange = {tipInput = it},
                modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth()
            )
            RoundTheTipRow(
                roundUp = roundUp,
                onRoundUpChanged = {roundUp = it},
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = stringResource(R.string.tip_amount, tip),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun EditNumberField(@StringRes label: Int, @DrawableRes leadingIcon: Int, keyboardOptions: KeyboardOptions, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = value,
        leadingIcon = {Icon(painterResource(id = leadingIcon), null)},
        onValueChange = onValueChange, //use parameter
        label = { Text(stringResource(label))},
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

@Composable
fun RoundTheTipRow(roundUp: Boolean, onRoundUpChanged: (Boolean) -> Unit, modifier: Modifier = Modifier){
    //make a switch to toggle within its own row
    Row(
        modifier = modifier.fillMaxWidth().size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            modifier = modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
        )
    }
}


/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
// changed tipPercent to 20 bc you should always tip 20% :)
private fun calculateTip(amount: Double, tipPercent: Double, roundUp: Boolean): String {
    //calculates tip: multiplies bill amount by tip percentage over 100
    var tip = tipPercent / 100 * amount
    if(roundUp) {
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
