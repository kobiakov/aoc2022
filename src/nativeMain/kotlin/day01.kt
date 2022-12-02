fun main01() {
    // Part 1
    solveDay(1) { input ->
        readElvishLists(input)
            .map(::toRation)
            .maxOfOrNull(::totalCalories)
    }
    // Part 2
    solveDay(1) { input ->
        readElvishLists(input)
            .map(::toRation)
            .map(::totalCalories)
            .sortedDescending()
            .take(3)
            .sum()
    }
}

typealias ElvishList = List<String>
fun readElvishLists(input: String): Sequence<ElvishList> = input.lines().partitionBy(String::isBlank).asSequence()

typealias CalorieCount = Int
typealias CarriedRation = List<CalorieCount>

fun toRation(s: ElvishList): CarriedRation = s.map { it.toInt() }
fun totalCalories(ration: CarriedRation): CalorieCount = ration.sum()