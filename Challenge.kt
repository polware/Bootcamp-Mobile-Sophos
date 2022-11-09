import com.sun.tools.javac.Main
import kotlin.random.Random

/**
 * Created by IntelliJ IDEA
 * User: Paul H. Vargas P.
 * Date: 9/11/2022
 * Time: 3:47 p. m.
 */

var invalidInput: Boolean = false

fun main(args: Array<String>) {
        println("########## EXCERCISE 1 ##########")
        print("Type a text: ")
        var inputText = readln()
        val result = generateTextString(inputText)
        println("")
        if (!invalidInput){
                println("\n########## EXCERCISE 2 ##########")
                print("Enter the order of the array (Asc/Desc): ")
                inputText = readln()
                arrayOrdering(result, inputText)
        }
}

fun generateTextString(input: String): String {
        var randomNumbers = input
        val list = mutableListOf<String>()
        val sb = StringBuilder()
        val separator = ""
        var convert: String
        if (randomNumbers.equals("TipoA", ignoreCase = true)){
                list.add("54")
                for (i in 1..8) {
                        convert = (Random.nextInt(10)).toString()
                        list.add(convert)
                }
        }
        else if (randomNumbers.equals("TipoB", ignoreCase = true)) {
                list.add("08")
                for (i in 1..8) {
                        convert = (Random.nextInt(10)).toString()
                        list.add(convert)
                }
        }
        else {
                println("Please specify: Type A or Type B.")
                invalidInput = true
        }
        if (list.size == 9) {
                list.forEach { sb.append(it).append(separator) }
                randomNumbers = sb.removeSuffix(separator).toString()
                print("Result text string: $randomNumbers")
        }
        return randomNumbers
}

fun arrayOrdering(inputArray: String, orderType:String) {
        val array = inputArray
        val orderedList = array.toMutableList().map { it.toString() }.toMutableList()
        var aux: String = ""
        if (orderType.equals("Asc", ignoreCase = true)){
                for (i in 0 until orderedList.size) {
                        for (j in i+1 until orderedList.size) {
                                if (orderedList[i] > orderedList[j]){
                                        aux = orderedList[i]
                                        orderedList[i] = orderedList[j]
                                        orderedList[j] = aux
                                }
                        }
                }
                print("Array sorted $orderType: $orderedList")
        }
        else if (orderType.equals("Desc", ignoreCase = true)) {
                for (i in 0 until orderedList.size) {
                        for (j in i+1 until orderedList.size) {
                                if (orderedList[i] < orderedList[j]){
                                        aux = orderedList[j]
                                        orderedList[j] = orderedList[i]
                                        orderedList[i] = aux
                                }
                        }
                }
                print("Array sorted $orderType: $orderedList")
        }
        else {
                println("$orderType is an invalid parameter.")
        }
}