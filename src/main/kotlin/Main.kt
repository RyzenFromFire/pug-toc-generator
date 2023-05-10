import java.io.File
import java.util.*

const val TAB_SPACES = 2

val baseIndent = 3 * TAB_SPACES
var currentIndent = 0

fun main(args: Array<String>) {
    /**
     * Change these variables to change the file input and output target
     */
    val inFile = File("input.md")
    val outFile = File("output-toc.pug")

    val reader = inFile.inputStream().bufferedReader()
    var firstHeaderNum = 0
    var currentHeaderNum = 0
    var headerContent: String

    val sb = StringBuilder()
    sb.append(".toc-container\n")
    sb.append("  nav.toc\n")
    sb.append("    ul\n")

    var line = reader.readLine()
    while (line != null) {
//        println(line)
        if (line.isNotEmpty() && line.first() == '#') { // Header
            // Determine header size
            var i = 1
            while (line[i] == '#') {
                i++
            }
            if (line[i] == ' ') {
                // Don't increment `i` yet

                // Set header variables and indent
                if (firstHeaderNum == 0) {
                    firstHeaderNum = i
                }
                currentHeaderNum = i
                setIndent(currentHeaderNum - firstHeaderNum)

                i++ // Now increment i
                headerContent = line.substring(i).trim()

                if (currentHeaderNum > firstHeaderNum) {
                    sb.append(getIndent())
                    sb.append("ul\n")
                    incrementIndent()
                }

                sb.append(getIndent())
                sb.append("li\n")
                incrementIndent()
                sb.append(getIndent())
                sb.append(getLink(headerContent))
                decrementIndent()
            }
        }

        line = reader.readLine()
    }
    sb.append("""    svg(class="toc-marker" width="200" height="200" xmlns="http://www.w3.org/2000/svg")""")
    sb.append("\n")
    sb.append("""      path(stroke="#fe750e" stroke-width="3" fill="transparent" stroke-dasharray="0, 0, 0, 1000" stroke-linecap="round" stroke-linejoin="round" transform="translate(-0.5, -0.5)")""")
    sb.append("\n")
    println(sb.toString())
    outFile.writeText(sb.toString())
}

fun getLink(str: String): String {
    val str2 = str.replace(",", "%2C")
        .replace("&", "%26")
        .replace("/", "%2F")
        .replace("`", "")
    val words = str2.lowercase().split(" ")
    val id = "#${words.joinToString("-")}"
    return "a(href=\"$id\") $str\n"
}

fun incrementIndent() {
    currentIndent += TAB_SPACES
}

fun decrementIndent() {
    if (currentIndent >= TAB_SPACES)
        currentIndent -= TAB_SPACES
    else
        println("Error: Tried to decrement to negative indent")
}

fun setIndent(num: Int) {
    if (num < 0) {
        println("Error: target indent change is negative")
        return
    }
    var diff = currentIndent - (num * TAB_SPACES)
    if (diff == 0) {
        return
    } else if (diff > 0) {
        while (diff > 0) {
            decrementIndent()
            diff = currentIndent - (num * TAB_SPACES)
        }
    } else if (diff < 0) {
        while (diff < 0) {
            incrementIndent()
            diff = currentIndent - (num * TAB_SPACES)
        }
    }
}

fun getIndent(): String {
    val spaces = baseIndent + currentIndent
    val sb = StringBuilder()
    for (i in 0 until spaces) {
        sb.append(" ")
    }
    return sb.toString()
}