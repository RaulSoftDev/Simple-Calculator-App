package com.underdoggameworks.calculator

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.NumberFormatException
import kotlin.properties.Delegates

class MainActivity : ComponentActivity() {
    private var currentInput : MutableList<String> = mutableListOf()
    private lateinit var resultText : TextView

    private var number1 : Double? = null
    private var number2 : Double? = null

    private var hiddenResult by Delegates.notNull<Double>()
    private lateinit var operation : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCalc()
    }

    private fun initCalc(){
        initAds()
        resultText = findViewById(R.id.result)
        resultText.text = "0"
        hiddenResult = 0.0
        operation = ""
    }

    private fun initAds(){
        MobileAds.initialize(this)

        var adView = AdView(this)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        adView = findViewById(R.id.banner)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    fun onButtonClick(view : View){
        when(view.tag){
            "C" -> clearCalcText()
            "del" -> setPosNegNumber()
            "+" -> {
                setCurrentOp("+")
            }
            "-" -> {
                setCurrentOp("-")
            }
            "x" -> {
                setCurrentOp("*")
            }
            "/" -> {
                setCurrentOp("/")
            }
            "=" -> resolveOperation(view)
            "." -> setDotText(view)
            else -> setCalcText(view)
        }
    }

    private fun setCalcText(view : View){
        saveInput(view)
    }

    private fun setDotText(view: View){
        if(!currentInput.contains(".")){
            saveInput(view)
        }
    }

    private fun saveInput(view: View){
        currentInput.add(view.tag.toString())
        setScreenText()
    }

    private fun setScreenText(){
        //If zeroes on left side, delete them
        if(currentInput.lastIndex >= 1 && !currentInput.contains(".")){
            hideLeadingZeros(currentInput.joinToString(separator = ""))
        }
        else resultText.text = currentInput.joinToString(separator = "")
    }

    private fun clearCalcText(){
        currentInput.clear()
        number1 = null
        number2 = null
        resultText.text = "0"
    }

    private fun setCurrentOp(type : String){
        if(currentInput.isEmpty()) currentInput.add("0")

        if(operation == ""){
            operation = type
            saveNumber()
        }
        else{
            saveNumber()
            operation = type
        }
    }

    private fun saveNumber(){
        if(number1 == null){
            number1 = currentInput.joinToString(separator = "").toDouble()
            hiddenResult = number1 as Double
        }
        else if(number2 == null){
            number2 = currentInput.joinToString(separator = "").toDouble()
            println("$number1 $operation $number2")
            hiddenResult = setOperation(number1 as Double, number2 as Double)
            number1 = hiddenResult
            number2 = null
        }
        currentInput.clear()
        resultText.text = "0"
        //currentInput.add(resultText.text.toString())
        println("$currentInput ; $number1")
    }

    //Operations
    private fun calcOperation(x : Double, y : Double, fn : (Double, Double) -> Double) : Double{
        return if(y == 0.0) x
        else fn(x, y)
    }

    private fun setOperation(n1: Double, n2 : Double) : Double {
        when(operation){
            "+" -> return calcOperation(n1, n2, ::add)
            "-" -> return calcOperation(n1, n2, ::subtract)
            "*" -> return calcOperation(n1, n2, ::multiply)
            "/" -> return calcOperation(n1, n2, ::divide)
            else -> return n1
        }
    }

    private fun resolveOperation(view: View){
        if(number1 == null && number2 == null){
            resultText.text = currentInput.joinToString(separator = "")
        }
        else if(operation != ""){
            var currentResult : Double = setOperation(number1 as Double, currentInput.joinToString(separator = "").toDouble())
            resultText.text = numberWithFormat(currentResult)
            hideLeadingZeros(resultText.text as String)
            operation = ""
            number1 = resultText.text.toString().toDouble()
            currentInput.clear()
        }
    }

    private fun hideLeadingZeros(text : String){
       text.replaceFirst("^0+(?!$)".toRegex(), "")
        resultText.text = text
    }

    private fun add(x: Double, y: Double) : Double {return x + y}
    private fun subtract(x: Double, y: Double) : Double {return x - y}
    private fun multiply(x: Double, y: Double) : Double {return x * y}
    private fun divide(x: Double, y: Double) : Double {return x / y}

    //Format
    private fun numberWithFormat(input : Double) : String{
        var format = DecimalFormat("0.###")
        format.roundingMode = RoundingMode.DOWN
        return format.format(input).replace(",",".")
    }

    //Change positive-negative number
    private fun setPosNegNumber(){
        var numberToChange : Double

        if(currentInput.isNotEmpty()){
            numberToChange = currentInput.joinToString(separator = "").toDouble()
            numberToChange *= -1
            currentInput.clear()
            currentInput.add(numberToChange.toString())
            resultText.text = numberWithFormat(currentInput.joinToString(separator = "").toDouble())
        }
        else{
            numberToChange = resultText.text.toString().toDouble()
            numberToChange *= -1
            currentInput.add(numberToChange.toString())
            resultText.text = numberWithFormat(currentInput.joinToString(separator = "").toDouble())
            number1 = null
        }
    }
}