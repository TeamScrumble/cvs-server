package cvs.crawler

enum class CvsTarget {
    CU, EMART_24, GS25, SEVEN_ELEVEN;

    companion object {
        operator fun invoke(target: String) = entries.find { it.name == target }
    }
}