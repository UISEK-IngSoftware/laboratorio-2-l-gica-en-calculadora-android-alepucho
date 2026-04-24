package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

data class CalculatorState (
    val display: String = "0"
)
sealed class CalculatorEvent{
    data class Number (val number : String) : CalculatorEvent()
    data class Operator (val operator : String) : CalculatorEvent()
    object Delete: CalculatorEvent();
    object AllClear: CalculatorEvent();
    object Calculate: CalculatorEvent();
    object Decimal: CalculatorEvent();
}

class CalculatorViewModel : ViewModel(){
    private var num1: String ="";
    private var num2: String ="";
    private var operator: String? = null;

    var state by mutableStateOf(CalculatorState())
    private set

    fun onEvent (event: CalculatorEvent){
        when (event){
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Delete -> clearLast()
            is CalculatorEvent.Calculate-> performCalculation()

        }
    }

    private fun enterNumber(num: String){
        if (operator== null){
            num1+=num
            state=state.copy(num1)
        }else{
            num2+=num
            state=state.copy(num2)
        }
    }

    private fun enterDecimal(){
        val currentNumber = if (operator==null) num1 else num2
        if (!currentNumber.contains(".")){
            num1+="."
            state=state.copy(num1)
        }else{
            num2+="."
            state=state.copy(num2)
        }
    }

    private fun enterOperator(operator: String){
        if (num1.isNotBlank()){
            performCalculation()
            this.operator=operator
        }
    }

    private fun clearAll(){
        num1=""
        num2=""
        operator=null
        state=state.copy("0")
    }

    private fun clearLast(){
        if (operator==null){
            num1=num1.dropLast(1)
            state=state.copy(num1)
        }else{
            num2=num2.dropLast(1)
            state=state.copy(num2)
        }
    }

    /**
     * Aquí ya calculamos por fin
     */
    private fun performCalculation(){
        val num1=num1.toDoubleOrNull()
        val num2=num2.toDoubleOrNull()
        if (num1 != null && num2 != null && operator != null){
            val result=when(operator){
                "+"-> num1+num2
                "-"-> num1-num2
                "÷"-> if (num2!=0.0) num1/num2 else Double.NaN
                "×"-> num1*num2
                else -> 0.0
            }
            clearAll()
            val resultStr=if(result.isNaN())"Error" else result.toString().removeSuffix(".0")
            this.num1=if(!result.isNaN())resultStr else ""
            state=state.copy(resultStr)
        }
    }
}