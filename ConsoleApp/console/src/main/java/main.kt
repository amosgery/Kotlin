import java.io.PrintStream
import java.nio.charset.StandardCharsets

fun main() {
    // הגדרת קידוד UTF-8 עבור הקונסולה להצגת עברית
    System.setOut(PrintStream(System.`out`, true, StandardCharsets.UTF_8.name()))

    println("=== אפליקציית קונסול הופעלה ===")

    print("הכנס את שמך: ")
    val name = readln()

    println("שלום $name, הבנייה של המודול הצליחה!")
}