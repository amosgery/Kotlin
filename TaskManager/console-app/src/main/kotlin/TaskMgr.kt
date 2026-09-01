import java.io.PrintStream
import java.nio.charset.StandardCharsets

// 1. הגדרת מחלקת הנתונים עבור משימה
data class Task(
    val title: String,
    var isCompleted: Boolean = false
)

// 2. פונקציית הכניסה הראשית
fun main() {
    // הגדרת קידוד UTF-8 עבור הקונסולה להצגת עברית
    System.setOut(PrintStream(System.`out`, true, StandardCharsets.UTF_8.name()))
    
    val tasksList = mutableListOf<Task>()
    var running = true

    while (running) {
        printMenu()
        print("בחר אפשרות (1-5): ")
        val choice = readln().toIntOrNull()

        println() // שורת רווח לנוחות
        when (choice) {
            1 -> printTasks(tasksList)
            2 -> addTask(tasksList)
            3 -> markTaskAsCompleted(tasksList)
            4 -> deleteTask(tasksList)
            5 -> {
                println("להתראות!")
                running = false
            }
            else -> println("אפשרות לא חוקית, נא לנסות שוב.\n")
        }
    }
}

// מדפיסה את התפריט הראשי
fun printMenu() {
    println("=== מערכת ניהול משימות ===")
    println("1. הצגת כל המשימות")
    println("2. הוספת משימה חדשה")
    println("3. סימון משימה כ\"בוצעה\"")
    println("4. מחיקת משימה")
    println("5. יציאה מהתוכנית")
}

// 1. הצגת המשימות (שילוב הדרישה והבונוס של forEachIndexed)
fun printTasks(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        println("אין משימות ברשימה.\n")
        return
    }

    println("--- רשימת המשימות ---")
    tasks.forEachIndexed { index, task ->
        val status = if (task.isCompleted) "[V]" else "[ ]"
        println("${index + 1}. $status ${task.title}")
    }
    println()
}

// 2. הוספת משימה חדשה (שילוב הבונוס של Null Safety עם אופרטור האלביס ?:)
fun addTask(tasks: MutableList<Task>) {
    print("הכנס את שם המשימה: ")
    val rawInput = readln().trim()

    // אם המשתמש לחץ Enter בלי להקליד כלום, נקבל ערך ברירת מחדל
    val title = rawInput.ifEmpty { null } ?: "משימה ללא שם"

    tasks.add(Task(title))
    println("המשימה \"$title\" נוספה בהצלחה!\n")
}

// 3. סימון משימה כבוצעה
fun markTaskAsCompleted(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) {
        println("אין משימות לסמן.\n")
        return
    }

    printTasks(tasks)
    print("הכנס את מספר המשימה לסימון כבוצעה: ")
    val index = readln().toIntOrNull()?.minus(1)

    if (index != null && index in tasks.indices) {
        tasks[index].isCompleted = true
        println("המשימה \"${tasks[index].title}\" סומנה כבוצעה!\n")
    } else {
        println("מספר משימה לא תקין.\n")
    }
}

// 4. מחיקת משימה
fun deleteTask(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) {
        println("אין משימות למחוק.\n")
        return
    }

    printTasks(tasks)
    print("הכנס את מספר המשימה למחיקה: ")
    val index = readln().toIntOrNull()?.minus(1)

    if (index != null && index in tasks.indices) {
        val removedTask = tasks.removeAt(index)
        println("המשימה \"${removedTask.title}\" נמחקה בהצלחה!\n")
    } else {
        println("מספר משימה לא תקין.\n")
    }
}